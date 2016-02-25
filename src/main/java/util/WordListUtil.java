package util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WordListUtil {
    private static final Set<String> NO_WHITESPACE_BEFORE = ImmutableSet.of(",", ";", "!", ".", "'", "''", ":");
    private static final Set<String> NO_WHITESPACE_AFTER = ImmutableSet.of("`", "``");
    private static final String COMMA = ",";

    /**
     * Calculates whether the given dependent is enclosed within commas or the end of the given sentence after the given
     * governor.
     *
     * @param governor  the given governor
     * @param dependent the given dependent
     * @param sentence  the given sentence
     * @return {@code null} if the dependent is not enclosed, or a range representing the enclosure
     */
    public static Range<Integer> findBoundedPart(IndexedWord governor, IndexedWord dependent, Sentence sentence) {
        final List<String> words = sentence.words();
        final List<String> nerTags = sentence.nerTags();
        final int governorIndex = governor.index() - 1;
        final int dependentIndex = dependent.index() - 1;

        // See if the dependent is enclosed within commas
        int leftCommaBound = -1;
        int rightCommaBound = words.size() - 1;
        for (int i = dependentIndex; i >= 0; i--) {
            if (words.get(i).equals(COMMA) && !nerTags.get(i).equalsIgnoreCase("date")) {
                leftCommaBound = i;
                break;
            }
        }
        for (int i = dependentIndex; i < words.size(); i++) {
            if (words.get(i).equals(COMMA) && !nerTags.get(i).equalsIgnoreCase("date")) {
                rightCommaBound = i;
                break;
            }
        }
        if (leftCommaBound <= governorIndex || rightCommaBound <= governorIndex) {
            System.out.println("The dependent is not bounded by commas after the governor");
            return null;
        }
        // If the appositive or relative clause is at the end of the sentence, make sure we're not deleting the
        // period.
        if (words.get(rightCommaBound).equals(".")) {
            rightCommaBound--;
        }
        return Range.closed(leftCommaBound, rightCommaBound);
    }

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

    /**
     * Takes a list of words from the Stanford CoreNLP parser and reconstructs a String for the words. This method will
     * try and ensure that punctuation and spacing is correct in the returned phrase.
     *
     * @param words the list of words from the Stanford CoreNLP parser
     * @return the String for the list of words
     */
    public static String constructPhraseFromWordList(List<String> words) {
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

        return answer.toString().replaceAll(" 's ", "'s ").trim();
    }

    private static void removeWhitespaceAtEnd(StringBuilder stringBuilder) {
        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == ' ') {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
    }
}
