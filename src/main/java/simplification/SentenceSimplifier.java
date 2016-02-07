package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SentenceSimplifier {
    private static final List<Extractor> extractors = ImmutableList.of(
            AppositiveAndRelativeClauseExtractor.getExtractor());
    public static void main(String[] args) {
        List<String> sentences = new ArrayList<>();
        sentences.add(Joiner.on(' ').join(args));
        for (final Extractor extractor : extractors) {
            final List<String> simplifiedSentences = new ArrayList<>();
            for (final String sentence : sentences) {
                simplifiedSentences.addAll(extractor.extract(sentence).getSimplifiedSentences());
            }
            sentences = simplifiedSentences;
        }
        System.out.println(sentences);
    }
}
