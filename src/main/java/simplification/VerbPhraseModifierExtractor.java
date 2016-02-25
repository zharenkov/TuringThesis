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
import util.WordListUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VerbPhraseModifierExtractor implements Extractor {
    private static final Set<String> MODIFIER_TYPES = ImmutableSet.of("nmod");
    private static final String COMMA = ",";

    private static VerbPhraseModifierExtractor extractor;

    private VerbPhraseModifierExtractor() {
    }

    public static VerbPhraseModifierExtractor getExtractor() {
        if (extractor == null) {
            extractor = new VerbPhraseModifierExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
        getExtractor().extract(Joiner.on(' ').join(args));
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        System.out.println("Original sentence: " + words);
        final SemanticGraph dependencies = parsed.dependencyGraph();

        final List<SemanticGraphEdge> modifiers = new ArrayList<>();
        for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
            for (final String modifierType : MODIFIER_TYPES) {
                if (edge.getRelation().getShortName().startsWith(modifierType)) {
                    modifiers.add(edge);
                }
            }
        }

        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        final Set<String> simplifiedSentences = new HashSet<>();
        for (final SemanticGraphEdge edge : modifiers) {
            final IndexedWord governor = edge.getGovernor();
            final int governorIndex = governor.index() - 1;
            final IndexedWord dependent = edge.getDependent();
            final int dependentIndex = dependent.index() - 1;

            final String governorTag = governor.backingLabel().tag().toLowerCase();
            if (governorTag.startsWith("vb")) {
                // See if the dependent is enclosed within commas
                int leftCommaBound = -1;
                int rightCommaBound = words.size() - 1;
                // TODO Refactor this and account for commas in dates
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
                if (leftCommaBound <= governorIndex || rightCommaBound <= governorIndex) {
                    System.out.println("The verb modifier is not bounded by commas after the governor");
                    continue;
                }
                // If the appositive or relative clause is at the end of the sentence, make sure we're not deleting the
                // period.
                if (words.get(rightCommaBound).equals(".")) {
                    rightCommaBound--;
                }
                final Range<Integer> dependentRange = Range.closed(leftCommaBound, rightCommaBound);
                partsToRemove.add(dependentRange);
            }
        }
        final List<String> answer = WordListUtil.removeParts(words, partsToRemove);
        System.out.println("With appositives and relative clauses removed: " + answer);
        final String simplifiedSentence = WordListUtil.constructPhraseFromWordList(answer);
        simplifiedSentences.add(simplifiedSentence);
        System.out.println("Simplified Sentences: " + simplifiedSentences);
        return new SimplificationResult(simplifiedSentences);
    }
}
