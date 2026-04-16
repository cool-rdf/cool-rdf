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

package cool.rdf.diagram.owl.graph;

import java.util.function.Function;

/**
 * Visitor for elements of a graph
 *
 * @param <T> the result type of the visit operation
 */
public class GraphVisitor<T> implements GraphElement.Visitor<T> {
   private final Node.Visitor<T> nodeTypeVisitor;

   private final Function<Edge.Plain, T> plainEdgeHandler;

   private final Function<Edge.Decorated, T> decoratedEdgeHandler;

   public GraphVisitor( final Node.Visitor<T> nodeTypeVisitor, final Function<Edge.Plain, T> plainEdgeHandler,
         final Function<Edge.Decorated, T> decoratedEdgeHandler ) {
      this.nodeTypeVisitor = nodeTypeVisitor;
      this.plainEdgeHandler = plainEdgeHandler;
      this.decoratedEdgeHandler = decoratedEdgeHandler;
   }

   @Override
   public T visit( final Edge.Plain edge ) {
      return plainEdgeHandler.apply( edge );
   }

   @Override
   public T visit( final Edge.Decorated decoratedEdge ) {
      return decoratedEdgeHandler.apply( decoratedEdge );
   }

   @Override
   public T visit( final Node node ) {
      return node.accept( nodeTypeVisitor );
   }
}
