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
     * Returns the label of the given tree in all lower-case.
     *
     * @param tree the given tree
     * @return the label of the given tree in all lower-case
     */
    public static String getLabel(Tree tree) {
        return tree.value().toLowerCase();
    }

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
     * @return the {@link Tree} representing the NP or {@code null} if no such tree exists
     */
    public static Tree getFirstNp(Tree root) {
        final Tree rootChild = root.getChild(0);
        for (final Tree child : rootChild.getChildrenAsList()) {
            if (labelEquals(child, "np")) {
                return child;
            }
        }
        return null;
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
     * Returns the highest parent of the given word in the phrase structure tree represented by {@code root} that has the label "NP".
     *
     * @param root the given root of the phrase structure tree
     * @param word the given word
     * @return the {@link Tree} representing the NP or {@code null} if no such tree exists
     */
    public static Tree getHighestNpFromWord(Tree root, Tree word) {
        Tree currentTree = word;
        while (!currentTree.label().value().equalsIgnoreCase("np")) {
            currentTree = getParent(root, currentTree);
            if (currentTree == root) {
                return null;
            }
        }
        Tree previousTree = currentTree;
        while (labelEquals(currentTree, "np") || labelEquals(currentTree, "pp")) {
            previousTree = currentTree;
            currentTree = getParent(root, currentTree);
        }
        return previousTree;
    }

    /**
     * Returns the highest parent of the given word in the phrase structure tree represented by {@code root} that has the label "NP".
     *
     * @param root  the given root of the phrase structure tree
     * @param index the index of the given word
     * @return the {@link Tree} representing the NP or {@code null} if no such tree exists
     */
    public static Tree getHighestNpFromWord(Tree root, int index) {
        return getHighestNpFromWord(root, root.getLeaves().get(index));
    }

    /**
     * Returns the first parent of the word represented by the given index in the phrase structure tree represented by {@code root} that has the label "NP".
     *
     * @param root  the given root of the phrase structure tree
     * @param index the given index
     * @return the {@link Tree} representing the NP or {@code null} if no such tree exists
     */
    public static Tree getNpFromWord(Tree root, int index) {
        final Tree wordTree = root.getLeaves().get(index);
        return getNpFromWord(root, wordTree);
    }

    /**
     * Returns the first parent of the given word in the phrase structure tree represented by {@code root} that has the label "NP".
     *
     * @param root the given root of the phrase structure tree
     * @param word the given word
     * @return the {@link Tree} representing the NP or {@code null} if no such tree exists
     */
    public static Tree getNpFromWord(Tree root, IndexedWord word) {
        return getNpFromWord(root, word.index() - 1);
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
     * Returns a {@link Vp} representing the full VP starting with the parent of the given tree in the sentence
     * represented by the given root.
     *
     * @param root the root of the given sentence
     * @param tree the given tree
     * @return a {@link Vp} representing the full VP
     */
    public static Vp getFullVpFromTree(Tree root, Tree tree) {
        final ReversePhraseBuilder reversePhraseBuilder = new ReversePhraseBuilder();
        Tree previousTree = tree;
        Tree currentTree = getParent(root, tree);
        while (labelEquals(currentTree, "vp")) {
            reversePhraseBuilder.addString(constructPhraseFromTree(currentTree.getChild(0)));
            previousTree = currentTree;
            currentTree = getParent(root, currentTree);
            if (currentTree == root) {
                break;
            }
        }
        return new Vp(reversePhraseBuilder.getWords(), previousTree);
    }

    /**
     * Returns the first SBAR ancestor for the given tree under the given root.
     *
     * @param root the given root
     * @param tree the given tree
     * @return the first SBAR ancestor or {@code null} if none exists
     */
    public static Tree getFirstSbar(Tree root, Tree tree) {
        Tree currentTree = getParent(root, tree);
        while (!labelEquals(currentTree, "sbar")) {
            currentTree = getParent(root, currentTree);
            if (currentTree == root) {
                return null;
            }
        }
        return currentTree;
    }

    /**
     * Returns whether the given NP tree is plural.
     *
     * @param tree the given NP tree
     * @return {@code true} if the NP tree is plural
     */
    public static boolean npIsPlural(Tree tree) {
        for (final Tree leaf : tree.getLeaves()) {
            final String posTag = TreeUtil.getParent(tree, leaf).value().toLowerCase();
            if (posTag.startsWith("nn") && posTag.startsWith("prp") && posTag.endsWith("s")) {
                return true;
            }
        }
        return false;
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
     * Returns the head of the given tree in the given sentence.
     *
     * @param root the root of the given sentence
     * @param tree the given tree
     * @return the head of the tree
     */
    public static int findIndexOfHead(Tree root, Tree tree) {
        final Tree head = HEAD_FINDER.determineHead(tree);
        return getLeafIndex(root, head.getLeaves().get(0));
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
     * Returns the string from the sentence represented by the given root that occurs between the given trees.
     *
     * @param root      the given root
     * @param leftTree  the given left tree
     * @param rightTree the given right tree
     * @return the string between the given trees
     */
    public static String getStringBetweenTrees(Tree root, Tree leftTree, Tree rightTree) {
        final List<String> stringBetween = new ArrayList<>();

        final Tree leftBound = Iterables.getLast(leftTree.getLeaves());
        final Tree rightBound = rightTree.getLeaves().get(0);

        final List<Tree> leaves = root.getLeaves();
        boolean add = false;
        for (final Tree leaf : leaves) {
            if (leaf == leftBound) {
                add = true;
            } else if (leaf == rightBound) {
                break;
            } else if (add) {
                stringBetween.add(leaf.value());
            }
        }
        return WordListUtil.constructPhraseFromWordList(stringBetween);
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

    /**
     * Returns whether the given tree represents a conjunction and that the conjunction is the word "and".
     *
     * @param tree the given tree
     * @return {@code true} if the given tree is a CC with the word "and" as its leaf
     */
    public static boolean treeIsAndConjunction(Tree tree) {
        return labelEquals(tree, "cc") && labelEquals(tree.getLeaves().get(0), "and");
    }
}
