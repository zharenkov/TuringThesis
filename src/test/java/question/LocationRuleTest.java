package question;

import org.junit.BeforeClass;
import org.junit.Test;

import static question.QuestionAssertions.assertNoQuestionsCreated;
import static question.QuestionAssertions.assertQuestionCreated;

public class LocationRuleTest {
    private static Rule locationRule;

    @BeforeClass
    public static void init() {
        locationRule = new LocationRule();
    }

    @Test
    public void testGenerateQuestionsWashington() {
        assertQuestionCreated(locationRule, "George Washington was born in Virginia",
                "Where was George Washington born?");
    }

    @Test
    public void testGenerateQuestionsFremantle() {
        assertQuestionCreated(locationRule, "Fremantle Prison is located in Australia",
                "Where is Fremantle Prison located?");
    }

    @Test
    public void testGenerateQuestionsBecket() {
        assertQuestionCreated(locationRule, "Saint Thomas Becket was murdered in Canterbury Cathedral",
                "Where was Saint Thomas Becket murdered?");
    }

    @Test
    public void testGenerateQuestionsAstrodome() {
        assertQuestionCreated(locationRule, "The Astrodome is located in Houston, Texas",
                "Where is the Astrodome located?");
    }

    @Test
    public void testGenerateQuestionsInAbsentia() {
        assertNoQuestionsCreated(locationRule, "The senator voted in absentia");
    }

    @Test
    public void testGenerateQuestionsInAgony() {
        assertNoQuestionsCreated(locationRule, "The actor yelled in agony");
    }
}