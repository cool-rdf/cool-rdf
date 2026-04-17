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

package cool.rdf.core.model.impl;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StatementTerm;

import cool.rdf.core.model.RdfNode;

public class DefaultRdfNode implements RdfNode {
   private final RDFNode rdfNode;

   public DefaultRdfNode( final RDFNode rdfNode ) {
      this.rdfNode = rdfNode;
   }

   @Override
   public boolean isAnon() {
      return rdfNode.isAnon();
   }

   @Override
   public boolean isLiteral() {
      return rdfNode.isLiteral();
   }

   @Override
   public boolean isURIResource() {
      return rdfNode.isURIResource();
   }

   @Override
   public boolean isResource() {
      return rdfNode.isResource();
   }

   @Override
   public boolean isStatementTerm() {
      return rdfNode.isStatementTerm();
   }

   @Override
   public <T extends RDFNode> T as( final Class<T> view ) {
      return rdfNode.as( view );
   }

   @Override
   public <T extends RDFNode> boolean canAs( final Class<T> view ) {
      return rdfNode.canAs( view );
   }

   @Override
   public Model getModel() {
      return rdfNode.getModel();
   }

   @Override
   public RDFNode inModel( final Model m ) {
      return rdfNode.inModel( m );
   }

   @Override
   public Object visitWith( final RDFVisitor rv ) {
      return rdfNode.visitWith( rv );
   }

   @Override
   public Resource asResource() {
      return rdfNode.asResource();
   }

   @Override
   public Literal asLiteral() {
      return rdfNode.asLiteral();
   }

   @Override
   public StatementTerm asStatementTerm() {
      return rdfNode.asStatementTerm();
   }

   @Override
   public Node asNode() {
      return rdfNode.asNode();
   }
}
