package simplification;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

// TODO assert on all simple sentences that should be created
public class AppositiveAndRelativeClauseExtractorTest {
    private static final Extractor extractor = AppositiveAndRelativeClauseExtractor.getExtractor();

    @Test
    public void testExtractNoAppositivesNoRelativeClauses() throws Exception {
        // The sentence contains no appositives and no relative clauses so it should remain unchanged
        final String original = "Bob, Sally, and John like cats.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(original);
    }

    @Test
    public void testExtractSimpleAppositive() throws Exception {
        // "my dear friend" is a non-restrictive appositive
        final String original = "Bob Jones, my dear friend, likes cats.";
        final String modified = "Bob Jones likes cats.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractAppositiveAtEndOfSentence() throws Exception {
        // "the third president" is a non-restrictive appositive
        final String original = "I like Jefferson, the third president.";
        final String modified = "I like Jefferson.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractAppositiveAndConjunction() throws Exception {
        // "the third U.S. president" is a non-restrictive appositive
        final String original = "Jefferson, the third U.S. president, loved to eat apples, peaches, and oranges.";
        final String modified = "Jefferson loved to eat apples, peaches, and oranges.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractMultipleAppositives() throws Exception {
        // "the first president" and "the third president" are non-restrictive appositives
        final String original = "Washington, the first president, and Jefferson, the third president, were friends.";
        final String modified = "Washington and Jefferson were friends.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractSimpleRelativeClause() throws Exception {
        // "who was my dear friend" is a non-restrictive relative clause
        final String original = "Bob Jones, who was my dear friend, likes cats.";
        final String modified = "Bob Jones likes cats.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractRelativeClauseAtEndOfSentence() throws Exception {
        // "who was the third president" is a non-restrictive relative clause
        final String original = "I like Jefferson, who was the third president.";
        final String modified = "I like Jefferson.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractRelativeClauseAndConjunction() throws Exception {
        // "who was the third president" is a non-restrictive relative clause
        final String original = "Jefferson, who was the third president, loved to eat apples, peaches, and oranges.";
        final String modified = "Jefferson loved to eat apples, peaches, and oranges.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractMultipleRelativeClauses() throws Exception {
        // "who was the first president" and "who was the third president" are non-restrictive relative clauses
        final String original = "Washington, who was the first president, and Jefferson, who was the third president, were friends.";
        final String modified = "Washington and Jefferson were friends.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }

    @Test
    public void testExtractAppositiveAndRelativeClause() throws Exception {
        // "who was the first president" is a non-restrictive relative clause and "the third president" is an appositive
        final String original = "Washington, who was the first president, and Jefferson, the third president, were friends.";
        final String modified = "Washington and Jefferson were friends.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).contains(modified);
    }
}