package lemmatizing;

import edu.stanford.nlp.process.Morphology;

public class Lemmatizer {
    /**
     * Returns the base, non-inflected version of the given word with the given part of speech (POS) tag.
     * <p/>
     * Words other than proper nouns will be changed to all lowercase.
     *
     * @param word the given word
     * @param tag the given POS tag
     *
     * @return The base, non-inflected version of the given word
     */
    public static String lemmatize(String word, String tag) {
        return Morphology.lemmaStatic(word, tag, true);
    }
}
