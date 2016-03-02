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
import generation.TextRealization;
import simplenlg.features.Tense;
import util.TreeUtil;
import util.WordListUtil;

import java.util.*;

import static edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations.CLAUSAL_MODIFIER;
import static edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations.RELATIVE_CLAUSE_MODIFIER;
import static util.TenseUtil.calculateTense;

public class RelativeClauseExtractor implements Extractor {
    private static RelativeClauseExtractor extractor;

    private RelativeClauseExtractor() {
    }

    public static RelativeClauseExtractor getExtractor() {
        if (extractor == null) {
            extractor = new RelativeClauseExtractor();
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
        appositivesAndRelativeClauses.addAll(dependencies.findAllRelns(RELATIVE_CLAUSE_MODIFIER));
        appositivesAndRelativeClauses.addAll(dependencies.findAllRelns(CLAUSAL_MODIFIER));
        System.out.println("Relations: " + appositivesAndRelativeClauses);

        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        final Set<String> simplifiedSentences = new HashSet<>();
        for (final SemanticGraphEdge edge : appositivesAndRelativeClauses) {
            // IndexedWord index is 1-based not 0-based
            final IndexedWord governor = edge.getGovernor();
            final IndexedWord dependent = edge.getDependent();
            final Range<Integer> boundedPart = WordListUtil.findBoundedPart(governor, dependent, parsed);
            if (boundedPart != null) {
                partsToRemove.add(boundedPart);
                simplifiedSentences.addAll(generateSimplifiedSentences(edge, parsed, boundedPart));
            }
        }

        final List<String> answer = WordListUtil.removeParts(words, partsToRemove);
        System.out.println("With relative clauses removed: " + answer);
        final String simplifiedSentence = WordListUtil.constructPhraseFromWordList(answer);
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
        final String dependentString = WordListUtil.constructPhraseFromWordList(dependentWordListNoPunctuation);

        final IndexedWord governor = edge.getGovernor();
        final Tree root = sentence.parse();
        final Tree governorNp = TreeUtil.getNpFromWord(root, governor);
        final String governorNpString = TreeUtil.constructPhraseFromTree(governorNp);

        final Tense tense = calculateTense(sentence);
        final String be;
        if (tense == Tense.PAST) {
            be = "was";
        } else {
            be = "is";
        }

        final String dependent;
        if (edge.getRelation().equals(RELATIVE_CLAUSE_MODIFIER)) {
            final String[] split = dependentString.split(" ");
            // Remove the relative pronoun that is at the start of the relative clause
            dependent = Joiner.on(' ').join(Arrays.copyOfRange(split, 1, split.length));
        } else {
            dependent = be + " " + dependentString;
        }
        return ImmutableSet.of(TextRealization.realizeSentence(governorNpString, dependent));
    }
}
