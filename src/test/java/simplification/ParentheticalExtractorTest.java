package simplification;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ParentheticalExtractorTest {
    private static final Extractor extractor = ParentheticalExtractor.getExtractor();

    @Test
    public void testRemoveParentheticalsNoParentheticals() throws Exception {
        // The sentence contains no parentheticals so it should remain unchanged
        final String original = "Bob Jones, my dear friend, likes cats.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(original);
    }

    @Test
    public void testRemoveParentheticalsSimpleParenthetical() throws Exception {
        // "February 22, 1732 – December 14, 1799" is a parenthetical
        final String original = "George Washington (February 22, 1732 – December 14, 1799) was the first president.";
        final String modified = "George Washington was the first president.";
        final String simplified1 = "George Washington was born February 22, 1732.";
        final String simplified2 = "George Washington died December 14, 1799.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simplified1,
                simplified2);
    }

    @Test
    public void testRemoveParentheticalsNestedParentheticals() throws Exception {
        // "my (somewhat) good friend" is a parenthetical
        final String original = "John (my (somewhat) good friend) likes cats.";
        final String modified = "John likes cats.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified);
    }

    @Test
    public void testRemoveParentheticalsMultipleParentheticals() throws Exception {
        // "my (somewhat) good friend" and "my nemesis" are parentheticals
        final String original = "John (my (somewhat) good friend) and Bob (my nemesis) like cats.";
        final String modified = "John and Bob like cats.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified);
    }
}