package question.who_what;

import com.google.common.base.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import generation.*;
import question.*;
import simplenlg.features.*;
import tagging.*;
import tagging.Sentence;

import java.util.*;

import static question.who_what.SubjectIdentifier.findInterrogativeTypeObject;

public class CopulaRule implements Rule {
    @Override
    public Set<String> generateQuestions(Sentence sentence) {
        final Set<String> questions = new HashSet<>();
        System.out.println("Starting Copula scanning\n-----------------------------------");
        System.out.println("Sentence: '" + sentence.getString() + "'");
        processTree(sentence.getPosTree(), sentence, questions);
        System.out.println("-----------------------------------\nEnding copula scanning");
        return questions;
    }

    private void processTree(Tree posTree, Sentence sentence, Set<String> questions) {
        final List<Tree> words = posTree.getLeaves();
        // Search for a noun and see if it has a copula
        for (final Tree word : words) {
            final Tree parent = word.parent(posTree);
            final String label = parent.label().value();
            if (label.startsWith("NN")) {
                final List<TypedDependency> dependencies = sentence.getDependenciesForLeaf(word);
                for (final TypedDependency typedDependency : dependencies) {
                    if (typedDependency.reln().getLongName().toLowerCase().contains("cop")) {
                        System.out.printf("Found noun with copula relation: '%s'\n", word.toString());
                        final String npString = getNp(word, posTree);

                        // Build up the full VP
                        final IndexedWord verb = typedDependency.dep();
                        // The typed dependencies are 1-indexed not zero indexed
                        final Tree verbTree = posTree.getLeaves().get(verb.index() - 1);
                        final String verbString = getVp(verbTree, posTree);
                        System.out.println(verbString);

                        // Find the noun subject to determine if this will be a WHO or WHAT question
                        InterrogativeType type = InterrogativeType.WHAT_OBJECT;
                        for (final TypedDependency dependency : dependencies) {
                            final String relation = dependency.reln().getShortName().toLowerCase();
                            if (relation.contains("nsubj")) {
                                final String subjectString = sentence.getNp(dependency.dep());
                                type = findInterrogativeTypeObject(sentence, subjectString);
                                break;
                            }
                        }

                        final String question = QuestionGenerator.generateNpVpQuestion(npString, verbString, type);
                        System.out.println("Generated question: " + question);
                        questions.add(question);
                    }
                }
            }
        }
    }

    private String getNp(Tree noun, Tree posTree) {
        Tree parent = noun.parent(posTree);
        Tree previous = noun;
        while (parent.label().value().equals("NP") || parent.label().value().equals("PP") ||
                parent.label().value().startsWith("NN")) {
            previous = parent;
            parent = parent.parent(posTree);
        }
        return Joiner.on(' ').join(previous.getLeaves());
    }

    private String getVp(Tree verb, Tree posTree) {
        final StringBuilder verbPhrase = new StringBuilder();
        verbPhrase.insert(0, verb.getLeaves().get(0).value());

        // Skip the current VP
        Tree parent = verb.parent(posTree).parent(posTree).parent(posTree);
        while (parent.label().value().equals("VP") || parent.label().value().equals("S")) {
            if(parent.label().value().equals("VP")) {
                verbPhrase.insert(0, parent.getLeaves().get(0).value() + " ");
            }
            parent = parent.parent(posTree);
        }
        return verbPhrase.toString();
    }
}
