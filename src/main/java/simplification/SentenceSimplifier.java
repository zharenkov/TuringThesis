package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import util.WordListUtil;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SentenceSimplifier {
    private static final List<Extractor> extractors = ImmutableList.of(ParentheticalExtractor.getExtractor(),
            AppositiveAndRelativeClauseExtractor.getExtractor(), ConjoinedVerbPhraseExtractor.getExtractor(),
            ConjoinedVerbExtractor.getExtractor(), VerbPhraseModifierExtractor.getExtractor(),
            AppositiveAndRelativeClauseExtractor.getExtractor(), ParticipialModifiersExtractor.getExtractor());

    public static void main(String[] args) {
        System.out.println(simplifySentence(Joiner.on(' ').join(args)));
    }

    public static Set<String> simplifySentence(String originalSentence) {
        Set<String> sentences = new LinkedHashSet<>();
        sentences.add(originalSentence.replaceAll("\"", ""));
        for (final Extractor extractor : extractors) {
            final Set<String> simplifiedSentences = new HashSet<>();
            for (final String sentence : sentences) {
                simplifiedSentences.addAll(extractor.extract(sentence).getSimplifiedSentences());
            }
            sentences = simplifiedSentences;
        }
        return cleanSentences(sentences);
    }

    private static Set<String> cleanSentences(Set<String> simplifiedSentences) {
        final Set<String> cleanedSentences = new LinkedHashSet<>(simplifiedSentences.size());
        for (final String sentence : simplifiedSentences) {
            cleanedSentences.add(cleanSentence(sentence));
        }
        return cleanedSentences;
    }

    private static String cleanSentence(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        for (int i = 0; i < parsed.words().size() - 2; i++) {
            final String word = parsed.words().get(i);
            // Check for IPA and remove it
            if (word.equals("/") && parsed.word(i + 2).equals("/")) {
                rangeSet.add(Range.closed(i, i + 2));
            }
        }
        return WordListUtil.constructPhraseFromWordList(WordListUtil.removeParts(parsed.words(), rangeSet));
    }
}
