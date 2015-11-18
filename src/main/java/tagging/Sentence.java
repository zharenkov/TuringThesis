package tagging;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.Collection;
import java.util.Map;

/**
 * Contains parse information for a single sentence.
 */
public class Sentence {
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

    public Map<String, NamedEntity> getNamedEntities() {
        return namedEntities;
    }
}
