package question;

import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import tagging.StanfordParser;

import static org.junit.Assert.assertThat;

/**
 * Static helper methods for making assertions about questions generated from rules.
 */
class QuestionAssertions {
    static void assertQuestionCreated(Rule rule, String sentence, String... question) {
        assertThat(rule.generateQuestions(StanfordParser.parseSentence(sentence)),
                IsIterableContainingInAnyOrder.containsInAnyOrder(question));
    }

    static void assertNoQuestionsCreated(Rule rule, String sentence) {
        assertThat(rule.generateQuestions(StanfordParser.parseSentence(sentence)),
                IsEmptyIterable.emptyIterable());
    }
}
