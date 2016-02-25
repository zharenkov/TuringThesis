package util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.CollinsHeadFinder;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.List;

public class TreeUtil {
    private static final HeadFinder HEAD_FINDER = new CollinsHeadFinder();

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

    /**
     * Returns whether the label for the given tree starts with the given prefix (case-insensitive).
     *
     * @param tree   the given tree
     * @param prefix the given prefix
     * @return whether the label for the given tree starts with the given prefix (case-insensitive)
     */
    public static boolean labelStartsWith(Tree tree, String prefix) {
        return tree.label().value().toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Returns whether the label for the given tree contains the given part (case-insensitive).
     *
     * @param tree the given tree
     * @param part the given part
     * @return whether the label for the given tree contains the given part (case-insensitive)
     */
    public static boolean labelContains(Tree tree, String part) {
        return tree.label().value().toLowerCase().contains(part.toLowerCase());
    }

    /**
     * Returns the head of the given tree.
     *
     * @param tree the given tree
     * @return the head of the tree
     */
    public static Tree findHead(Tree tree) {
        return HEAD_FINDER.determineHead(tree);
    }

    /**
     * Returns the index of the given leaf in the tree represented by the given root.
     *
     * @param root the given root
     * @param leaf the given leaf
     * @return the index of the given leaf
     */
    public static int getLeafIndex(Tree root, Tree leaf) {
        final List<Tree> leaves = root.getLeaves();
        for (int i = 0; i < leaves.size(); i++) {
            if (leaves.get(i) == leaf) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the string from the sentence represented by the given root that occurs before the given tree.
     *
     * @param root           the given root
     * @param childToExclude the given tree
     * @return the string before the given tree
     */
    public static String getStringBeforeTree(Tree root, Tree childToExclude) {
        final List<String> words = new ArrayList<>();
        final Tree childToExcludeFirstLeaf = childToExclude.getLeaves().get(0);
        for (final Tree leaf : root.getLeaves()) {
            if (leaf == childToExcludeFirstLeaf) {
                break;
            } else {
                words.add(leaf.value());
            }
        }
        return WordListUtil.constructPhraseFromWordList(words);
    }

    /**
     * Returns the string from the sentence represented by the given root that occurs after the given tree.
     *
     * @param root           the given root
     * @param childToExclude the given tree
     * @return the string after the given tree
     */
    public static String getStringAfterTree(Tree root, Tree childToExclude) {
        final ReversePhraseBuilder stringAfter = new ReversePhraseBuilder();
        final Tree childToExcludeLastLeaf = Iterables.getLast(childToExclude.getLeaves());
        final List<Tree> leaves = root.getLeaves();
        for (int i = leaves.size() - 1; i >= 0; i--) {
            final Tree leaf = leaves.get(i);
            if (leaf == childToExcludeLastLeaf) {
                break;
            } else {
                stringAfter.addString(leaf.value());
            }
        }
        return WordListUtil.constructPhraseFromWordList(stringAfter.getWords());
    }

    /**
     * Returns the range representing the bounds of the given tree in the given sentence.
     *
     * @param root the root of the given sentence
     * @param tree the given tree
     * @return the range of the given tree
     */
    public static Range<Integer> getRangeOfTree(Tree root, Tree tree) {
        final List<Tree> leaves = tree.getLeaves();
        final Tree leftmostLeaf = leaves.get(0);
        final Tree rightmostLeaf = leaves.get(leaves.size() - 1);
        int leftIndex = 0;
        int rightIndex = 0;
        final List<Tree> rootLeaves = root.getLeaves();
        for (int i = 0; i < rootLeaves.size(); i++) {
            final Tree leaf = rootLeaves.get(i);
            if (leaf == leftmostLeaf) {
                leftIndex = i;
            }
            if (leaf == rightmostLeaf) {
                rightIndex = i;
            }
        }
        return Range.closed(leftIndex, rightIndex);
    }

    /**
     * Takes a {@link Tree} from the CoreNLP parser and reconstructs a String for the words. This method will try and
     * ensure that punctuation and spacing is correct in the returned phrase.
     *
     * @param tree the tree from the Stanford CoreNLP parser
     * @return the String for the tree
     */
    public static String constructPhraseFromTree(Tree tree) {
        final List<Tree> leaves = tree.getLeaves();
        final List<String> words = new ArrayList<>(leaves.size());
        for (final Tree leaf : leaves) {
            words.add(leaf.value());
        }
        return WordListUtil.constructPhraseFromWordList(words);
    }
}
