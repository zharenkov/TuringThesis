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
    public void testRemoveParentheticalsBirthDeath() throws Exception {
        // "February 22, 1732 – December 14, 1799" is a parenthetical
        final String original = "George Washington (February 22, 1732 – December 14, 1799) was the first president.";
        final String modified = "George Washington was the first president.";
        final String simplified1 = "George Washington was born February 22, 1732.";
        final String simplified2 = "George Washington died December 14, 1799.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simplified1,
                simplified2);
    }

    @Test
    public void testRemoveParentheticalsBirth() throws Exception {
        // "born December 18, 1963" is a parenthetical
        final String original = "Brad Pitt (born December 18, 1963) is an American actor and producer.";
        final String modified = "Brad Pitt is an American actor and producer.";
        final String simplified1 = "Brad Pitt was born December 18, 1963.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simplified1);
    }

    @Test
    public void testRemoveParentheticalsName() throws Exception {
        // "born Leslie Lynch King" is a parenthetical
        final String original = "Gerald Rudolph Ford (born Leslie Lynch King) was an American politician.";
        final String modified = "Gerald Rudolph Ford was an American politician.";
        final String simplified1 = "Gerald Rudolph Ford was born Leslie Lynch King.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simplified1);
    }

    @Test
    public void testRemoveParentheticalsAcronymOrganization() throws Exception {
        // "GIF" is a parenthetical
        final String original = "Major League Baseball (MLB) is a professional sports organization.";
        final String modified = "Major League Baseball is a professional sports organization.";
        final String simplified1 = "MLB stands for Major League Baseball.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simplified1);
    }

    @Test
    public void testRemoveParentheticalsAcronym() throws Exception {
        // "GIF" is a parenthetical
        final String original = "The Graphics Interchange Format (GIF) is a standard for images.";
        final String modified = "The Graphics Interchange Format is a standard for images.";
        final String simplified1 = "GIF stands for The Graphics Interchange Format.";
        assertThat(extractor.extract(original).getSimplifiedSentences()).containsExactly(modified, simplified1);
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