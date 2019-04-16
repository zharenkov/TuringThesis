package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import data.Text;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.TreeUtil;
import util.WordListUtil;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static util.TreeUtil.labelEquals;

public class SentenceSimplifier {
    private static final Map<String, String> STRING_REPLACEMENTS = ImmutableMap.of("\"", "", ": ", " ", " : ", " ");

    private static final List<Extractor> allextractors = ImmutableList.of(ExistentialIgnore.getExtractor(),
            ParentheticalExtractor.getExtractor(), AppositiveExtractor.getExtractor(),
            ConjoinedVerbPhraseExtractor.getExtractor(), ConjoinedVerbExtractor.getExtractor(),
            VerbPhraseModifierExtractor.getExtractor(), RelativeClauseExtractor.getExtractor(),
            ParticipialModifiersExtractor.getExtractor(), PrepositionalPhraseExtractor.getExtractor(),
            SbarWhExtractor.getExtractor(), SubVpExtractor.getExtractor());

    private static final List<Extractor> paranthesisExtractors = ImmutableList.of(ParentheticalExtractor.getExtractor());

    public static void main(String[] args) {
//        System.out.println(simplifySentence(Joiner.on(' ').join(args)));
    }

    public static Set<Text> simplifyParanteticalSentence(String originalSentence) {
        return doSimplification(originalSentence, paranthesisExtractors);
    }

    public static Set<Text> simplifySentence(String originalSentence) {
        return doSimplification(originalSentence, allextractors);
    }

    public static Set<Text> doSimplification(String originalSentence, List<Extractor> extractors) {

        Set<String> sentences = new LinkedHashSet<>();
        sentences.add(preCleanSentence(originalSentence));
        for (final Extractor extractor : extractors) {
            final Set<String> simplifiedSentences = new HashSet<>();
            for (final String sentence : sentences) {
                simplifiedSentences.addAll(extractor.extract(sentence).getSimplifiedSentences());
            }
            sentences = simplifiedSentences;
        }
        final Set<Text> texts = new LinkedHashSet<>();
        for (final String simplifiedSentence : sentences) {
            texts.add(postCleanSentence(simplifiedSentence));
        }
        return texts;
    }

    private static String preCleanSentence(String originalSentence) {
        String modifiedSentence = originalSentence;
        // Group quoted text
        final StringBuilder groupedStringBuilder = new StringBuilder();
        boolean inQuotes = false;
        for (final char ch : modifiedSentence.toCharArray()) {
            if (ch == '"') {
                inQuotes = !inQuotes;
                continue;
            }

            if (inQuotes) {
                if (ch == ' ') {
                    groupedStringBuilder.append('_');
                } else {
                    groupedStringBuilder.append(ch);
                }
            } else {
                groupedStringBuilder.append(ch);
            }
        }
        modifiedSentence = groupedStringBuilder.toString();

        // Replace all of the banned strings
        for (final Entry<String, String> stringReplacement : STRING_REPLACEMENTS.entrySet()) {
            modifiedSentence = modifiedSentence.replaceAll(stringReplacement.getKey(), stringReplacement.getValue());
        }

        // Remove IPA
        final Sentence sentence = new Sentence(modifiedSentence);
        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        final Tree root = sentence.parse();
        for (int i = 1; i < root.size(); i++) {
            final Tree tree = root.getNodeNumber(i);
            if (labelEquals(tree, "prn")) {
                final Tree firstLeaf = tree.getLeaves().get(0);
                final Tree lastLeaf = Iterables.getLast(tree.getLeaves());
                if (labelEquals(firstLeaf, "/") && labelEquals(lastLeaf, "/")) {
//                    System.out.println("Remove IPA: " + tree);
                    final int left = TreeUtil.getLeafIndex(root, firstLeaf);
                    final int right = TreeUtil.getLeafIndex(root, lastLeaf);
                    partsToRemove.add(Range.closed(left, right));
                }
            }
        }
        return WordListUtil.constructPhraseFromWordList(WordListUtil.removeParts(sentence.words(), partsToRemove));
    }

    private static Text postCleanSentence(String sentence) {
        return new Text(sentence.replaceAll(" -- ", "-"));
    }
}
