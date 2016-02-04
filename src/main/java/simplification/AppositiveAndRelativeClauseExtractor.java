package simplification;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;

import java.util.ArrayList;
import java.util.List;

public class AppositiveAndRelativeClauseExtractor {
    private static final String COMMA = ",";

    /**
     * Returns the given sentence with all non-restrictive appositives and relative clauses removed.
     *
     * @param sentence the given sentence
     * @return the given sentence with all non-restrictive appositives and relative clauses removed
     */
    public static String removeNonRestrictiveAppositivesAndRelativeClauses(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        System.out.println("Original sentence: " + words);

        final SemanticGraph dependencies = parsed.dependencyGraph();
        final List<SemanticGraphEdge> appositivesAndRelativeClauses = new ArrayList<>();
        appositivesAndRelativeClauses.addAll(dependencies.findAllRelns(
                UniversalEnglishGrammaticalRelations.APPOSITIONAL_MODIFIER));
        appositivesAndRelativeClauses.addAll(dependencies.findAllRelns(
                UniversalEnglishGrammaticalRelations.RELATIVE_CLAUSE_MODIFIER));

        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        for (final SemanticGraphEdge edge : appositivesAndRelativeClauses) {
            // IndexedWord index is 1-based not 0-based
            final IndexedWord governor = edge.getGovernor();
            final int governorIndex = governor.index() - 1;
            final IndexedWord dependent = edge.getDependent();
            final int dependentIndex = dependent.index() - 1;

            // See if the dependent is enclosed within commas
            int leftCommaBound = -1;
            int rightCommaBound = words.size() - 1;
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
            // TODO handle the case of appositives and relative clauses occurring before the governor
            if (leftCommaBound <= governorIndex || rightCommaBound <= governorIndex) {
                System.out.println("The appositive/relative clause is not bounded by commas after the governor");
                continue;
            }
            // If the appositive or relative clause is at the end of the sentence, make sure we're not deleting the
            // period.
            if (words.get(rightCommaBound).equals(".")) {
                rightCommaBound--;
            }
            partsToRemove.add(Range.closed(leftCommaBound, rightCommaBound));
        }

        final List<String> answer = WordListUtil.removeParts(words, partsToRemove);
        System.out.println("With appositives and relative clauses removed: " + answer);
        return WordListUtil.constructSentenceFromWordList(answer);
    }
}
