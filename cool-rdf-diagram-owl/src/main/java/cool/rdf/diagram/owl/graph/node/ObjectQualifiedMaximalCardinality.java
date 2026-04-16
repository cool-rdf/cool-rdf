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

package cool.rdf.diagram.owl.graph.node;

import cool.rdf.diagram.owl.graph.Node;

/**
 * Represents a qualified OWL object maximal cardinality node in the graph.
 *
 * @param id the id of the node
 * @param cardinality the cardinality of the node
 */
public record ObjectQualifiedMaximalCardinality(
      Id id, int cardinality
) implements Node.CardinalityNode {
   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visit( this );
   }

   @Override
   public ObjectQualifiedMaximalCardinality withId( final Id id ) {
      return this.id == id ? this : new ObjectQualifiedMaximalCardinality( id, cardinality );
   }

   public ObjectQualifiedMaximalCardinality withCardinality( final int cardinality ) {
      return this.cardinality == cardinality ? this : new ObjectQualifiedMaximalCardinality( id, cardinality );
   }
}
