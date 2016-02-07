package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;

import java.util.*;

import static edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations.APPOSITIONAL_MODIFIER;
import static edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations.RELATIVE_CLAUSE_MODIFIER;
import static simplification.TenseUtil.calculateTense;

public class AppositiveAndRelativeClauseExtractor implements Extractor {
    private static final String COMMA = ",";
    private static AppositiveAndRelativeClauseExtractor extractor;

    private AppositiveAndRelativeClauseExtractor() {
    }

    public static AppositiveAndRelativeClauseExtractor getExtractor() {
        if (extractor == null) {
            extractor = new AppositiveAndRelativeClauseExtractor();
        }
        return extractor;
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        System.out.println("Original sentence: " + words);

        final SemanticGraph dependencies = parsed.dependencyGraph();
        final List<SemanticGraphEdge> appositivesAndRelativeClauses = new ArrayList<>();
        appositivesAndRelativeClauses.addAll(dependencies.findAllRelns(APPOSITIONAL_MODIFIER));
        appositivesAndRelativeClauses.addAll(dependencies.findAllRelns(RELATIVE_CLAUSE_MODIFIER));
        System.out.println("Relations: " + appositivesAndRelativeClauses);

        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        final Set<String> simplifiedSentences = new HashSet<>();
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
            final Range<Integer> dependentRange = Range.closed(leftCommaBound, rightCommaBound);
            partsToRemove.add(dependentRange);

            simplifiedSentences.addAll(generateSimplifiedSentences(edge, parsed, dependentRange));
        }

        final List<String> answer = WordListUtil.removeParts(words, partsToRemove);
        System.out.println("With appositives and relative clauses removed: " + answer);
        final String simplifiedSentence = WordListUtil.constructSentenceFromWordList(answer);
        simplifiedSentences.add(simplifiedSentence);
        System.out.println("Simplified Sentences: " + simplifiedSentences);
        return new SimplificationResult(simplifiedSentences);
    }

    private static Set<String> generateSimplifiedSentences(SemanticGraphEdge edge, Sentence sentence, Range<Integer> dependentRange) {
        final List<String> dependentWordList = sentence.words().subList(dependentRange.lowerEndpoint(),
                dependentRange.upperEndpoint() + 1);
        final List<String> dependentWordListNoPunctuation = new ArrayList<>();
        for (final String word : dependentWordList) {
            if (!word.matches("\\p{Punct}")) {
                dependentWordListNoPunctuation.add(word);
            }
        }
        final String dependentString = WordListUtil.constructSentenceFromWordList(dependentWordListNoPunctuation);

        final IndexedWord governor = edge.getGovernor();
        final Tree root = sentence.parse();
        final Tree governorNp = TreeUtil.getNpFromWord(root, governor);
        final String governorNpString = WordListUtil.constructPhraseFromTree(governorNp);

        final Tense tense = calculateTense(sentence);
        final String be;
        if (tense == Tense.PAST) {
            be = "was";
        } else {
            be = "is";
        }

        if (edge.getRelation().equals(APPOSITIONAL_MODIFIER)) {
            return ImmutableSet.of(Joiner.on(' ').join(governorNpString, be, dependentString) + ".");
        } else if (edge.getRelation().equals(RELATIVE_CLAUSE_MODIFIER)) {

        }
        return Collections.emptySet();
    }
}
