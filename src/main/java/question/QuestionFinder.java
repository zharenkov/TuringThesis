package question;

import com.google.common.base.*;
import question.where.*;
import question.who_what.*;
import tagging.*;

import java.util.*;

public class QuestionFinder {
    final static List<Rule> questionRules = Arrays.asList(new LocationRule(), new CopulaRule(), new NpVpRule());

    public static void main(String[] args) {
        final String sentence = Joiner.on(' ').join(args);
        final Sentence parsedSentence = StanfordParser.parseSentence(sentence, true);
        for(final Rule rule : questionRules) {
            rule.generateQuestions(parsedSentence);
        }
    }
}
