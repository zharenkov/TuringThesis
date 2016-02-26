package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SentenceSimplifier {
    private static final List<Extractor> extractors = ImmutableList.of(ParentheticalExtractor.getExtractor(),
            AppositiveAndRelativeClauseExtractor.getExtractor(), ConjoinedVerbPhraseExtractor.getExtractor(),
            ConjoinedVerbExtractor.getExtractor(), VerbPhraseModifierExtractor.getExtractor(),
            AppositiveAndRelativeClauseExtractor.getExtractor());

    public static void main(String[] args) {
        System.out.println(simplifySentence(Joiner.on(' ').join(args)));
    }

    public static Set<String> simplifySentence(String originalSentence) {
        Set<String> sentences = new LinkedHashSet<>();
        sentences.add(originalSentence);
        for (final Extractor extractor : extractors) {
            final Set<String> simplifiedSentences = new HashSet<>();
            for (final String sentence : sentences) {
                simplifiedSentences.addAll(extractor.extract(sentence).getSimplifiedSentences());
            }
            sentences = simplifiedSentences;
        }
        return sentences;
    }
}
