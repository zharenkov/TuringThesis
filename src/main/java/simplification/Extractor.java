package simplification;

/**
 * A system for simplifying a sentence by extracting syntactic constructions and possibly using those constructions to
 * generate questions.
 */
public interface Extractor {

    /**
     * Simplifies the given sentence by removing certain syntactic constructions.
     *
     * @param sentence the given sentence
     * @return the result of the extraction
     */
    SimplificationResult extract(String sentence);
}
