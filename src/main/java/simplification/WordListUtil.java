package simplification;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.RangeSet;
import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WordListUtil {
    private static final Set<String> NO_WHITESPACE_BEFORE = ImmutableSet.of(",", ";", "!", ".", "'", "''", ":");
    private static final Set<String> NO_WHITESPACE_AFTER = ImmutableSet.of("`", "``");

    /**
     * Returns the given list of words with the given parts removed. The given list will not be modified by this method.
     * This method assumes that indices in the given ranges are 0-based.
     * <p>
     * For instance, if {@code partsToRemove} contained the range {@code [5-6]} and {@code words} contained
     * {@code ["My", "friend", "John", "likes", "cats", "and", "dogs", "."]}, then the result would be
     * {@code ["My", "friend", "John", "likes", "cats", "."]}.
     * <p>
     * Behavior for this method is not defined if the ranges fall outside of the valid boundaries of the given word
     * list.
     *
     * @param words         the given list of words
     * @param partsToRemove the parts to remove from the list of words
     * @return the list of words with the given parts removed
     */
    public static List<String> removeParts(List<String> words, RangeSet<Integer> partsToRemove) {
        final List<String> modifiedWords = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (partsToRemove.contains(i)) {
                continue;
            }
            modifiedWords.add(words.get(i));
        }
        return modifiedWords;
    }

    public static String constructPhraseFromTree(Tree tree) {
        final List<Tree> leaves = tree.getLeaves();
        final List<String> words = new ArrayList<>(leaves.size());
        for (final Tree leaf : leaves) {
            words.add(leaf.value());
        }
        return constructSentenceFromWordList(words);
    }

    /**
     * Takes a list of words from the Stanford CoreNLP parser and reconstructs a String for the words.
     *
     * @param words the list of words from the Stanford CoreNLP parser
     * @return the String for the list of words
     */
    public static String constructSentenceFromWordList(List<String> words) {
        final StringBuilder answer = new StringBuilder();
        for (final String word : words) {
            if (NO_WHITESPACE_BEFORE.contains(word)) {
                removeWhitespaceAtEnd(answer);
            }

            switch (word) {
                // Stanford's parser turns first quotes into specialized 'left' forms that are difficult to deal with so
                // just turn them back into regular quotes.
                case "`":
                    answer.append("'");
                    break;
                // Stanford's parser turns double quotes into two single quotes so turn them back into proper form
                case "''":
                case "``":
                    answer.append("\"");
                    break;
                default:
                    answer.append(word);
                    break;
            }

            if (!NO_WHITESPACE_AFTER.contains(word)) {
                answer.append(" ");
            }
        }

        return answer.toString().trim();
    }

    private static void removeWhitespaceAtEnd(StringBuilder stringBuilder) {
        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == ' ') {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
    }
}
