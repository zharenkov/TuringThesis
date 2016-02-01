package question;

import org.junit.BeforeClass;
import org.junit.Test;
import question.when.DateParentheticalRule;

import static question.QuestionAssertions.assertNoQuestionsCreated;
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

    @Test
    public void testGenerateQuestionsWilliamAlfred() throws Exception {
        assertQuestionCreatedWithPunctuation(dateParentheticalRule,
                "William Alfred 'Bill' Brown (31 July 1912 – 16 March 2008) was an Australian cricketer who played 22 Tests between 1934 and 1948, captaining his country in one Test.",
                "When was William Alfred 'Bill' Brown born?", "When did William Alfred 'Bill' Brown die?");
    }

    @Test
    public void testGenerateQuestionsUssMissouri() throws Exception {
        assertNoQuestionsCreated(dateParentheticalRule,
                "USS Missouri (BB-63) (\"Mighty Mo\" or \"Big Mo\") is a United States Navy Iowa-class battleship and was the third ship of the U.S. Navy to be named in honor of the US state of Missouri.");
    }
}