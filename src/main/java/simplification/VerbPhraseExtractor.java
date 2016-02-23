package simplification;

import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import generation.TextRealization;
import util.WordListUtil;

import java.util.HashSet;
import java.util.Set;

import static util.TreeUtil.getParent;
import static util.TreeUtil.labelEquals;

public class VerbPhraseExtractor implements Extractor {
    private static VerbPhraseExtractor extractor;

    private VerbPhraseExtractor() {
    }

    public static VerbPhraseExtractor getExtractor() {
        if (extractor == null) {
            extractor = new VerbPhraseExtractor();
        }
        return extractor;
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Set<String> simplifiedSentences = new HashSet<>();
        final Sentence parsed = new Sentence(sentence);
        final Tree root = parsed.parse();
        for (int i = 1; i < root.size(); i++) {
            final Tree tree = root.getNodeNumber(i);
            if (labelEquals(tree, "vp")) {
                for (final Tree child : tree.children()) {
                    if (labelEquals(child, "cc")) {
                        final Tree treeParent = getParent(root, tree);
                        final Tree[] siblings = treeParent.children();
                        for (int k = 1; k < siblings.length; k++) {
                            final Tree sibling = siblings[k];
                            if (sibling == tree) {
                                final Tree siblingPrior = siblings[k - 1];
                                if (labelEquals(siblingPrior, "np")) {
                                    final String npString = WordListUtil.constructPhraseFromTree(siblingPrior);
                                    for (final Tree vpChild : tree.children()) {
                                        if (labelEquals(vpChild, "vp")) {
                                            final String vpString = WordListUtil.constructPhraseFromTree(vpChild);
                                            simplifiedSentences.add(
                                                    TextRealization.realizeSentence(npString, vpString));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (simplifiedSentences.isEmpty()) {
            return new SimplificationResult(ImmutableSet.of(sentence));
        }
        return new SimplificationResult(simplifiedSentences);
    }
}
