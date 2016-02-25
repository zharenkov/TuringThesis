package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import generation.TextRealization;
import util.WordListUtil;

import java.util.List;

public class ParticipialModifiersExtractor implements Extractor {
    private static ParticipialModifiersExtractor extractor;

    private ParticipialModifiersExtractor() {
    }

    public static ParticipialModifiersExtractor getExtractor() {
        if (extractor == null) {
            extractor = new ParticipialModifiersExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
        getExtractor().extract(Joiner.on(' ').join(args));
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        final List<String> nerTags = parsed.nerTags();
        final List<String> posTags = parsed.posTags();

        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        for (int i = 1; i < posTags.size(); i++) {
            final String posTag = posTags.get(i);
            if (posTag.equalsIgnoreCase("vbg") || posTag.equalsIgnoreCase("vbn")) {
                if (words.get(i - 1).equalsIgnoreCase(",")) {
                    int rightCommaBound = words.size() - 1;
                    for (int k = i + 1; k < words.size(); k++) {
                        if (words.get(k).equalsIgnoreCase(",") && !nerTags.get(k).equalsIgnoreCase("date")) {
                            rightCommaBound = k;
                            break;
                        }
                    }
                    while (rightCommaBound < words.size() && !words.get(rightCommaBound).equalsIgnoreCase(",")) {
                        rightCommaBound++;
                    }
                    rangeSet.add(Range.closed(i - 1, rightCommaBound));

                }
            }
        }
        if (rangeSet.isEmpty()) {
            return new SimplificationResult(ImmutableSet.of(sentence));
        }
        final String simplifiedSentence = TextRealization.realizeSentence(WordListUtil.constructPhraseFromWordList(
                WordListUtil.removeParts(words, rangeSet)));
        return new SimplificationResult(ImmutableSet.of(simplifiedSentence));
    }
}
