package util;

import edu.stanford.nlp.simple.Sentence;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static util.PosUtil.*;

public class PosUtilTest {

    @Test
    public void testIsVerbPositiveCase() throws Exception {
        assertTrue(isVerb(new Sentence("I ran a mile."), 1));
        assertTrue(isVerb(new Sentence("I run a lot."), 1));
        assertTrue(isVerb(new Sentence("He ate."), 1));
    }

    @Test
    public void testIsVerbNegativeCase() throws Exception {
        assertFalse(isVerb(new Sentence("I ran a mile."), 0));
        assertFalse(isVerb(new Sentence("I run a lot."), 0));
        assertFalse(isVerb(new Sentence("He ate."), 0));
        assertFalse(isVerb(new Sentence("Eating is run."), 0));
    }

    @Test
    public void testIsNounPositiveCase() throws Exception {
        assertTrue(isNoun(new Sentence("I ran a mile."), 3));
        assertTrue(isNoun(new Sentence("I run a lot."), 0));
        assertTrue(isNoun(new Sentence("He ate food."), 2));
    }

    @Test
    public void testIsNounNegativeCase() throws Exception {
        assertFalse(isNoun(new Sentence("I ran a mile."), 1));
        assertFalse(isNoun(new Sentence("I run a lot."), 1));
        assertFalse(isNoun(new Sentence("He ate."), 2));
        assertFalse(isNoun(new Sentence("Eating is run."), 1));
    }

    @Test
    public void testIsPluralNounPositiveCase() throws Exception {
        assertTrue(isPluralNoun(new Sentence("I ran two miles."), 3));
        assertTrue(isPluralNoun(new Sentence("I ate apples."), 2));
        assertTrue(isPluralNoun(new Sentence("Apples taste good."), 0));
    }

    @Test
    public void testIsPluralNounNegativeCase() throws Exception {
        assertFalse(isPluralNoun(new Sentence("I ran one mile."), 3));
        assertFalse(isPluralNoun(new Sentence("I ate apple."), 1));
        assertFalse(isPluralNoun(new Sentence("Apples taste good."), 1));
    }

    @Test
    public void testIsPastTenseVerbPositiveCase() throws Exception {
        assertTrue(isPastTenseVerb(new Sentence("I ran two miles."), 1));
        assertTrue(isPastTenseVerb(new Sentence("I ate apples."), 1));
        assertTrue(isPastTenseVerb(new Sentence("Apples fell from the tree."), 1));
    }

    @Test
    public void testIsPastTenseVerbNegativeCase() throws Exception {
        assertFalse(isPastTenseVerb(new Sentence("I run two miles every day."), 1));
        assertFalse(isPastTenseVerb(new Sentence("I eat apples."), 1));
        assertFalse(isPastTenseVerb(new Sentence("Apples fall from trees."), 1));
    }
}