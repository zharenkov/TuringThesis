package util;

import edu.stanford.nlp.simple.Sentence;
import simplenlg.features.Tense;

public class TenseUtil {
    public static Tense calculateTense(Sentence sentence) {
        for (final String tag : sentence.posTags()) {
            // Look for past-tense verb or past participle
            if (tag.equalsIgnoreCase("vbd") || tag.equalsIgnoreCase("vbn")) {
                return Tense.PAST;
            }
        }
        return Tense.PRESENT;
    }
}
