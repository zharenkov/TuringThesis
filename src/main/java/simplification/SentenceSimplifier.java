package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SentenceSimplifier {
    private static final List<Extractor> extractors = ImmutableList.of(ParentheticalExtractor.getExtractor(),
            AppositiveAndRelativeClauseExtractor.getExtractor(), VerbPhraseExtractor.getExtractor());

    public static void main(String[] args) {
        System.out.println(simplifySentence(Joiner.on(' ').join(args)));
    }

    public static List<String> simplifySentence(String originalSentence) {
        List<String> sentences = new ArrayList<>();
        sentences.add(originalSentence);
        for (final Extractor extractor : extractors) {
            final List<String> simplifiedSentences = new ArrayList<>();
            for (final String sentence : sentences) {
                simplifiedSentences.addAll(extractor.extract(sentence).getSimplifiedSentences());
            }
            sentences = simplifiedSentences;
        }
        return sentences;
    }
}
