package question.who_what;

import com.google.common.base.*;
import edu.stanford.nlp.trees.*;
import question.*;
import simplenlg.features.*;
import tagging.*;

import java.util.*;

import static question.who_what.SubjectIdentifier.*;

/**
 * Appositives must always have commas to separate them from the rest of the sentence but we can use a different way to
 * distinguish between the two noun phrases: the two noun phrases will both be under a single noun phrase. Thus, when we
 * go up from each noun to the highest contiguous NP, we then look at the children of that NP to differentiate.
 */
public class AppositiveRule implements Rule {
    @Override
    public Set<String> generateQuestions(Sentence sentence) {
        final Set<String> questions = new HashSet<>();
        System.out.println("Starting Appositive scanning\n-----------------------------------");
        System.out.println("Sentence: '" + sentence.getString() + "'");
        processTree(sentence.getPosTree(), sentence, questions);
        System.out.println("-----------------------------------\nEnding Appositive scanning");
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
                    if (typedDependency.reln().getLongName().toLowerCase().contains("appos")) {
                        System.out.printf("Found noun with appositive relation: '%s'\n", word.toString());
                        final String npString = getNp(word, posTree);

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
}
