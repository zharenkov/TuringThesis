package question.where;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import generation.QuestionGenerator;
import question.Rule;
import tagging.NamedEntity;
import tagging.ParsedSentence;

import java.util.List;
import java.util.Set;

public class LocationRule extends Rule {
    private static final Set<String> locationalPrepositions = ImmutableSet.of("above", "across", "against", "along",
            "among", "around", "at", "behind", "below", "beneath", "beside", "besides", "between", "beyond", "by",
            "down", "from", "in", "inside", "into", "near", "onto", "through", "to", "toward", "under", "underneath",
            "up", "within");

    @Override
    protected String getRuleName() {
        return "location";
    }

    @Override
    protected void findQuestions(Tree tree, ParsedSentence sentence, Set<String> questions) {
        if (tree.label().value().equals("PP")) {
            System.out.println("Found a PP");
            validatePP(tree, sentence, questions);
            System.out.println();
        }
        for (final Tree child : tree.getChildrenAsList()) {
            findQuestions(child, sentence, questions);
        }
    }

    private void validatePP(Tree pp, ParsedSentence sentence, Set<String> questions) {
        // Check the preposition of the PP
        if (locationalPrepositions.contains(pp.firstChild().firstChild().value())) {
            System.out.println("PP starts with locational preposition");
            final String phrase = Joiner.on(' ').join(pp.getChild(1).getLeaves());
            // Check the NP part of the PP to see if it is a location
            if (sentence.getNamedEntities().get(phrase) == NamedEntity.LOCATION) {
                System.out.println("NP is a location");
                // Check that the PP is contained within a VP
                final Tree ppParent = pp.parent(sentence.getPosTree());
                if (ppParent.label().value().equalsIgnoreCase("vp")) {
                    System.out.println("PP contained within VP");
                    constructQuestion(ppParent, sentence, questions);
                }
            }
        }
    }

    private void constructQuestion(Tree vp, ParsedSentence sentence, Set<String> questions) {
        String verb = vp.firstChild().firstChild().value();

        final Tree verbTree = vp.getLeaves().get(0);
        final List<TypedDependency> verbDependencies = sentence.getDependenciesForLeaf(verbTree);
        String subject = "";
        for (final TypedDependency typedDependency : verbDependencies) {
            if (typedDependency.reln().getLongName().toLowerCase().contains("subj")) {
                subject = sentence.getNp(typedDependency.dep());
            }
            if (typedDependency.reln().getLongName().toLowerCase().contains("aux")) {
                verb = typedDependency.dep().originalText() + " " + verb;
            }
        }
        final String question = QuestionGenerator.generateLocationQuestion(verb, subject);
        System.out.println("Question generated: " + question);
        questions.add(question);
    }
}
