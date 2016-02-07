package simplification;

import java.util.Set;

/**
 * Represents the result of passing a sentence through a given {@link Extractor} for simplification.
 */
public class SimplificationResult {
    private final Set<String> simplifiedSentences;

    public SimplificationResult(Set<String> simplifiedSentences) {
        this.simplifiedSentences = simplifiedSentences;
    }

    public Set<String> getSimplifiedSentences() {
        return simplifiedSentences;
    }
}
