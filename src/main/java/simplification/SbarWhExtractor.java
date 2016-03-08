package simplification;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import generation.TextRealization;
import util.TreeUtil;
import util.WordListUtil;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SbarWhExtractor implements Extractor {
    private static SbarWhExtractor extractor;

    private SbarWhExtractor() {
    }

    public static SbarWhExtractor getExtractor() {
        if (extractor == null) {
            extractor = new SbarWhExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
        System.out.println(getExtractor().extract(Joiner.on(' ').join(args)));
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        System.out.println(parsed.words());
        final Tree root = parsed.parse();
        final List<String> posTags = parsed.posTags();
        System.out.println(posTags);
        final Set<String> simplifiedSentences = new LinkedHashSet<>();
        simplifiedSentences.add(sentence);
        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        for (int i = 0; i < posTags.size(); i++) {
            final String posTag = posTags.get(i).toLowerCase();
            if (posTag.startsWith("wp") || posTag.equals("wdt")) {
                System.out.println("Found word tagged WP");
                final Tree tree = root.getLeaves().get(i);
                final Tree grandparent = TreeUtil.getParent(root, tree, 3);
                final Tree greatGrandparent = TreeUtil.getParent(root, tree, 4);

                final Tree sbar;
                if (TreeUtil.labelEquals(grandparent, "sbar")) {
                    sbar = grandparent;
                } else {
                    sbar = greatGrandparent;
                }

                if (TreeUtil.labelEquals(sbar, "sbar")) {
                    System.out.println("Found SBAR: " + sbar);
                    final String np = getMainNp(root);
                    if (Strings.isNullOrEmpty(np)) {
                        System.err.println("Could not find main NP");
                    } else {
                        final String sbarString = TreeUtil.getStringAfterTree(root, tree);
                        partsToRemove.add(TreeUtil.getRangeOfTree(root, sbar));
                        simplifiedSentences.add(TextRealization.realizeSentence(np, sbarString));
                    }
                }
            }
        }
        simplifiedSentences.add(
                WordListUtil.constructPhraseFromWordList(WordListUtil.removeParts(parsed.words(), partsToRemove)));
        return new SimplificationResult(simplifiedSentences);
    }

    private static String getMainNp(Tree root) {
        System.out.println("Finding NP from " + Arrays.toString(root.getChild(0).children()));
        for (final Tree tree : root.getChild(0).children()) {
            if (TreeUtil.labelEquals(tree, "np")) {
                return TreeUtil.constructPhraseFromTree(tree);
            }
        }
        return null;
    }
}
