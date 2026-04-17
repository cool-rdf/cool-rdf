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

package cool.rdf.diagram.owl;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * The configuration that controls the visual output
 *
 * @param dotBinary the name (and possibly path) to the GraphViz dot binary
 * @param fontname the default font name
 * @param fontsize the default font size
 * @param nodeFontname the name of the font for nodes
 * @param nodeFontsize the size of the font for nodes
 * @param nodeShape the default node shape, see
 *        <a href="https://graphviz.org/doc/info/shapes.html">Node Shapes</a> for the possible
 *        options
 * @param nodeMargin the margin values to be used for shapes, see
 *        <a href="https://graphviz.org/docs/attrs/margin/">margin</a> for syntax
 * @param nodeStyle the style to use for nodes, see
 *        <a href="https://graphviz.org/docs/attr-types/style/">style</a> for details
 * @param bgColor the background color for nodes
 * @param fgColor the foreground color (lines and text)
 * @param format the output format
 * @param layoutDirection the diagram layout direction
 */
@RecordBuilder
public record Configuration(
      @RecordBuilder.Initializer( "DEFAULT_DOT_BINARY" ) String dotBinary,
      @RecordBuilder.Initializer( "DEFAULT_FONT_NAME" ) String fontname,
      @RecordBuilder.Initializer( "DEFAULT_FONT_SIZE" ) int fontsize,
      @RecordBuilder.Initializer( "DEFAULT_FONT_NAME" ) String nodeFontname,
      @RecordBuilder.Initializer( "DEFAULT_FONT_SIZE" ) int nodeFontsize,
      @RecordBuilder.Initializer( "DEFAULT_NODE_SHAPE" ) String nodeShape,
      @RecordBuilder.Initializer( "DEFAULT_NODE_MARGIN" ) String nodeMargin,
      @RecordBuilder.Initializer( "DEFAULT_NODE_STYLE" ) String nodeStyle,
      @RecordBuilder.Initializer( "DEFAULT_BG_COLOR" ) String bgColor,
      @RecordBuilder.Initializer( "DEFAULT_FG_COLOR" ) String fgColor,
      @RecordBuilder.Initializer( "DEFAULT_FORMAT" ) Format format,
      @RecordBuilder.Initializer( "DEFAULT_LAYOUT_DIRECTION" ) LayoutDirection layoutDirection
) {
   public static final String DEFAULT_DOT_BINARY = "dot";
   public static final String DEFAULT_FONT_NAME = "Verdana";
   public static final int DEFAULT_FONT_SIZE = 12;
   public static final String DEFAULT_NODE_SHAPE = "box";
   public static final String DEFAULT_NODE_MARGIN = "0.05,0.0";
   public static final String DEFAULT_NODE_STYLE = "rounded";
   public static final String DEFAULT_BG_COLOR = "white";
   public static final String DEFAULT_FG_COLOR = "black";
   public static final Format DEFAULT_FORMAT = Format.SVG;
   public static final LayoutDirection DEFAULT_LAYOUT_DIRECTION = LayoutDirection.LEFT_TO_RIGHT;

   /**
    * The possible formats for diagram generation
    */
   public enum Format {
      /**
       * PNG format
       */
      PNG,
      /**
       * SVG format
       */
      SVG;

      /**
       * The file extension according to the format, e.g. "png" or "svg"
       *
       * @return the file extension
       */
      public String getExtension() {
         return toString().toLowerCase();
      }

      @Override
      public String toString() {
         return switch ( this ) {
            case PNG -> "png";
            case SVG -> "svg";
         };
      }
   }

   /**
    * The possible directions in which the generated diagrams are aligned
    */
   public enum LayoutDirection {
      /**
       * Diagrams are aligned vertically, with the root node on top
       */
      TOP_TO_BOTTOM,

      /**
       * Diagrams are aligned horizontally, with the root node on the left
       */
      LEFT_TO_RIGHT,

      /**
       * Check if the file contains the {@link #HINT_PREFIX} followed by either "top_to_bottom" or
       * "left_to_right"
       */
      DETECT;

      public static final String HINT_PREFIX = "#pragma diagram: ";

      @Override
      public String toString() {
         return switch ( this ) {
            case TOP_TO_BOTTOM -> "top_to_bottom";
            case LEFT_TO_RIGHT -> "left_to_right";
            case DETECT -> "detect";
         };
      }
   }
}
