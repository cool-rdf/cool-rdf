package cool.rdf.formatter.blanknode;

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
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.lang.LangRIOT;
import org.apache.jena.riot.lang.RiotParsers;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.ParserProfileWrapper;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.apache.jena.sparql.util.Context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

public class BlankNodeOrderAwareTurtleParser {
    /**
     * Parses the TTL <code>content</code> and returns a {@link ParseResult}, containing the
     * new {@link Model} and a {@link BlankNodeMetadata} object that makes the ordering of the
     * blank nodes in the original <code>content</code> accessible for further processing.
     * @param content RDF in TTL format
     * @return the parse result and the blank node ordering
     */
    public static ParseResult parseModel(String content) {
        BlankNodeMetadata bnodeMetadata = new BlankNodeMetadata();

        Lang TTL_bn = LangBuilder.create("TTL_BN", "text/bogus")
                        .build();
        RDFParserRegistry.registerLangTriples(TTL_bn, new ReaderRIOTFactory() {
            @Override public ReaderRIOT create(Lang language, ParserProfile profile) {
                ParserProfile profileWrapper = new ParserProfileWrapper(profile) {
                    @Override public Node createBlankNode(Node scope, String label, long line, long col) {
                        Node blank = get().createBlankNode(scope, label, line, col);
                        bnodeMetadata.registerNewBlankNode(blank, label);
                        return blank;
                    }

                    @Override public Node createBlankNode(Node scope, long line, long col) {
                        Node blank = get().createBlankNode(scope, line, col);
                        bnodeMetadata.registerNewBlankNode(blank);
                        return blank;
                    }

                    @Override
                    public Node create(Node currentGraph, Token token) {
                        // Dispatches to the underlying ParserFactory operation
                        long line = token.getLine();
                        long col = token.getColumn();
                        String str = token.getImage();
                        if (token.getType() == TokenType.BNODE) {
                            return createBlankNode(currentGraph, str, line, col);
                        }
                        return get().create(currentGraph, token);
                    }

                };
                return new ReaderRIOT() {
                    @Override public void read(InputStream in, String baseURI, ContentType ct, StreamRDF output,
                                    Context context) {
                        LangRIOT parser = RiotParsers.createParser(in, Lang.TTL, output, profileWrapper);
                        parser.parse();
                    }

                    @Override public void read(Reader reader, String baseURI, ContentType ct, StreamRDF output,
                                    Context context) {
                        LangRIOT parser = RiotParsers.createParser(reader, Lang.TTL, output, profileWrapper);
                        parser.parse();
                    }
                };
            }
        });
        Graph graph = RDFParser.source(new ByteArrayInputStream(content.getBytes())).labelToNode(LabelToNode.createUseLabelAsGiven()).lang(
                        TTL_bn).toGraph();
        RDFParserRegistry.removeRegistration(TTL_bn);
        Model model = ModelFactory.createModelForGraph(graph);
        bnodeMetadata.linkGraphNodesToModelResources(model);
        return new ParseResult(model, bnodeMetadata);
    }

    public static class ParseResult {
        private final Model model;
        private final BlankNodeMetadata blankNodeMetadata;

        public ParseResult(Model model, BlankNodeMetadata blankNodeMetadata) {
            this.model = model;
            this.blankNodeMetadata = blankNodeMetadata;
        }

        public Model getModel() {
            return model;
        }

        public BlankNodeMetadata getBlankNodeMetadata() {
            return blankNodeMetadata;
        }
    }
}
