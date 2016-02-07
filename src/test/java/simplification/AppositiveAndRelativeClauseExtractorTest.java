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
        final String simple1 = "Bob Jones is my dear friend.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simple1);
    }

    @Test
    public void testExtractAppositiveAtEndOfSentence() throws Exception {
        // "the third president" is a non-restrictive appositive
        final String original = "I like Jefferson, the third president.";
        final String modified = "I like Jefferson.";
        final String simple1 = "Jefferson is the third president.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simple1);
    }

    @Test
    public void testExtractAppositiveAndConjunction() throws Exception {
        // "the third U.S. president" is a non-restrictive appositive
        final String original = "Jefferson, the third U.S. president, loved to eat apples, peaches, and oranges.";
        final String modified = "Jefferson loved to eat apples, peaches, and oranges.";
        final String simple1 = "Jefferson was the third U.S. president.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simple1);
    }

    @Test
    public void testExtractMultipleAppositives() throws Exception {
        // "the first president" and "the noted inventor" are non-restrictive appositives
        final String original = "Bob, my dear friend, married Sally, a math teacher.";
        final String modified = "Bob married Sally.";
        final String simple1 = "Bob was my dear friend.";
        final String simple2 = "Sally was a math teacher.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simple1, simple2);
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