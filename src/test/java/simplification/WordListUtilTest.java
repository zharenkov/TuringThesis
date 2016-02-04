package simplification;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WordListUtilTest {
    private static final List<String> WORDS = ImmutableList.of("John", ",", "my", "good", "friend", ",", "likes",
            "cats", "and", "dogs", ".");

    @Test
    public void testRemovePartsNoParts() throws Exception {
        assertEquals(WORDS, WordListUtil.removeParts(WORDS, TreeRangeSet.create()));
    }

    @Test
    public void testRemovePartsAllParts() throws Exception {
        final ImmutableRangeSet<Integer> entireRange = ImmutableRangeSet.of(Range.closed(0, WORDS.size() - 1));
        assertEquals(Collections.EMPTY_LIST, WordListUtil.removeParts(WORDS, entireRange));
    }

    @Test
    public void testRemovePartsOnePart() throws Exception {
        // The range corresponds to the word 'good' in the sentence
        final ImmutableRangeSet<Integer> range = ImmutableRangeSet.of(Range.closed(3, 3));
        final List<String> expected = ImmutableList.of("John", ",", "my", "friend", ",", "likes", "cats", "and", "dogs",
                ".");
        assertEquals(expected, WordListUtil.removeParts(WORDS, range));
    }

    @Test
    public void testRemovePartsMultiWordPart() throws Exception {
        // The range corresponds to the phrase ", my good friend," in the sentence
        final ImmutableRangeSet<Integer> range = ImmutableRangeSet.of(Range.closed(1, 5));
        final List<String> expected = ImmutableList.of("John", "likes", "cats", "and", "dogs", ".");
        assertEquals(expected, WordListUtil.removeParts(WORDS, range));
    }

    @Test
    public void testConstructSentenceFromWordList() throws Exception {
        assertConstructSentenceFromWordListCorrect("This is a simple sentence");
        assertConstructSentenceFromWordListCorrect("This is a simple sentence with a period.");
        assertConstructSentenceFromWordListCorrect("This is a simple sentence with a period; and a semicolon.");
        assertConstructSentenceFromWordListCorrect("My friend Bob 'Bobby' John is nice.");
        assertConstructSentenceFromWordListCorrect("My friend Bob \"Bobby\" John is nice.");
        assertConstructSentenceFromWordListCorrect("Bob \"Bobby\" John, my good friend, loves the song Hey! by Keyz.");
    }

    private static void assertConstructSentenceFromWordListCorrect(String sentence) {
        final List<String> words = new Sentence(sentence).words();
        assertEquals(sentence, WordListUtil.constructSentenceFromWordList(words));
    }
}