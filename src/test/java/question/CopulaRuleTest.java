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

    @Test
    public void testGenerateQuestionsLeopold() {
        assertQuestionCreated(copulaRule, "Leopold II was Holy Roman Emperor from 1790 to 1792.",
                "Who was Holy Roman Emperor from 1790 to 1792?");
    }

    @Test
    public void testGenerateQuestionsCulprit() {
        assertQuestionCreated(copulaRule, "Paracetamol is a widely used pain medication to reduce fever.",
                "What is a widely used pain medication to reduce fever?");
    }
}
