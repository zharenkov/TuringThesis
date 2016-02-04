package simplification;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static simplification.AppositiveAndRelativeClauseExtractor.removeNonRestrictiveAppositivesAndRelativeClauses;

public class AppositiveAndRelativeClauseExtractorTest {

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesNoAppositivesNoRelativeClauses() throws Exception {
        // The sentence contains no appositives and no relative clauses so it should remain unchanged
        final String original = "Bob, Sally, and John like cats.";
        assertEquals(original, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesSimpleAppositive() throws Exception {
        // "my dear friend" is a non-restrictive appositive
        final String original = "Bob Jones, my dear friend, likes cats.";
        final String modified = "Bob Jones likes cats.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesAppositiveAtEndOfSentence() throws Exception {
        // "the third president" is a non-restrictive appositive
        final String original = "I like Jefferson, the third president.";
        final String modified = "I like Jefferson.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesAppositiveAndConjunction() throws Exception {
        // "the third U.S. president" is a non-restrictive appositive
        final String original = "Jefferson, the third U.S. president, loved to eat apples, peaches, and oranges.";
        final String modified = "Jefferson loved to eat apples, peaches, and oranges.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesMultipleAppositives() throws Exception {
        // "the first president" and "the third president" are non-restrictive appositives
        final String original = "Washington, the first president, and Jefferson, the third president, were friends.";
        final String modified = "Washington and Jefferson were friends.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesSimpleRelativeClause() throws Exception {
        // "who was my dear friend" is a non-restrictive relative clause
        final String original = "Bob Jones, who was my dear friend, likes cats.";
        final String modified = "Bob Jones likes cats.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesRelativeClauseAtEndOfSentence() throws Exception {
        // "who was the third president" is a non-restrictive relative clause
        final String original = "I like Jefferson, who was the third president.";
        final String modified = "I like Jefferson.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesRelativeClauseAndConjunction() throws Exception {
        // "who was the third president" is a non-restrictive relative clause
        final String original = "Jefferson, who was the third president, loved to eat apples, peaches, and oranges.";
        final String modified = "Jefferson loved to eat apples, peaches, and oranges.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesMultipleRelativeClauses() throws Exception {
        // "who was the first president" and "who was the third president" are non-restrictive relative clauses
        final String original = "Washington, who was the first president, and Jefferson, who was the third president, were friends.";
        final String modified = "Washington and Jefferson were friends.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }

    @Test
    public void testRemoveNonRestrictiveAppositivesAndRelativeClausesAppositiveAndRelativeClause() throws Exception {
        // "who was the first president" is a non-restrictive relative clause and "the third president" is an appositive
        final String original = "Washington, who was the first president, and Jefferson, the third president, were friends.";
        final String modified = "Washington and Jefferson were friends.";
        assertEquals(modified, removeNonRestrictiveAppositivesAndRelativeClauses(original));
    }
}