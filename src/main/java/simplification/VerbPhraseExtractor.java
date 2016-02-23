package simplification;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.TreeUtil;
import util.WordListUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static generation.TextRealization.realizeSentence;
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

    public static void main(String[] args) {
        final SimplificationResult simplificationResult = getExtractor().extract(
                "USS Missouri is a United States Navy Iowa-class battleship and was the third ship of the U.S. Navy to be named in honor of the US state of Missouri.");
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
                    if (labelEquals(child, "cc")) {
                        hasConjoinedVpChildren = true;
                    } else if (labelEquals(child, "vp")) {
                        vps.add(child);
                    } else {
                        if (nonVps.length() > 0) {
                            nonVps.append(" ");
                        }
                        nonVps.append(WordListUtil.constructPhraseFromTree(child));
                    }
                }
                if (hasConjoinedVpChildren) {
                    final String stringBeforeTree = TreeUtil.getStringBeforeTree(root, tree);
                    final String stringAfterTree = TreeUtil.getStringAfterTree(root, tree);
                    for (final Tree vp : vps) {
                        final String vpString = WordListUtil.constructPhraseFromTree(vp);
                        simplifiedSentences.add(
                                realizeSentence(stringBeforeTree, vpString, nonVps.toString(), stringAfterTree));
                    }
                }
            }
        }
        return new SimplificationResult(simplifiedSentences);
    }
}
