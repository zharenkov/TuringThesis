package simplification;

import com.google.common.base.Joiner;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.TreeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static generation.TextRealization.realizeSentence;
import static util.TreeUtil.labelEquals;
import static util.TreeUtil.treeIsAndConjunction;

public class ConjoinedVerbPhraseExtractor implements Extractor {
    private static ConjoinedVerbPhraseExtractor extractor;

    private ConjoinedVerbPhraseExtractor() {
    }

    public static ConjoinedVerbPhraseExtractor getExtractor() {
        if (extractor == null) {
            extractor = new ConjoinedVerbPhraseExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
        final SimplificationResult simplificationResult = getExtractor().extract(Joiner.on(' ').join(args));
        System.out.println(simplificationResult);
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Set<String> simplifiedSentences = new HashSet<>();
        // Keep the original sentence so that we don't lose any information
        simplifiedSentences.add(sentence);
        final Sentence parsed = new Sentence(sentence);
        final Tree root = parsed.parse();
        for (int i = 1; i < root.size(); i++) {
            final Tree tree = root.getNodeNumber(i);
            if (labelEquals(tree, "vp")) {
                boolean hasConjoinedVpChildren = false;
                final List<Tree> vps = new ArrayList<>();
                final StringBuilder nonVps = new StringBuilder();
                for (final Tree child : tree.children()) {
                    if (treeIsAndConjunction(child)) {
                        hasConjoinedVpChildren = true;
                    } else if (labelEquals(child, "vp")) {
                        vps.add(child);
                    } else {
                        if (nonVps.length() > 0) {
                            nonVps.append(" ");
                        }
                        nonVps.append(TreeUtil.constructPhraseFromTree(child));
                    }
                }
                if (hasConjoinedVpChildren) {
                    final String stringBeforeTree = TreeUtil.getStringBeforeTree(root, tree);
                    final String stringAfterTree = TreeUtil.getStringAfterTree(root, tree);
                    for (final Tree vp : vps) {
                        final String vpString = TreeUtil.constructPhraseFromTree(vp);
                        simplifiedSentences.add(
                                realizeSentence(stringBeforeTree, vpString, nonVps.toString(), stringAfterTree));
                    }
                }
            }
        }
        return new SimplificationResult(simplifiedSentences);
    }
}
