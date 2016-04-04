package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import data.Text;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.TreeUtil;
import util.WordListUtil;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static util.TreeUtil.labelEquals;

public class SentenceSimplifier {
    private static final Set<Character> BANNED_CHARACTERS = ImmutableSet.of('\"', ':');

    private static final List<Extractor> extractors = ImmutableList.of(ExistentialIgnore.getExtractor(),
            ParentheticalExtractor.getExtractor(), AppositiveExtractor.getExtractor(),
            ConjoinedVerbPhraseExtractor.getExtractor(), ConjoinedVerbExtractor.getExtractor(),
            VerbPhraseModifierExtractor.getExtractor(), RelativeClauseExtractor.getExtractor(),
            ParticipialModifiersExtractor.getExtractor(), PrepositionalPhraseExtractor.getExtractor(),
            SbarWhExtractor.getExtractor());

    public static void main(String[] args) {
        System.out.println(simplifySentence(Joiner.on(' ').join(args)));
    }

    public static Set<Text> simplifySentence(String originalSentence) {
        Set<String> sentences = new LinkedHashSet<>();
        String modifiedSentence = originalSentence;
        for (final char bannedCharacter : BANNED_CHARACTERS) {
            modifiedSentence = modifiedSentence.replaceAll("" + bannedCharacter, "");
        }
        sentences.add(preCleanSentence(modifiedSentence));
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
        final Sentence sentence = new Sentence(originalSentence);
        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        final Tree root = sentence.parse();
        for (int i = 1; i < root.size(); i++) {
            final Tree tree = root.getNodeNumber(i);
            if (labelEquals(tree, "prn")) {
                final Tree firstLeaf = tree.getLeaves().get(0);
                final Tree lastLeaf = Iterables.getLast(tree.getLeaves());
                if (labelEquals(firstLeaf, "/") && labelEquals(lastLeaf, "/")) {
                    System.out.println("Remove IPA: " + tree);
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
