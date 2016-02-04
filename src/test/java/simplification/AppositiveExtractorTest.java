package simplification;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static simplification.AppositiveExtractor.removeNonRestrictiveAppositives;

public class AppositiveExtractorTest {

    @Test
    public void testRemoveNonRestrictiveAppositives1() throws Exception {
        final String original = "Bob Jones, my dear friend, likes cats.";
        final String modified = "Bob Jones likes cats.";
        assertEquals(modified, removeNonRestrictiveAppositives(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositives2() throws Exception {
        final String original = "Jefferson, the third U.S. president, loved to eat apples, peaches, and oranges.";
        final String modified = "Jefferson loved to eat apples, peaches, and oranges.";
        assertEquals(modified, removeNonRestrictiveAppositives(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositives3() throws Exception {
        final String original = "Washington, the first president, and Jefferson, the third president, were friends.";
        final String modified = "Washington and Jefferson were friends.";
        assertEquals(modified, removeNonRestrictiveAppositives(original));
    }
}