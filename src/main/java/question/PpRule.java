package question;

import com.google.common.base.Joiner;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import simplenlg.features.Tense;
import util.NerUtil;
import util.TenseUtil;
import util.TreeUtil;
import util.Vp;

import java.util.LinkedHashSet;
import java.util.Set;

import static generation.TextRealization.realizeQuestion;

public class PpRule extends Rule {
    private static PpRule instance;

    private PpRule() {
    }

    public static PpRule getRule() {
        if (instance == null) {
            instance = new PpRule();
        }
        return instance;
    }

    public static void main(String[] args) {
        final Set<String> questions = new LinkedHashSet<>();
        getRule().findQuestions(new Sentence(Joiner.on(' ').join(args)), questions);
        System.out.println(questions);
    }

    @Override
    protected void findQuestions(Sentence sentence, Set<String> questions) {
        System.out.println(sentence);
        final Tree root = sentence.parse();
        for (int i = 1; i < root.size(); i++) {
            final Tree node = root.getNodeNumber(i);
            if (TreeUtil.labelEquals(node, "pp")) {
                final Tree parent = TreeUtil.getParent(root, node);
                if (TreeUtil.labelEquals(parent, "vp")) {
                    final Tree ppTree = node;
                    final Tree secondChildOfPp = ppTree.getChild(1);
                    if (TreeUtil.labelEquals(secondChildOfPp, "np")) {
                        System.out.println("NP under PP: " + secondChildOfPp);

                        String wh = null;
                        if (NerUtil.headOfTreeIsLocation(sentence, root, secondChildOfPp)) {
                            System.out.println("NP under PP is a location");
                            wh = "where";
                        } else if (NerUtil.headOfTreeIsTime(sentence, root, secondChildOfPp)) {
                            System.out.println("NP under PP is a date or time");
                            wh = "when";
                        }

                        if (wh != null) {
                            final Vp fullVp = TreeUtil.getFullVpFromTree(root, ppTree);
                            System.out.println("VP above PP: " + fullVp);
                            final Tense tense = TenseUtil.calculateTense(sentence);

                            final String secondWord;
                            final String remainingVp;
                            if (fullVp.hasAuxiliary()) {
                                secondWord = fullVp.getFirstAuxiliary();
                                remainingVp = fullVp.getAllButFirstAuxiliary();
                            } else {
                                if (tense == Tense.PAST) {
                                    secondWord = "did";
                                } else {
                                    secondWord = "do";
                                }
                                final int vpLeafIndex = TreeUtil.getLeafIndex(root, parent.getLeaves().get(0));
                                remainingVp = sentence.lemma(vpLeafIndex);
                            }

                            final String subject = TreeUtil.getStringBeforeTree(root, fullVp.getTree());

                            System.out.printf("Finding string between %s and %s\n", parent.getChild(0), ppTree);
                            final String lastString = TreeUtil.getStringBetweenTrees(root, parent.getChild(0), ppTree);

                            questions.add(realizeQuestion(wh, secondWord, subject, remainingVp, lastString));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected String getRuleName() {
        return "PP Rule";
    }
}
