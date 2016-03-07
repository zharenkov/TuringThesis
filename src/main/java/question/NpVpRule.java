package question;

import com.google.common.base.Joiner;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.NerUtil;
import util.PosUtil;
import util.TreeUtil;

import java.util.HashSet;
import java.util.Set;

import static generation.TextRealization.realizeQuestion;

public class NpVpRule extends Rule {
    private static NpVpRule instance;

    private NpVpRule() {
    }

    public static NpVpRule getRule() {
        if (instance == null) {
            instance = new NpVpRule();
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
        for (int i = 1; i < root.size(); i++) {
            final Tree node = root.getNodeNumber(i);
            for (int k = 0; k < node.numChildren() - 1; k++) {
                final Tree child = node.getChild(k);
                final Tree nextSibling = node.getChild(k + 1);
                if (TreeUtil.labelEquals(child, "np") && TreeUtil.labelEquals(nextSibling, "vp")) {
                    final String wh = NerUtil.getWhFromHead(sentence, root, child);
                    boolean passive = false;
                    boolean past = false;
                    for (final Tree vpLeaf : nextSibling.getLeaves()) {
                        // If we reach a noun then don't look for 'by' afterwards
                        if (PosUtil.isNoun(root, vpLeaf)) {
                            break;
                        }
                        // Passive voice is marked by the word 'by'
                        if (vpLeaf.value().equals("by")) {
                            passive = true;
                        }
                        if (PosUtil.isPastTenseVerb(root, vpLeaf)) {
                            past = true;
                        }
                    }
                    final String verbString = TreeUtil.constructPhraseFromTree(nextSibling);
                    if (passive && !(verbString.startsWith("is") || verbString.startsWith("was"))) {
                        final String vb;
                        if (past) {
                            vb = "was";
                        } else {
                            vb = "is";
                        }
                        questions.add(realizeQuestion(wh, vb, verbString));
                    } else {
                        questions.add(realizeQuestion(wh, verbString));
                    }
                }
            }
        }
    }

    @Override
    protected String getRuleName() {
        return "NP-VP Rule";
    }
}
