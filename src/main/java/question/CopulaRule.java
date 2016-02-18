package question;

import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.ReversePhraseBuilder;
import util.WordListUtil;

import java.util.List;
import java.util.Set;

import static util.TreeUtil.getParent;

public class CopulaRule extends Rule {
    private static final Set<String> COPULAS = ImmutableSet.of("be");

    private static CopulaRule instance;

    private CopulaRule() {
    }

    public static CopulaRule getRule() {
        if (instance == null) {
            instance = new CopulaRule();
        }
        return instance;
    }

    @Override
    protected void findQuestions(Sentence sentence, Set<String> questions) {
        final List<String> lemmas = sentence.lemmas();
        System.out.println(lemmas);
        for (int i = 0; i < lemmas.size(); i++) {
            final String lemma = lemmas.get(i);
            if (COPULAS.contains(lemma)) {
                System.out.println("Copula detected at index " + i);
                final Tree root = sentence.parse();
                final Tree verbTree = root.getLeaves().get(i);
                Tree verbPhraseTree = getParent(root, verbTree, 2);
                final Tree rightChildOfVp = verbPhraseTree.getChild(1);
                if (rightChildOfVp.label().value().equalsIgnoreCase("np")) {
                    final String rightNp = WordListUtil.constructPhraseFromTree(rightChildOfVp);
                    final ReversePhraseBuilder verbPhrase = new ReversePhraseBuilder();

                    Tree upperMostVerbPhrase = verbPhraseTree;
                    Tree phraseAboveUpperMostVerbPhrase = verbPhraseTree;
                    while (phraseAboveUpperMostVerbPhrase.label().value().equalsIgnoreCase("vp")) {
                        verbPhrase.addString(phraseAboveUpperMostVerbPhrase.getLeaves().get(0).label().value());
                        upperMostVerbPhrase = phraseAboveUpperMostVerbPhrase;
                        phraseAboveUpperMostVerbPhrase = getParent(root, phraseAboveUpperMostVerbPhrase);
                    }

                    final List<Tree> children = phraseAboveUpperMostVerbPhrase.getChildrenAsList();
                    final int indexOfUpperMostVerbPhrase = phraseAboveUpperMostVerbPhrase.objectIndexOf(
                            upperMostVerbPhrase);
                    final Tree leftOfVerbPhrase = children.get(indexOfUpperMostVerbPhrase - 1);
                    if (leftOfVerbPhrase.label().value().equalsIgnoreCase("np")) {
                        final String leftNp = WordListUtil.constructPhraseFromTree(leftOfVerbPhrase);
                        // TODO replace leftNp and rightNp with wh-
                    }
                }
            }
        }
    }

    @Override
    protected String getRuleName() {
        return "Copula Rule";
    }
}
