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

package cool.rdf.formatter.blanknode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.lang.LangRIOT;
import org.apache.jena.riot.lang.LangTurtle;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.ParserProfileWrapper;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.riot.tokens.TokenizerText;
import org.apache.jena.sparql.util.Context;

public class BlankNodeOrderAwareTurtleParser {
   /**
    * Parses the TTL {@code content} and returns a {@link ParseResult}, containing the new
    * {@link Model} and a {@link BlankNodeMetadata} object that makes the ordering of the blank nodes
    * in the original {@code content} accessible for further processing.
    *
    * @param content RDF in TTL format
    * @return the parse result and the blank node ordering
    */
   public static ParseResult parseModel( final String content ) {
      final BlankNodeMetadata bnodeMetadata = new BlankNodeMetadata();

      final Lang ttlBn = LangBuilder.create( "TTL_BN", "text/bogus" )
            .build();
      RDFParserRegistry.registerLangTriples( ttlBn, ( language, profile ) -> {
         final ParserProfile profileWrapper = new ParserProfileWrapper( profile ) {
            @Override
            public Node createBlankNode( final Node scope, final String label, final long line, final long col ) {
               final Node blank = get().createBlankNode( scope, label, line, col );
               bnodeMetadata.registerNewBlankNode( blank, label );
               return blank;
            }

            @Override
            public Node createBlankNode( final Node scope, final long line, final long col ) {
               final Node blank = get().createBlankNode( scope, line, col );
               bnodeMetadata.registerNewBlankNode( blank );
               return blank;
            }

            @Override
            public Node create( final Node currentGraph, final Token token ) {
               // Dispatches to the underlying ParserFactory operation
               final long line = token.getLine();
               final long col = token.getColumn();
               final String str = token.getImage();
               if ( token.getType() == TokenType.BNODE ) {
                  return createBlankNode( currentGraph, str, line, col );
               }
               return get().create( currentGraph, token );
            }

         };
         return new ReaderRIOT() {
            @Override
            public void read( final InputStream in, final String baseURI, final ContentType ct, final StreamRDF output,
                  final Context context ) {
               final Tokenizer tokenizer = TokenizerText.create().source( in ).errorHandler( profileWrapper.getErrorHandler() ).build();
               final LangRIOT parser = new LangTurtle( tokenizer, profileWrapper, output );
               parser.parse();
            }

            @Override
            public void read( final Reader reader, final String baseURI, final ContentType ct, final StreamRDF output,
                  final Context context ) {
               final Tokenizer tokenizer = TokenizerText.create().source( reader ).errorHandler( profileWrapper.getErrorHandler() ).build();
               final LangRIOT parser = new LangTurtle( tokenizer, profileWrapper, output );
               parser.parse();
            }
         };
      } );
      final Graph graph = RDFParser.source( new ByteArrayInputStream( content.getBytes() ) ).labelToNode( LabelToNode
            .createUseLabelAsGiven() )
            .lang( ttlBn ).toGraph();
      RDFParserRegistry.removeRegistration( ttlBn );
      final Model model = ModelFactory.createModelForGraph( graph );
      bnodeMetadata.linkGraphNodesToModelResources( model );
      return new ParseResult( model, bnodeMetadata );
   }

   public record ParseResult(
         Model model,
         BlankNodeMetadata blankNodeMetadata
   ) {}
}
