package question;

import org.junit.BeforeClass;
import org.junit.Test;
import question.who_what.*;

import static question.QuestionAssertions.assertQuestionCreated;

public class NpVpRuleTest {
    private static Rule npVpRule;

    @BeforeClass
    public static void init() {
        npVpRule = new NpVpRule();
    }

    @Test
    public void testGenerateQuestionsWashington() {
        assertQuestionCreated(npVpRule, "George Washington was the first president of the United States.",
                "Who was the first president of the United States?");
    }

    @Test
    public void testGenerateQuestionsBooth() {
        assertQuestionCreated(npVpRule, "John Wilkes Booth killed president Abraham Lincoln in 1865.",
                "Who killed president Abraham Lincoln in 1865?");
    }

    @Test
    public void testGenerateQuestionsDisease() {
        assertQuestionCreated(npVpRule, "Infectious diseases caused the most deaths in America in the year 1900.",
                "What caused the most deaths in America in the year 1900?");
    }
}
