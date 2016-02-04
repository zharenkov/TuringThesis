package simplification;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static simplification.ParentheticalExtractor.removeParentheticals;

public class ParentheticalExtractorTest {

    @Test
    public void testRemoveParentheticalsNoParentheticals() throws Exception {
        // The sentence contains no parentheticals so it should remain unchanged
        final String original = "Bob Jones, my dear friend, likes cats.";
        assertEquals(original, removeParentheticals(original));
    }

    @Test
    public void testRemoveParentheticalsSimpleParenthetical() throws Exception {
        // "February 22, 1732 – December 14, 1799" is a parenthetical
        final String original = "George Washington (February 22, 1732 – December 14, 1799) was the first president.";
        final String modified = "George Washington was the first president.";
        assertEquals(modified, removeParentheticals(original));
    }

    @Test
    public void testRemoveParentheticalsNestedParentheticals() throws Exception {
        // "my (somewhat) good friend" is a parenthetical
        final String original = "John (my (somewhat) good friend) likes cats.";
        final String modified = "John likes cats.";
        assertEquals(modified, removeParentheticals(original));
    }

    @Test
    public void testRemoveParentheticalsMultipleParentheticals() throws Exception {
        // "my (somewhat) good friend" and "my nemesis" are parentheticals
        final String original = "John (my (somewhat) good friend) and Bob (my nemesis) like cats.";
        final String modified = "John and Bob like cats.";
        assertEquals(modified, removeParentheticals(original));
    }
}