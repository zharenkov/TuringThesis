package util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class to build up a space-separated phrase in reverse order. This is useful, for instance, when iterating
 * through a word list in reverse order.
 */
public class ReversePhraseBuilder {
    private static final Joiner SPACES = Joiner.on(' ');
    private final LinkedList<String> phrase = new LinkedList<>();

    /**
     * Adds the given string to the beginning of the current phrase if the given string is non-empty.
     *
     * @param string the given string
     */
    public void addString(String string) {
        if (string.length() > 0) {
            phrase.addFirst(string);
        }
    }

    /**
     * Returns an immutable list of the words currently in the phrase.
     *
     * @return the list of words currently in the phrase
     */
    public List<String> getWords() {
        return ImmutableList.copyOf(phrase);
    }

    @Override
    public String toString() {
        return SPACES.join(phrase);
    }
}
