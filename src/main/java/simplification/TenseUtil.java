package simplification;

import edu.stanford.nlp.simple.Sentence;

public class TenseUtil {
    public static Tense calculateTense(Sentence sentence) {
        for (final String tag : sentence.posTags()) {
            if (tag.equalsIgnoreCase("vbd")) {
                return Tense.PAST;
            }
        }
        return Tense.PRESENT;
    }
}
