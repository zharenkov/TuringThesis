package simplification;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;

import java.util.List;
import java.util.Stack;

// TODO implement Extractor interface
public class ParentheticalExtractor {
    private static final String LEFT_PARENTHESIS = "-LRB-";
    private static final String RIGHT_PARENTHESIS = "-RRB-";

    /**
     * Returns the given sentence with all parentheticals removed. This method expects parenthesis to be properly paired
     * (i.e. every open parenthesis has a corresponding closed parenthesis).
     *
     * @param sentence the given sentence
     * @return the given sentence with all parentheticals removed.
     */
    public static String removeParentheticals(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        System.out.println("Original sentence: " + words);

        int start = -1;
        final Stack<String> parenthesis = new Stack<>();
        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals(LEFT_PARENTHESIS)) {
                if (start == -1) {
                    start = i;
                }
                parenthesis.add("(");
            } else if (words.get(i).equals(RIGHT_PARENTHESIS)) {
                parenthesis.pop();
                if (parenthesis.isEmpty()) {
                    rangeSet.add(Range.closed(start, i));
                    start = -1;
                }
            }
        }
        final List<String> modified = WordListUtil.removeParts(words, rangeSet);
        System.out.println("With parentheticals removed: " + modified);
        return WordListUtil.constructSentenceFromWordList(modified);
    }
}
