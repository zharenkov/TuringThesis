package question;

import com.google.common.base.Joiner;
import tagging.Sentence;
import tagging.StanfordParser;

import java.util.Arrays;
import java.util.List;

public class QuestionGenerator {
    final static StanfordParser parser = new StanfordParser();
    final static List<Rule> questionRules = Arrays.asList(new LocationRule());

    public static void main(String[] args) {
        final String sentence = Joiner.on(' ').join(args);
        final Sentence parsedSentence = parser.parseSentence(sentence);
        for(final Rule rule : questionRules) {
            rule.generateQuestions(parsedSentence);
        }
    }
}
