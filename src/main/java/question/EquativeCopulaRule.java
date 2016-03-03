package question;

import com.google.common.base.Joiner;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import generation.TextRealization;
import util.TreeUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static util.NerUtil.headOfTreeIsPerson;

public class EquativeCopulaRule extends Rule {
    private static EquativeCopulaRule instance;

    private EquativeCopulaRule() {
    }

    public static EquativeCopulaRule getRule() {
        if (instance == null) {
            instance = new EquativeCopulaRule();
        }
        return instance;
    }

    public static void main(String[] args) {
        final HashSet<String> questions = new HashSet<>();
        getRule().findQuestions(new Sentence(Joiner.on(' ').join(args)), questions);
        System.out.println(questions);
    }

    @Override
    protected void findQuestions(Sentence sentence, Set<String> questions) {
        final Tree root = sentence.parse();
        final List<String> words = sentence.words();
        final List<String> lemmas = sentence.lemmas();
        for (int i = 1; i < lemmas.size(); i++) {
            final String lemma = lemmas.get(i);
            if (lemma.equals("be")) {
                final Tree governorTree = TreeUtil.getHighestNpFromWord(root, i - 1);
                System.out.println(governorTree);
                final Tree beVpTree = TreeUtil.getVpFromWord(root, root.getLeaves().get(i));
                System.out.println(beVpTree);
                if (governorTree != null && beVpTree != null && beVpTree.numChildren() > 1) {
                    final Tree dependentTree = beVpTree.getChild(1);
                    if (TreeUtil.labelEquals(dependentTree, "np")) {
                        final String governorString = TreeUtil.constructPhraseFromTree(governorTree);
                        final StringBuilder dependentString = new StringBuilder(
                                TreeUtil.constructPhraseFromTree(dependentTree));
                        for (int k = 2; k < beVpTree.numChildren(); k++) {
                            final Tree child = beVpTree.getChild(k);
                            if (TreeUtil.labelEquals(child, "vp")) {
                                break;
                            }
                            dependentString.append(" ").append(TreeUtil.constructPhraseFromTree(child));
                        }

                        final String wh;
                        if (headOfTreeIsPerson(sentence, root, governorTree) || headOfTreeIsPerson(sentence, root,
                                dependentTree)) {
                            wh = "who";
                        } else {
                            wh = "what";
                        }
                        questions.add(TextRealization.realizeQuestion(wh, words.get(i), governorString));
                        questions.add(TextRealization.realizeQuestion(wh, words.get(i), dependentString.toString()));
                    }
                }
            }
        }
    }

    @Override
    protected String getRuleName() {
        return "Equative Copula Rule";
    }
}
