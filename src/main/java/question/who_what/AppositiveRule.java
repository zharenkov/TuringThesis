package question.who_what;

import com.google.common.base.Joiner;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import generation.QuestionGenerator;
import question.Rule;
import simplenlg.features.InterrogativeType;
import tagging.Sentence;

import java.util.List;
import java.util.Set;

import static question.who_what.SubjectIdentifier.findInterrogativeTypeSubject;

/**
 * Appositives must always have commas to separate them from the rest of the sentence but we can use a different way to
 * distinguish between the two noun phrases: the two noun phrases will both be under a single noun phrase. Thus, when we
 * go up from each noun to the highest contiguous NP, we then look at the children of that NP to differentiate.
 * <p>
 * This rule will only generate questions in the present tense due to the difficulty in determining tense with just noun
 * phrases.
 */
public class AppositiveRule extends Rule {
    @Override
    protected String getRuleName() {
        return "appositive";
    }

    @Override
    protected void findQuestions(Tree tree, Sentence sentence, Set<String> questions) {
        final List<Tree> words = tree.getLeaves();
        // Search for a noun and see if it has a copula
        for (final Tree word : words) {
            final Tree parent = word.parent(tree);
            final String label = parent.label().value();
            if (label.startsWith("NN")) {
                final List<TypedDependency> dependencies = sentence.getDependenciesForLeaf(word);
                for (final TypedDependency typedDependency : dependencies) {
                    if (typedDependency.reln().getLongName().toLowerCase().contains("appos")) {
                        final String np1String = getNp(word, tree);
                        final String np2String = getNp(words.get(typedDependency.dep().index() - 1), tree);
                        System.out.printf("Found noun phrases with appositive relation: '%s' '%s'\n", np1String,
                                np2String);

                        final InterrogativeType type1 = findInterrogativeTypeSubject(sentence, np2String);
                        final InterrogativeType type2 = findInterrogativeTypeSubject(sentence, np1String);
                        final String question1 = QuestionGenerator.generateAppositiveQuestion(np1String, type1);
                        final String question2 = QuestionGenerator.generateAppositiveQuestion(np2String, type2);
                        System.out.println("Generated question 1: " + question1);
                        System.out.println("Generated question 2: " + question2);
                        questions.add(question1);
                        questions.add(question2);
                    }
                }
            }
        }
    }

    private String getNp(Tree noun, Tree posTree) {
        Tree parent = noun.parent(posTree);
        Tree previous = noun;
        Tree previous2 = noun;
        while (parent.label().value().equals("NP") || parent.label().value().equals("PP") ||
                parent.label().value().startsWith("NN")) {
            previous2 = previous;
            previous = parent;
            parent = parent.parent(posTree);
        }
        return Joiner.on(' ').join(previous2.getLeaves());
    }
}
