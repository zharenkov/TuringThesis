package question;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.BeforeClass;
import org.junit.Test;
import tagging.StanfordParser;

import static org.junit.Assert.assertThat;

public class LocationRuleTest {
    private static Rule locationRule;

    @BeforeClass
    public static void init() {
        locationRule = new LocationRule();
    }

    @Test
    public void testGenerateQuestionsWashington() {
        assertQuestionCreated("George Washington was born in Virginia", "Where was George Washington born?");
    }

    @Test
    public void testGenerateQuestionsFremantle() {
        assertQuestionCreated("Fremantle Prison is located in Australia", "Where is Fremantle Prison located?");
    }

    @Test
    public void testGenerateQuestionsBecket() {
        assertQuestionCreated("Saint Thomas Becket was murdered in Canterbury Cathedral",
                "Where was Saint Thomas Becket murdered?");
    }

    @Test
    public void testGenerateQuestionsAstrodome() {
        assertQuestionCreated("The Astrodome is located in Houston, Texas",
                "Where is the Astrodome located?");
    }

    private void assertQuestionCreated(String sentence, String... question) {
        assertThat(locationRule.generateQuestions(StanfordParser.parseSentence(sentence)),
                IsIterableContainingInAnyOrder.containsInAnyOrder(question));
    }
}