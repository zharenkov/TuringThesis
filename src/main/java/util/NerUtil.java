package util;

import edu.stanford.nlp.simple.Sentence;

public class NerUtil {
    /**
     * Returns whether the given string contains a word that is tagged as a PERSON.
     *
     * @param np the given string
     * @return {@code true} if the given string contains a word tagged as a PERSON
     */
    public static boolean isPerson(String np) {
        final Sentence sentence = new Sentence(np);
        for (String label : sentence.nerTags()) {
            if (label.equalsIgnoreCase("person")) {
                return true;
            }
        }
        return false;
    }
}
