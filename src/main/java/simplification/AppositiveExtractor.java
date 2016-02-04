package simplification;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;

import java.util.List;

public class AppositiveExtractor {
    private static final String COMMA = ",";

    /**
     * Returns the given sentence with all non-restrictive appositives removed.
     *
     * @param sentence the given sentence
     * @return the given sentence with all non-restrictive appositives removed
     */
    public static String removeNonRestrictiveAppositives(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        System.out.println("Original sentence: " + words);

        final SemanticGraph dependencies = parsed.dependencyGraph();
        final List<SemanticGraphEdge> appositiveRelations = dependencies.findAllRelns(
                UniversalEnglishGrammaticalRelations.APPOSITIONAL_MODIFIER);

        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        for (final SemanticGraphEdge edge : appositiveRelations) {
            // IndexedWord index is 1-based not 0-based
            final IndexedWord governor = edge.getGovernor();
            final int governorIndex = governor.index() - 1;
            final IndexedWord dependent = edge.getDependent();
            final int dependentIndex = dependent.index() - 1;

            // See if the dependent is enclosed within commas
            int leftCommaBound = -1;
            int rightCommaBound = -1;
            for (int i = dependentIndex; i >= 0; i--) {
                if (words.get(i).equals(COMMA)) {
                    leftCommaBound = i;
                    break;
                }
            }
            for (int i = dependentIndex; i < words.size(); i++) {
                if (words.get(i).equals(COMMA)) {
                    rightCommaBound = i;
                    break;
                }
            }
            // TODO handle the case of appositives occurring before the governor
            if (leftCommaBound <= governorIndex || rightCommaBound <= governorIndex) {
                System.out.println("The appositive is not bounded by commas after the governor");
                continue;
            }
            partsToRemove.add(Range.closed(leftCommaBound, rightCommaBound));
        }

        final List<String> answer = WordListUtil.removeParts(words, partsToRemove);
        System.out.println("With appositives removed: " + answer);
        return WordListUtil.constructSentenceFromWordList(answer);
    }
}
