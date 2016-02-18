package demo;

import com.google.common.base.Joiner;
import question.Rules;

public class QuestionGenerationDemo {
    public static void main(String[] args) {
        final String sentence = Joiner.on(' ').join(args);
        Rules.generateQuestions(sentence);
    }
}
