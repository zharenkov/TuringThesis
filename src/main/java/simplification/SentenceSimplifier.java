package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
    private static final Set<Character> BANNED_CHARACTERS = ImmutableSet.of('\"');

    private static final List<Extractor> extractors = ImmutableList.of(ParentheticalExtractor.getExtractor(),
            AppositiveExtractor.getExtractor(), ConjoinedVerbPhraseExtractor.getExtractor(),
            ConjoinedVerbExtractor.getExtractor(), VerbPhraseModifierExtractor.getExtractor(),
            RelativeClauseExtractor.getExtractor(), ParticipialModifiersExtractor.getExtractor(),
            PrepositionalPhraseExtractor.getExtractor(), SbarWhExtractor.getExtractor());

    public static void main(String[] args) {
        System.out.println(simplifySentence(Joiner.on(' ').join(args)));
    }

    public static Set<String> simplifySentence(String originalSentence) {
        Set<String> sentences = new LinkedHashSet<>();
        String modifiedSentence = originalSentence;
        for (final char bannedCharacter : BANNED_CHARACTERS) {
            modifiedSentence = modifiedSentence.replaceAll("" + bannedCharacter, "");
        }
        sentences.add(modifiedSentence);
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
        final List<String> words = parsed.words();
        for (int i = 0; i < words.size() - 2; i++) {
            final String word = words.get(i);
            // Check for IPA and remove it
            if (word.equals("/") && parsed.word(i + 2).equals("/")) {
                rangeSet.add(Range.closed(i, i + 2));
            }
        }
        final String ipaRemoved = WordListUtil.constructPhraseFromWordList(WordListUtil.removeParts(words, rangeSet));
        return ipaRemoved.replaceAll(" -- ", "-");
    }
}
