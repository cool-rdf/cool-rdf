package cool.rdf.formatter.blanknode;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A lookup table for each blank node's order in a TTL file.
 */
public class BlankNodeMetadata {
    private final Map<Node, Long> blankNodeIndex = new HashMap<>();
    private final Map<Node, String> blankNodeLabels = new HashMap<>();
    private final Set<Resource> labeledBlankNodes = new HashSet<>();
    private long nextIndex = 0;

    public BlankNodeMetadata() {
    }

    public void linkGraphNodesToModelResources(Model model ){
        this.labeledBlankNodes.addAll(model.listStatements()
                        .toList()
                        .stream()
                        .flatMap(s -> Stream.of(s.getSubject(), s.getObject()))
                        .filter(RDFNode::isAnon)
                        .filter(a -> this.blankNodeLabels.containsKey(a.asNode()))
                        .map(RDFNode::asResource)
                        .collect(Collectors.toSet()));

    }

    public static BlankNodeMetadata gotNothing() {
        return new BlankNodeMetadata();
    }

    /**
     * Returns the order of the specified node, if it has been added previously via
     * {@link #registerNewBlankNode(Node)}, or null.
     * @param node the node to look up
     * @return the 0-based order of the {@code node} (or null if it has not been registered)
     */
    public Long getOrder(Node node) {
        return blankNodeIndex.get(node);
    }

    /**
     * If the specified {@code node} is a labeled blank node, the label is returned.
     * @param node
     * @return the label or null.
     */
    public String getLabel(Node node) {
        return blankNodeLabels.get(node);
    }

    void registerNewBlankNode(Node blankNode) {
        if (blankNode.isBlank() &&  ! blankNodeIndex.containsKey(blankNode)){
            this.blankNodeIndex.put(blankNode, nextIndex++);
        }
    }

    void registerNewBlankNode(Node blankNode, String label) {
        registerNewBlankNode(blankNode);
        this.blankNodeLabels.put(blankNode, label);
    }

    public Set<Resource> getLabeledBlankNodes() {
        return Collections.unmodifiableSet(this.labeledBlankNodes);
    }

    public Set<String> getAllBlankNodeLabels() {
        return Collections.unmodifiableSet(new HashSet<>(this.blankNodeLabels.values()));
    }
}
