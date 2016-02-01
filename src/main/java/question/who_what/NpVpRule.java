package question.who_what;

import com.google.common.base.Joiner;
import edu.stanford.nlp.trees.Tree;
import generation.QuestionGenerator;
import question.Rule;
import simplenlg.features.InterrogativeType;
import tagging.ParsedSentence;

import java.util.List;
import java.util.Set;

public class NpVpRule extends Rule {
    @Override
    protected String getRuleName() {
        return "np-vp";
    }

    @Override
    protected void findQuestions(Tree tree, ParsedSentence sentence, Set<String> questions) {
        final List<Tree> children = tree.getChildrenAsList();
        // Search for a NP followed immediately by a VP
        for (int i = 0; i < children.size() - 1; i++) {
            constructQuestion(children.get(i), children.get(i + 1), sentence, questions);
            findQuestions(children.get(i), sentence, questions);
        }
        if (children.size() >= 1) {
            findQuestions(children.get(children.size() - 1), sentence, questions);
        }
    }

    private void constructQuestion(Tree np, Tree vp, ParsedSentence sentence, Set<String> questions) {
        System.out.printf("Checking %s %s\n", np.label(), vp.label());
        if (!np.label().value().equals("NP") || !vp.label().value().equals("VP")) {
            return;
        }
        System.out.println("Found NP followed by VP");
        final String npString = Joiner.on(' ').join(np.getLeaves());
        final String vpString = Joiner.on(' ').join(vp.getLeaves());
        final InterrogativeType type = SubjectIdentifier.findInterrogativeTypeSubject(sentence, npString);

        final String question = QuestionGenerator.generateNpVpQuestion(npString, vpString, type);
        System.out.println("Generated question: " + question);
        questions.add(question);
    }
}
