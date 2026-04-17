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

import cool.rdf.formatter.TurtleFormatter;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * The configuration for inferring from models
 *
 * @param base the format used to parse the input
 * @param inputFormat the RDF base URL
 */
@RecordBuilder
public record Configuration(
      @RecordBuilder.Initializer( "DEFAULT_INPUT_FORMAT" ) Format inputFormat,
      @RecordBuilder.Initializer( "DEFAULT_BASE" ) String base
) {

   public static final Format DEFAULT_INPUT_FORMAT = Format.TURTLE;
   public static final String DEFAULT_BASE = TurtleFormatter.DEFAULT_EMPTY_BASE;

   /**
    * For backwards compatibility
    *
    * @return the builder
    */
   public static ConfigurationBuilder builder() {
      return ConfigurationBuilder.builder();
   }

   /**
    * The possible input formats
    */
   public enum Format {
      /**
       * RDF/Turtle
       */
      TURTLE,

      /**
       * RDF/XML
       */
      RDFXML,

      /**
       * N-Triple
       */
      NTRIPLE,

      /**
       * N3 format
       */
      N3;

      @Override
      public String toString() {
         return switch ( this ) {
            case TURTLE -> "turtle";
            case RDFXML -> "rdfxml";
            case NTRIPLE -> "ntriple";
            case N3 -> "n3";
         };
      }
   }
}
