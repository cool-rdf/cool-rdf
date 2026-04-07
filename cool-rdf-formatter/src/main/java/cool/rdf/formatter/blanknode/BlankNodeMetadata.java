/*
 * Copyright Andreas Textor
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

package cool.rdf.formatter.blanknode;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * A lookup table for each blank node's order in a TTL file.
 */
public class BlankNodeMetadata {
   private final Map<Node, Long> blankNodeIndex = new HashMap<>();
   private final Map<Node, String> blankNodeLabels = new HashMap<>();
   private final Set<Resource> labeledBlankNodes = new HashSet<>();
   private long nextIndex = 0;

   public BlankNodeMetadata() {}

   public void linkGraphNodesToModelResources( final Model model ) {
      labeledBlankNodes.addAll( model.listStatements()
            .toList()
            .stream()
            .flatMap( s -> Stream.of( s.getSubject(), s.getObject() ) )
            .filter( RDFNode::isAnon )
            .filter( a -> blankNodeLabels.containsKey( a.asNode() ) )
            .map( RDFNode::asResource )
            .collect( Collectors.toSet() ) );

   }

   public static BlankNodeMetadata gotNothing() {
      return new BlankNodeMetadata();
   }

   /**
    * Returns the order of the specified node, if it has been added previously via
    * {@link #registerNewBlankNode(Node)},
    * or null.
    *
    * @param node the node to look up
    * @return the 0-based order of the {@code node} (or null if it has not been registered)
    */
   public Long getOrder( final Node node ) {
      return blankNodeIndex.get( node );
   }

   /**
    * If the specified {@code node} is a labeled blank node, the label is returned.
    *
    * @param node the node to look up
    * @return the label or null
    */
   public String getLabel( final Node node ) {
      return blankNodeLabels.get( node );
   }

   void registerNewBlankNode( final Node blankNode ) {
      if ( blankNode.isBlank() && !blankNodeIndex.containsKey( blankNode ) ) {
         blankNodeIndex.put( blankNode, nextIndex++ );
      }
   }

   void registerNewBlankNode( final Node blankNode, final String label ) {
      registerNewBlankNode( blankNode );
      blankNodeLabels.put( blankNode, label );
   }

   public Set<Resource> getLabeledBlankNodes() {
      return Collections.unmodifiableSet( labeledBlankNodes );
   }

   public Set<String> getAllBlankNodeLabels() {
      return Set.copyOf( blankNodeLabels.values() );
   }
}
