package simplification;

import com.google.common.base.Joiner;
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
import util.TenseUtil;
import util.TreeUtil;
import util.WordListUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations.ADV_CLAUSE_MODIFIER;
import static edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT;
import static generation.TextRealization.realizeSentence;
import static generation.TextRealization.realizeVerbPhraseWithFeatures;

public class ParticipialModifiersExtractor implements Extractor {
    private static ParticipialModifiersExtractor extractor;

    private ParticipialModifiersExtractor() {
    }

    public static ParticipialModifiersExtractor getExtractor() {
        if (extractor == null) {
            extractor = new ParticipialModifiersExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
        final SimplificationResult simplificationResult = getExtractor().extract(Joiner.on(' ').join(args));
        System.out.println(simplificationResult.getSimplifiedSentences());
    }

    @Override
    public SimplificationResult extract(String sentence) {
        System.out.println("Original sentence: " + sentence);
        final Sentence parsed = new Sentence(sentence);
        final Tree root = parsed.parse();
        final SemanticGraph dependencyGraph = parsed.dependencyGraph();
        final List<String> words = parsed.words();
        final List<String> posTags = parsed.posTags();

        final List<SemanticGraphEdge> relations = dependencyGraph.findAllRelns(ADV_CLAUSE_MODIFIER);
        System.out.println("Relations: " + relations);

        final Set<String> simplifiedSentences = new HashSet<>();
        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        for (int i = 0; i < posTags.size(); i++) {
            final String posTag = posTags.get(i);
            if (posTag.equalsIgnoreCase("vbg") || posTag.equalsIgnoreCase("vbn")) {
                // Participial phrases should not come after a verb
                if (i > 0 && !posTags.get(i - 1).toLowerCase().startsWith("vb")) {
                    final Tree word = root.getLeaves().get(i);
                    final Tree participialPhrase = TreeUtil.getVpFromWord(root, word);
                    if (participialPhrase == null) {
                        System.out.println("Participial phrase not contained in VP");
                        continue;
                    }

                    // Calculate the bounds of the participial phrase and account for commas
                    final Range<Integer> rangeOfParticipialPhrase = TreeUtil.getRangeOfTree(root, participialPhrase);
                    int lowerBound = rangeOfParticipialPhrase.lowerEndpoint();
                    if (i > 0 && words.get(i - 1).equals(",")) {
                        lowerBound--;
                    }
                    int upperBound = rangeOfParticipialPhrase.upperEndpoint();
                    if (i < posTags.size() - 1 && words.get(i + 1).equals(",")) {
                        upperBound++;
                    }
                    rangeSet.add(Range.closed(lowerBound, upperBound));

                    for (final SemanticGraphEdge edge : relations) {
                        final IndexedWord dependent = edge.getDependent();
                        final int dependentIndex = dependent.index() - 1;
                        if (rangeOfParticipialPhrase.contains(dependentIndex)) {
                            final List<SemanticGraphEdge> governorRelations = dependencyGraph.getOutEdgesSorted(
                                    edge.getGovernor());
                            for (final SemanticGraphEdge governorEdge : governorRelations) {
                                if (governorEdge.getRelation().equals(NOMINAL_SUBJECT)) {
                                    final IndexedWord subject = governorEdge.getDependent();
                                    final Tree subjectNp = TreeUtil.getNpFromWord(root, subject);
                                    final String subjectNpString = TreeUtil.constructPhraseFromTree(subjectNp);

                                    final String vp = TreeUtil.constructPhraseFromTree(participialPhrase);
                                    final Tense tense = TenseUtil.calculateTense(parsed);
                                    final String participialPhraseModified = realizeVerbPhraseWithFeatures(vp, false,
                                            tense);
                                    final String simplifiedSentence = realizeSentence(subjectNpString,
                                            participialPhraseModified);
                                    simplifiedSentences.add(simplifiedSentence);
                                }
                            }
                        }
                    }
                }
            }
        }
        final String phrase = WordListUtil.constructPhraseFromWordList(WordListUtil.removeParts(words, rangeSet));
        simplifiedSentences.add(TextRealization.realizeSentence(phrase));
        return new SimplificationResult(simplifiedSentences);
    }
}
