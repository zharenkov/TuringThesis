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

    /**
     * Returns whether the word at the given index in the given sentence represents a person.
     *
     * @param sentence the given sentence
     * @param index    the given index
     * @return whether the word at the given index in the given sentence represents a person
     */
    public static boolean isPerson(Sentence sentence, int index) {
        System.out.println(sentence.word(index));
        return sentence.nerTag(index).equalsIgnoreCase("person");
    }
}
