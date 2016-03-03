package question;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rules {
    // Add all rules that should be used to generate questions here
    final static List<Rule> questionRules = Arrays.asList(EquativeCopulaRule.getRule());

    public static Set<String> generateQuestions(String simplifiedSentence) {
        final Set<String> generatedQuestions = new HashSet<>();
        for (final Rule rule : questionRules) {
            final Set<String> questions = rule.generateQuestions(simplifiedSentence);
            generatedQuestions.addAll(questions);
        }
        return generatedQuestions;
    }
}
