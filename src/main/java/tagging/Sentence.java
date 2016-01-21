package tagging;

import com.google.common.base.Joiner;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Contains parse information for a single sentence.
 */
public class Sentence implements Serializable {
    private final Tree posTree;
    private final Collection<TypedDependency> dependencies;
    private final Map<String, NamedEntity> namedEntities;

    Sentence(Tree posTree, Collection<TypedDependency> dependencies, Map<String, NamedEntity> namedEntities) {
        this.posTree = posTree;
        this.dependencies = dependencies;
        this.namedEntities = namedEntities;
    }

    public Tree getPosTree() {
        return posTree;
    }

    public Collection<TypedDependency> getDependencies() {
        return dependencies;
    }

    public List<TypedDependency> getDependenciesForLeaf(Tree governor) {
        final List<TypedDependency> dependenciesForLeaf = new ArrayList<>();
        final int leafIndex = calculateLeafIndex(governor);
        for (final TypedDependency typedDependency : dependencies) {
            // The typed dependencies are 1-indexed not zero indexed
            if (typedDependency.gov().index() - 1 == leafIndex) {
                dependenciesForLeaf.add(typedDependency);
            }
        }
        return dependenciesForLeaf;
    }

    public List<TypedDependency> getDependenciesForWord(IndexedWord governor) {
        final List<TypedDependency> dependenciesForLeaf = new ArrayList<>();
        for (final TypedDependency typedDependency : dependencies) {
            // The typed dependencies are 1-indexed not zero indexed
            if (typedDependency.gov().equals(governor)) {
                dependenciesForLeaf.add(typedDependency);
            }
        }
        return dependenciesForLeaf;
    }

    private int calculateLeafIndex(Tree leaf) {
        final List<Tree> leaves = posTree.getLeaves();
        for (int i = 0; i < leaves.size(); i++) {
            if (leaves.get(i).equals(leaf)) {
                return i;
            }
        }
        return -1;
    }

    public String getNp(IndexedWord part) {
        final StringBuilder np = new StringBuilder();
        for (final TypedDependency typedDependency : getDependenciesForWord(part)) {
            if (typedDependency.gov().equals(part)) {
                final String label = typedDependency.reln().getLongName().toLowerCase();
                String originalText = typedDependency.dep().originalText();
                if(label.contains("det")) {
                    originalText = originalText.toLowerCase();
                }
                if (label.contains("compound") || label.contains("det")) {
                    np.append(originalText).append(" ");
                }
            }
        }
        np.append(part.originalText());
        return np.toString();
    }

    public Map<String, NamedEntity> getNamedEntities() {
        return namedEntities;
    }

    public String getString() {
        return Joiner.on(' ').join(posTree.getLeaves());
    }
}
