package simplification;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the result of passing a sentence through a given {@link Extractor} for simplification.
 */
public class SimplificationResult {
    private final Set<String> simplifiedSentences;

    public SimplificationResult(Set<String> simplifiedSentences) {
        this.simplifiedSentences = processSentences(simplifiedSentences);
    }

    private static Set<String> processSentences(Set<String> sentences) {
        final Set<String> processedSentences = new HashSet<>();
        for (final String sentence : sentences) {
            processedSentences.add(Character.toUpperCase(sentence.charAt(0)) + sentence.substring(1));
        }
        return processedSentences;
    }

    public Set<String> getSimplifiedSentences() {
        return simplifiedSentences;
    }
}
