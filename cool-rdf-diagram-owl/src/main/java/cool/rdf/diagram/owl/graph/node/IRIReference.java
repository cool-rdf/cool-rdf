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

import org.semanticweb.owlapi.model.IRI;

import cool.rdf.diagram.owl.graph.Node;
import cool.rdf.diagram.owl.graph.transformer.IriReferenceResolver;

/**
 * Represents a reference to some yet unknown other graph that has a {@link Node.Id} with a given
 * {@link IRI}. This type
 * of node should never end up in the final graph, as it is resolved by the
 * {@link IriReferenceResolver} after the Axiom
 * -> Graph Elements mapping is done.
 *
 * @param id the id of the node
 * @param iri the IRI of the referenced node
 */
public record IRIReference(
      Id id, IRI iri
) implements Node.InvisibleNode<IRIReference> {
   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visit( this );
   }

   @Override
   public IRIReference withId( final Id id ) {
      return this.id == id ? this : new IRIReference( id, iri );
   }
}
