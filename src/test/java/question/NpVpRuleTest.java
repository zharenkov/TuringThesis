package question;

import org.junit.BeforeClass;
import org.junit.Test;

import static question.QuestionAssertions.assertQuestionCreated;

public class NpVpRuleTest {
    private static Rule npVpRule;

    @BeforeClass
    public static void init() {
    }

    @Test
    public void testGenerateQuestionsWashington() {
        assertQuestionCreated(npVpRule, "George Washington was the first president of the United States.",
                "Who was the first president of the United States?");
    }

    @Test
    public void testGenerateQuestionsBooth() {
        assertQuestionCreated(npVpRule, "John Wilkes Booth assassinated president Abraham Lincoln.",
                "Who assassinated president Abraham Lincoln?");
    }

    @Test
    public void testGenerateQuestionsDisease() {
        assertQuestionCreated(npVpRule, "Infectious diseases caused the most deaths in America in the year 1900.",
                "What caused the most deaths in America in the year 1900?");
    }
}
