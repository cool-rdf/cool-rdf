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

package cool.rdf.formatter;

import java.util.Comparator;
import java.util.Optional;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.PrefixMapping;

import cool.rdf.formatter.blanknode.BlankNodeMetadata;

public class RDFNodeComparatorFactory {

   private final PrefixMapping prefixMapping;
   private final BlankNodeMetadata blankNodeOrdering;
   private final RDFNodeComparator rdfNodeComparator = new RDFNodeComparator();

   public RDFNodeComparatorFactory( final PrefixMapping prefixMapping, final BlankNodeMetadata blankNodeOrdering ) {
      this.prefixMapping = prefixMapping;
      this.blankNodeOrdering = blankNodeOrdering;
   }

   public RDFNodeComparatorFactory( final PrefixMapping prefixMapping ) {
      this( prefixMapping, null );
   }

   public RDFNodeComparator comparator() {
      return rdfNodeComparator;
   }

   private class RDFNodeComparator implements Comparator<RDFNode> {
      @Override
      public int compare( final RDFNode left, final RDFNode right ) {
         if ( left.isURIResource() ) {
            if ( right.isURIResource() ) {
               return prefixMapping.shortForm( left.asResource().getURI() ).compareTo( prefixMapping.shortForm( right.asResource()
                     .getURI() ) );
            } else if ( right.isAnon() ) {
               return -1; // uris first
            }
         } else if ( left.isAnon() ) {
            if ( right.isAnon() ) {
               if ( blankNodeOrdering != null ) {
                  return Optional.ofNullable( blankNodeOrdering.getOrder( left.asResource().asNode() ) )
                        .orElse( Long.MAX_VALUE )
                        .compareTo( Optional.ofNullable(
                              blankNodeOrdering.getOrder( right.asResource().asNode() ) )
                              .orElse( Long.MAX_VALUE ) );
               }
            } else if ( right.isResource() ) {
               return 1; // uris first
            }
         }
         // fall-through for all other cases, especially if we don't have a blank node ordering
         return left.toString().compareTo( right.toString() );
      }
   }
}
