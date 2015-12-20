package question;

import org.junit.*;
import question.who_what.*;

import static question.QuestionAssertions.assertQuestionCreated;

public class CopulaRuleTest {
    private static Rule copulaRule;

    @BeforeClass
    public static void init() {
        copulaRule = new CopulaRule();
    }

    @Test
    public void testGenerateQuestionsObama() {
        assertQuestionCreated(copulaRule, "Barack Obama is the current president of the United States of America.",
                "Who is the current president of the United States of America?");
    }
}
