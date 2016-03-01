package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import generation.TextRealization;
import util.WordListUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        final SimplificationResult simplificationResult = getExtractor().extract(Joiner.on(' ').join(args));
        System.out.println(simplificationResult.getSimplifiedSentences());
    }

    @Override
    public SimplificationResult extract(String sentence) {
        System.out.println("Original sentence: " + sentence);
        final Sentence parsed = new Sentence(sentence);
        System.out.println("NER tags: " + parsed.nerTags());
        //final Tree root = parsed.parse();
        final List<String> words = parsed.words();
        final List<String> posTags = parsed.posTags();

        final Set<String> simplifiedSentences = new HashSet<>();
        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        for (int i = 2; i < posTags.size(); i++) {
            final String posTag = posTags.get(i);
            // If the word is a participle
            if (posTag.equalsIgnoreCase("vbg") || posTag.equalsIgnoreCase("vbn")) {
                System.out.printf("Found participle '%s' at index %d\n", words.get(i), i);
                // Look for a participle that comes after a noun phrase and a comma
                if (words.get(i - 1).equals(",") && !posTags.get(i - 2).startsWith("vb")) {
                    final int leftBoundary = i - 1;
                    int rightBoundary = -1;
                    for (int k = i + 1; k < words.size(); k++) {
                        if (WordListUtil.isBoundaryComma(k, parsed)) {
                            rightBoundary = k;
                            break;
                        }
                    }
                    if (rightBoundary == -1) {
                        rightBoundary = words.size() - 1;
                    }
                    rangeSet.add(Range.closed(leftBoundary, rightBoundary));

                    // Disabled for now due to issues identifying the subject of a participial phrase
                    /*final List<String> participialPhrase = words.subList(leftBoundary + 1, rightBoundary - 1);
                    final String participialPhraseString = WordListUtil.constructPhraseFromWordList(participialPhrase);
                    final String nounPhrase = TreeUtil.constructPhraseFromTree(TreeUtil.getNpFromWord(root, i - 2));

                    final String realizedParticipialPhrase = TextRealization.realizeVerbPhraseWithFeatures(
                            participialPhraseString, true, Tense.PAST);
                    System.out.println(nounPhrase + " | " + realizedParticipialPhrase);
                    simplifiedSentences.add(TextRealization.realizeSentence(nounPhrase, realizedParticipialPhrase));*/
                }
            }
        }
        final String phrase = WordListUtil.constructPhraseFromWordList(WordListUtil.removeParts(words, rangeSet));
        simplifiedSentences.add(TextRealization.realizeSentence(phrase));
        return new SimplificationResult(simplifiedSentences);
    }
}
