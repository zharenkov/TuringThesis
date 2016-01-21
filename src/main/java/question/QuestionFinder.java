package question;

import com.google.common.base.Joiner;
import question.where.LocationRule;
import question.who_what.CopulaRule;
import question.who_what.NpVpRule;
import tagging.Sentence;
import tagging.StanfordCoreNlpClient;

import java.util.Arrays;
import java.util.List;

public class QuestionFinder {
    final static List<Rule> questionRules = Arrays.asList(new LocationRule(), new CopulaRule(), new NpVpRule());

    public static void main(String[] args) {
        final String sentence = Joiner.on(' ').join(args);
        final Sentence parsedSentence = StanfordCoreNlpClient.parseSentence(sentence, true);
        for (final Rule rule : questionRules) {
            rule.generateQuestions(parsedSentence);
        }
    }
}
