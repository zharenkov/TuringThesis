package demo;

import com.google.common.base.Joiner;
import question.CopulaRule;
import question.Rule;

import java.util.Arrays;
import java.util.List;

public class QuestionGenerationDemo {
    // Add all rules that should be used to generate questions here
    final static List<Rule> questionRules = Arrays.asList(CopulaRule.getRule());

    public static void main(String[] args) {
        final String sentence = Joiner.on(' ').join(args);
        for (final Rule rule : questionRules) {
            rule.generateQuestions(sentence);
        }
    }
}
