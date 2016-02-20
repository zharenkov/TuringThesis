package util;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.Tree;

public class TreeUtil {
    /**
     * Returns the parent of the given {@code child} tree using the given {@code root} tree to perform the search.
     *
     * @param root  the root of the phrase structure tree
     * @param child the given child tree
     * @return the parent tree of the given child tree
     */
    public static Tree getParent(Tree root, Tree child) {
        return getParent(root, child, 1);
    }

    /**
     * Returns the ancestor that is {@code n} generations separated from the given {@code child} tree using the given
     * {@code root} tree to perform the search.
     *
     * @param root  the root of the phrase structure tree
     * @param child the given child tree
     * @param n     the number of generations to go back
     * @return the parent tree of the given child tree
     */
    public static Tree getParent(Tree root, Tree child, int n) {
        for (int i = 0; i < n; i++) {
            child = child.parent(root);
        }
        return child;
    }

    /**
     * Returns the first parent of the given word in the phrase structure tree represented by {@code root} that has the label "NP".
     *
     * @param root the given root of the phrase structure tree
     * @param word the given word
     * @return the {@link Tree} representing the NP or {@code null} if no such tree exists
     */
    public static Tree getNpFromWord(Tree root, Tree word) {
        Tree currentTree = word;
        while (!currentTree.label().value().equalsIgnoreCase("np")) {
            currentTree = getParent(root, currentTree);
            if (currentTree == root) {
                return null;
            }
        }
        return currentTree;
    }

    /**
     * Returns the first parent of the given word in the phrase structure tree represented by {@code root} that has the label "NP".
     *
     * @param root the given root of the phrase structure tree
     * @param word the given word
     * @return the {@link Tree} representing the NP or {@code null} if no such tree exists
     */
    public static Tree getNpFromWord(Tree root, IndexedWord word) {
        final Tree wordTree = root.getLeaves().get(word.index() - 1);
        return getNpFromWord(root, wordTree);
    }

    /**
     * Returns the first parent of the given word in the phrase structure tree represented by {@code root} that has the label "VP".
     *
     * @param root the given root of the phrase structure tree
     * @param word the given word
     * @return the {@link Tree} representing the VP or {@code null} if no such tree exists
     */
    public static Tree getVpFromWord(Tree root, Tree word) {
        Tree currentTree = word;
        while (!currentTree.label().value().equalsIgnoreCase("vp")) {
            currentTree = getParent(root, currentTree);
            if (currentTree == root) {
                return null;
            }
        }
        return currentTree;
    }

    /**
     * Returns whether the label for the given tree equals the given label (case-insensitive).
     *
     * @param tree  the given tree
     * @param label the given label
     * @return whether the label for the given tree equals the given label (case-insensitive)
     */
    public static boolean labelEquals(Tree tree, String label) {
        return tree.label().value().equalsIgnoreCase(label.toLowerCase());
    }
}
