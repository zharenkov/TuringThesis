package question;

import data.Text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rules {
    // Add all rules that should be used to generate questions here
    final static List<Rule> questionRules = Arrays.asList(EquativeCopulaRule.getRule(),
            AttributiveCopulaRule.getRule(), NpVpRule.getRule(), PpRule.getRule());

    public static Set<Text> generateQuestions(String simplifiedSentence) {
        final Set<Text> generatedQuestions = new HashSet<>();
        for (final Rule rule : questionRules) {
            final Set<String> questions = rule.generateQuestions(simplifiedSentence);
            for (final String question : questions) {
                generatedQuestions.add(new Text(question));
            }
        }
        return generatedQuestions;
    }
}
