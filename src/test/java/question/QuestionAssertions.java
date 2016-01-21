package question;

import static com.google.common.truth.Truth.*;
import static tagging.StanfordCoreNlpClient.parseSentence;


/**
 * Static helper methods for making assertions about questions generated from rules.
 */
class QuestionAssertions {
    static void assertQuestionCreated(Rule rule, String sentence, String... question) {
        assertThat(rule.generateQuestions(parseSentence(sentence, true))).containsExactly(question);
    }

    static void assertNoQuestionsCreated(Rule rule, String sentence) {
        assertThat(rule.generateQuestions(parseSentence(sentence, true))).isEmpty();
    }

    static void assertQuestionCreatedWithPunctuation(Rule rule, String sentence, String... question) {
        assertThat(rule.generateQuestions(parseSentence(sentence, false))).containsExactly(question);
    }

    static void assertNoQuestionsCreatedWithPunctuation(Rule rule, String sentence) {
        assertThat(rule.generateQuestions(parseSentence(sentence, false))).isEmpty();
    }
}
