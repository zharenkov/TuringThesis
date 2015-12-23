package question;

import org.junit.*;
import question.who_what.*;

import static question.QuestionAssertions.*;

public class AppositiveRuleTest {
    private static Rule appositiveRule;

    @BeforeClass
    public static void init() {
        appositiveRule = new AppositiveRule();
    }

    @Test
    public void testGenerateQuestionsObama() {
        assertQuestionCreatedWithPunctuation(appositiveRule,
                "Barack Obama, the 44th president of the United States, was born in 1961.",
                "What is Barack Obama?", "Who is the 44th president of the United States?");
    }

    @Test
    public void testGenerateQuestionsTennis() {
        // TODO think of a way to make the 'A' not capitalized when generating the question
        assertQuestionCreatedWithPunctuation(appositiveRule,
                "A hot-tempered tennis player, Robbie began to charge the umpire after the bad call.",
                "What is Robbie?", "Who is A hot-tempered tennis player?");
    }
}
