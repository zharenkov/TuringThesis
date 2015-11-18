package tagging;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StanfordNamedEntityRecognizer {
    private static final String serializedClassifier = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf" +
            ".ser.gz";
    private static final AbstractSequenceClassifier<CoreLabel> classifier;

    static {
        classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
    }

    private static List<Triple<String, Integer, Integer>> findNerSubstrings(String sentence) {
        return classifier.classifyToCharacterOffsets(sentence);
    }

    public static Map<String, NamedEntity> findNamedEntities(String sentence) {
        final Map<String, NamedEntity> namedEntities = new HashMap<>();
        final List<Triple<String, Integer, Integer>> nerSubstrings = findNerSubstrings(sentence);
        for (final Triple<String, Integer, Integer> substring : nerSubstrings) {
            namedEntities.put(sentence.substring(substring.second(), substring.third()),
                    NamedEntity.getNamedEntity(substring.first()));
        }
        return namedEntities;
    }
}
