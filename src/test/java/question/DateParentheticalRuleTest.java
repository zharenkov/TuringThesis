package question;

import org.junit.BeforeClass;
import org.junit.Test;
import question.when.DateParentheticalRule;

import static question.QuestionAssertions.assertQuestionCreatedWithPunctuation;

public class DateParentheticalRuleTest {
    private static Rule dateParentheticalRule;

    @BeforeClass
    public static void init() {
        dateParentheticalRule = new DateParentheticalRule();
    }

    @Test
    public void testGenerateQuestionsGeorgeWashington() throws Exception {
        assertQuestionCreatedWithPunctuation(dateParentheticalRule,
                "George Washington (February 22, 1732 – December 14, 1799) was the first President of the United States (1789–97), the Commander-in-Chief of the Continental Army during the American Revolutionary War, and one of the Founding Fathers of the United States.",
                "When was George Washington born?", "When did George Washington die?");
    }
}