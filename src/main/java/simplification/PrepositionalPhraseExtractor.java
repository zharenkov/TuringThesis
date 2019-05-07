package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.TreeUtil;
import util.WordListUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrepositionalPhraseExtractor implements Extractor {
    private static PrepositionalPhraseExtractor extractor;

    private PrepositionalPhraseExtractor() {
    }

    public static PrepositionalPhraseExtractor getExtractor() {
        if (extractor == null) {
            extractor = new PrepositionalPhraseExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
        final SimplificationResult simplificationResult = getExtractor().extract(Joiner.on(' ').join(args));
//        System.out.println(simplificationResult);
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        final Tree root = parsed.parse();
        final List<Tree> prepositions = new ArrayList<>();
        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        final Set<String> simplifiedSentences = new HashSet<>();
        for (int i = 1; i < root.size(); i++) {
            final Tree tree = root.getNodeNumber(i);
            if (TreeUtil.labelEquals(tree, "pp")) {
                final List<Tree> leaves = tree.getLeaves();
                final int leftIndex = TreeUtil.getLeafIndex(root, leaves.get(0));
                final int rightIndex = TreeUtil.getLeafIndex(root, leaves.get(leaves.size() - 1));
                if (leftIndex == 0) {
                    if (inBoundsAndEquals(words, rightIndex + 1, ",")) {
                        partsToRemove.add(Range.closed(0, rightIndex + 1));
                        prepositions.add(tree);
                    }
                } else {
                    if (inBoundsAndEquals(words, rightIndex + 1, ",") && inBoundsAndEquals(words, leftIndex - 1, ",")) {
                        partsToRemove.add(Range.closed(leftIndex - 1, rightIndex + 1));
                        prepositions.add(tree);
                    }
                }
            }
        }

        if (partsToRemove.isEmpty()) {
            simplifiedSentences.add(sentence);
            return new SimplificationResult(simplifiedSentences);
        }

        final List<String> newWords = WordListUtil.removeParts(words, partsToRemove);
        while (newWords.get(newWords.size() - 1).matches("\\p{Punct}")) {
            newWords.remove(newWords.size() - 1);
        }
        final StringBuilder modifiedSentence = new StringBuilder(WordListUtil.constructPhraseFromWordList(newWords));
        for (final Tree preposition : prepositions) {
            modifiedSentence.append(" ").append(lowerCaseFirstLetter(TreeUtil.constructPhraseFromTree(preposition)));
        }
        modifiedSentence.append(".");
        simplifiedSentences.add(modifiedSentence.toString());

        return new SimplificationResult(simplifiedSentences);
    }

    private static boolean inBoundsAndEquals(List<String> words, int index, String expected) {
        return index >= 0 && index < words.size() && words.get(index).equalsIgnoreCase(expected);
    }

    private static String lowerCaseFirstLetter(String string) {
        return string;//Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }
}
