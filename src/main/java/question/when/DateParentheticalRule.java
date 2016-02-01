package question.when;

import edu.stanford.nlp.trees.Tree;
import question.Rule;
import tagging.ParsedSentence;

import java.util.List;
import java.util.Set;

import static tagging.ParsedSentence.getString;
import static tagging.ParsedSentence.labelEquals;

public class DateParentheticalRule extends Rule {
    @Override
    protected String getRuleName() {
        return "date parenthetical";
    }

    @Override
    protected void findQuestions(Tree tree, ParsedSentence sentence, Set<String> questions) {
        final List<Tree> childrenAsList = tree.getChildrenAsList();
        for (int i = 0; i < childrenAsList.size() - 1; i++) {
            final Tree child = childrenAsList.get(i);
            final Tree nextChild = childrenAsList.get(i + 1);

            if (labelEquals(child, "NP") && sentence.isAPerson(child)) {
                System.out.println("Found a Person NP");
                // PRN has the left and right parenthesis as children 0 and 2 so get child 1 for the actual content
                if (labelEquals(nextChild, "PRN") && prnContainsTwoDates(nextChild.getChild(1), sentence)) {
                    System.out.println("Neighbor is a PRN with two dates");
                    constructQuestion(child, questions);
                }
            }
        }


        for (final Tree child : tree.getChildrenAsList()) {
            findQuestions(child, sentence, questions);
        }
    }

    private boolean prnContainsTwoDates(Tree prn, ParsedSentence sentence) {
        final String string = getString(prn);
        final String[] parts = string.split("-");
        System.out.println("Searching for dates in: " + string);
        int numDates = 0;
        for (String part : parts) {
            // Make sure there are no leading spaces and that commas do not have a space in front of them
            part = part.trim().replace(" ,", ",");
            System.out.print("Examining part \"" + part + "\"... ");
            if (sentence.isADate(part)) {
                System.out.println("is a date");
                numDates++;
            } else {
                System.out.println("is not a date");
            }
        }
        System.out.println("Found " + numDates + " dates");
        return numDates >= 2;
    }

    private void constructQuestion(Tree personNp, Set<String> questions) {
        final String personString = getString(personNp);
        final String questionBirth = "When was " + personString + " born?";
        final String questionDeath = "When did " + personString + " die?";
        System.out.println("Question generated: " + questionBirth);
        System.out.println("Question generated: " + questionDeath);
        questions.add(questionBirth);
        questions.add(questionDeath);
    }
}
