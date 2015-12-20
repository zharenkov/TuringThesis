package question.who_what;

import com.google.common.base.*;
import edu.stanford.nlp.trees.*;
import generation.*;
import question.*;
import simplenlg.features.*;
import tagging.*;

import java.util.*;

public class CopulaRule implements Rule {
    @Override
    public Set<String> generateQuestions(Sentence sentence) {
        final Set<String> questions = new HashSet<>();
        System.out.println("Starting Copula scanning\n-----------------------------------");
        System.out.println("Sentence: '" + sentence.getString() + "'");
        processTree(sentence.getPosTree(), sentence, questions);
        System.out.println("-----------------------------------\nEnding location scanning");
        return questions;
    }

    private void processTree(Tree posTree, Sentence sentence, Set<String> questions) {
        final List<Tree> words = posTree.getLeaves();
        // Search for a noun and see if it has a copula
        for (int i = 0; i < words.size() - 1; i++) {
            final Tree word = words.get(i);
            final Tree parent = word.parent(posTree);
            final String label = parent.label().value();
            if (label.startsWith("NN")) {
                final List<TypedDependency> dependencies = sentence.getDependenciesForLeaf(word);
                for (final TypedDependency typedDependency : dependencies) {
                    if (typedDependency.reln().getLongName().toLowerCase().contains("cop")) {
                        System.out.printf("Found noun with copula relation: '%s'\n", word.toString());
                        final String npString = getNp(word, posTree);
                        final String verbString = typedDependency.dep().originalText();
                        final InterrogativeType type = SubjectIdentifier.findInterrogativeTypeObject(sentence, npString);

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
}
