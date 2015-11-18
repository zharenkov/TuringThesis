package tagging;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.Collection;

/**
 * Contains parse information for a single sentence.
 */
public class Sentence {
    private final Tree posTree;
    private final Collection<TypedDependency> dependencies;

    Sentence(Tree posTree, Collection<TypedDependency> dependencies) {
        this.posTree = posTree;
        this.dependencies = dependencies;
    }

    public Tree getPosTree() {
        return posTree;
    }

    public Collection<TypedDependency> getDependencies() {
        return dependencies;
    }
}
