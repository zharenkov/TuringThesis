package question;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.List;

public class QuestionFinder {
    final static List<Rule> questionRules = Arrays.asList();

    public static void main(String[] args) {
        final String sentence = Joiner.on(' ').join(args);
        for (final Rule rule : questionRules) {
            rule.generateQuestions(sentence);
        }
    }
}
