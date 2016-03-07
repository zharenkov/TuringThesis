package util;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;

/**
 * Utility class for dealing with parts of speech.
 */
public class PosUtil {
    /**
     * Returns whether the word at the given index in the given sentence is a verb.
     *
     * @param sentence the given sentence
     * @param index    the given index
     * @return whether the word at the given index is a verb
     */
    public static boolean isVerb(Sentence sentence, int index) {
        return sentence.posTag(index).toLowerCase().startsWith("vb");
    }

    /**
     * Returns whether the word at the given index in the given sentence is a noun.
     *
     * @param sentence the given sentence
     * @param index    the given index
     * @return whether the word at the given index is a noun
     */
    public static boolean isNoun(Sentence sentence, int index) {
        final String posTag = sentence.posTag(index).toLowerCase();
        return posTag.startsWith("nn") || posTag.startsWith("prp");
    }

    /**
     * Returns whether the word at the given index in the given sentence is a noun.
     *
     * @param root the root of the given sentence
     * @param leaf the given leaf
     * @return whether the word at the given index is a noun
     */
    public static boolean isNoun(Tree root, Tree leaf) {
        if (leaf.isLeaf()) {
            final String posTag = TreeUtil.getParent(root, leaf).value().toLowerCase();
            return posTag.equals("nn") || posTag.equals("prp");
        }
        System.err.println("Tree is not a leaf");
        return false;
    }

    /**
     * Returns whether the word at the given index in the given sentence is a plural noun.
     *
     * @param sentence the given sentence
     * @param index    the given index
     * @return whether the word at the given index is a plural noun
     */
    public static boolean isPluralNoun(Sentence sentence, int index) {
        return isNoun(sentence, index) && sentence.posTag(index).toLowerCase().endsWith("s");
    }

    /**
     * Returns whether the word at the given index in the given sentence is a past-tense verb.
     *
     * @param sentence the given sentence
     * @param index    the given index
     * @return whether the word at the given index is a past-tense verb
     */
    public static boolean isPastTenseVerb(Sentence sentence, int index) {
        final String posTag = sentence.posTag(index).toLowerCase();
        return posTag.equals("vbn") || posTag.equals("vbd");
    }

    /**
     * Returns whether the given leaf in the given sentence is a past-tense verb.
     *
     * @param root the root of the given sentence
     * @param leaf the given leaf
     * @return whether the word at the given index is a past-tense verb
     */
    public static boolean isPastTenseVerb(Tree root, Tree leaf) {
        if (leaf.isLeaf()) {
            final String posTag = TreeUtil.getParent(root, leaf).value().toLowerCase();
            return posTag.equals("vbn") || posTag.equals("vbd");
        }
        System.err.println("Tree is not a leaf");
        return false;
    }
}
