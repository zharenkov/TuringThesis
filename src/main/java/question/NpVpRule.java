package question;

import com.google.common.base.Joiner;
import edu.stanford.nlp.trees.Tree;
import generation.QuestionGenerator;
import simplenlg.features.InterrogativeType;
import tagging.Sentence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NpVpRule implements Rule {
    @Override
    public Set<String> generateQuestions(Sentence sentence) {
        final Set<String> questions = new HashSet<>();
        System.out.println("Starting NP-VP scanning\n-----------------------------------");
        System.out.println("Sentence: '" + sentence.getString() + "'");
        processTree(sentence.getPosTree(), sentence, questions);
        System.out.println("-----------------------------------\nEnding location scanning");
        return questions;
    }

    private void processTree(Tree posTree, Sentence sentence, Set<String> questions) {
        final List<Tree> children = posTree.getChildrenAsList();
        // Search for a NP followed immediately by a VP
        for (int i = 0; i < children.size() - 1; i++) {
            constructQuestion(children.get(i), children.get(i + 1), sentence, questions);
            processTree(children.get(i), sentence, questions);
        }
        if (children.size() >= 1) {
            processTree(children.get(children.size() - 1), sentence, questions);
        }
    }

    private void constructQuestion(Tree np, Tree vp, Sentence sentence, Set<String> questions) {
        System.out.printf("Checking %s %s\n", np.label(), vp.label());
        if (!np.label().value().equals("NP") || !vp.label().value().equals("VP")) {
            return;
        }
        System.out.println("Found NP followed by VP");
        final String npString = Joiner.on(' ').join(np.getLeaves());
        final String vpString = Joiner.on(' ').join(vp.getLeaves());
        // TODO calculate whether it is a WHO or a WHAT
        final String question = QuestionGenerator.generateNpVpQuestion(npString, vpString,
                InterrogativeType.WHO_SUBJECT);
        System.out.println("Generated question: " + question);
        questions.add(question);
    }
}
