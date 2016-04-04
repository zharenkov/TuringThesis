package simplification;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import generation.TextRealization;
import util.TreeUtil;

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
        for (int i = 0; i < posTags.size(); i++) {
            final String posTag = posTags.get(i).toLowerCase();
            if (posTag.startsWith("wp") || posTag.equals("wdt")) {
                System.out.println("Found word tagged WP");
                final Tree tree = root.getLeaves().get(i);
                System.out.println(tree);

                final Tree sbar = TreeUtil.getFirstSbar(root, tree);
                if (sbar == null) {
                    System.err.println("Cannot find SBAR");
                    continue;
                }

                System.out.println("Found SBAR: " + sbar);
                final String np = getMainNp(root);
                if (Strings.isNullOrEmpty(np)) {
                    System.err.println("Could not find main NP");
                } else {
                    final String sbarString = TreeUtil.getStringAfterTree(root, tree);
                    System.out.printf("NP: %s  SBAR: %s\n", np, sbarString);
                    simplifiedSentences.add(TextRealization.realizeSentence(np, sbarString));
                }
            }
        }
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
