package simplification;

import edu.stanford.nlp.simple.Sentence;

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