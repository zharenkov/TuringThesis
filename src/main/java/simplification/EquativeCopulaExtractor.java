package simplification;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import generation.TextRealization;
import simplenlg.features.Tense;
import util.TreeUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static generation.TextRealization.realizeSentence;

public class EquativeCopulaExtractor implements Extractor {
    private static EquativeCopulaExtractor extractor;

    private EquativeCopulaExtractor() {
    }

    public static EquativeCopulaExtractor getExtractor() {
        if (extractor == null) {
            extractor = new EquativeCopulaExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
        System.out.println(getExtractor().extract(Joiner.on(' ').join(args)));
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final Tree root = parsed.parse();
        final List<String> lemmas = parsed.lemmas();
        final Set<String> simplifiedSentences = new HashSet<>();
        simplifiedSentences.add(sentence);
        for (int i = 1; i < lemmas.size(); i++) {
            final String lemma = lemmas.get(i);
            if (lemma.equals("be")) {
                final Tree governorTree = TreeUtil.getNpFromWord(root, i - 1);
                final Tree beVpTree = TreeUtil.getVpFromWord(root, root.getLeaves().get(i));
                if (governorTree != null && beVpTree != null && beVpTree.numChildren() > 1) {
                    final Tree firstNonVerbTree = beVpTree.getChild(1);
                    if (TreeUtil.labelEquals(firstNonVerbTree, "np")) {
                        final Tree dependentTree = TreeUtil.getNpFromWord(root, firstNonVerbTree.getLeaves().get(0));
                        final String beforeString = TreeUtil.getStringBeforeTree(root, governorTree);
                        final String afterString = TreeUtil.getStringAfterTree(root, dependentTree);

                        if (Strings.isNullOrEmpty(beforeString) && Strings.isNullOrEmpty(afterString)) {
                            continue;
                        }

                        final String governorString = TreeUtil.constructPhraseFromTree(governorTree);
                        final String dependentString = TreeUtil.constructPhraseFromTree(dependentTree);

                        final String bePosTag = parsed.posTag(i);
                        final String vp;
                        if (bePosTag.equalsIgnoreCase("vbd") || bePosTag.equalsIgnoreCase("vbn")) {
                            vp = TextRealization.realizeVerbPhraseWithFeatures(afterString, false, Tense.PAST);
                        } else {
                            vp = TextRealization.realizeVerbPhraseWithFeatures(afterString, false, Tense.PRESENT);
                        }

                        simplifiedSentences.add(realizeSentence(beforeString, governorString, vp));
                        simplifiedSentences.add(realizeSentence(beforeString, dependentString, vp));
                    }
                }
            }
        }
        return new SimplificationResult(simplifiedSentences);
    }
}
