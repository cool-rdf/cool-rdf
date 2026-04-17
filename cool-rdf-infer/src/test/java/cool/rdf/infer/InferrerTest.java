/*
 * Copyright Cool RDF project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.infer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import cool.rdf.core.model.RdfModel;
import cool.rdf.core.model.RdfResource;

public class InferrerTest {
   private final Inferrer inferrer = new Inferrer();

   private InputStream turtleInputStream() {
      final String turtleDocument = """
         @prefix : <https://rdf.cool/vocab/example#> .
         @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
         @prefix owl: <http://www.w3.org/2002/07/owl#> .

         :Person a owl:Class .
         :Student rdfs:subClassOf :Person .
         :name a owl:DatatypeProperty .
         :Max a :Student ;
             :name "Max" .
         """;
      return new ByteArrayInputStream( turtleDocument.getBytes() );
   }

   @Test
   public void testReasoning() {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final Configuration configuration = Inferrer.DEFAULT_CONFIGURATION;
      inferrer.infer( turtleInputStream(), out, configuration ).get();
      final RdfModel rdfModel = RdfModel.fromTurtle( out.toString() );

      final String namespace = "https://rdf.cool/vocab/example#";
      final RdfResource max = rdfModel.createRdfResource( namespace + "Max" );
      final RdfResource person = rdfModel.createRdfResource( namespace + "Person" );
      assertThat( rdfModel.contains( max, RDF.type, person ) ).isTrue();
   }
}
