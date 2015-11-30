package question;

import static com.google.common.truth.Truth.assertThat;
import static tagging.StanfordParser.parseSentence;


/**
 * Static helper methods for making assertions about questions generated from rules.
 */
class QuestionAssertions {
    static void assertQuestionCreated(Rule rule, String sentence, String... question) {
        assertThat(rule.generateQuestions(parseSentence(sentence))).containsExactly(question);
    }

    static void assertNoQuestionsCreated(Rule rule, String sentence) {
        assertThat(rule.generateQuestions(parseSentence(sentence))).isEmpty();
    }
}
