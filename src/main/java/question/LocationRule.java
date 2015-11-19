package question;

import com.google.common.base.Joiner;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import tagging.NamedEntity;
import tagging.Sentence;

import java.util.ArrayList;
import java.util.List;

public class LocationRule implements Rule {

    @Override
    public List<String> generateQuestions(Sentence sentence) {
        final List<String> questions = new ArrayList<>();
        System.out.println("Starting location scanning");
        processTree(sentence.getPosTree(), sentence);
        System.out.println("Ending location scanning");
        return questions;
    }

    private void processTree(Tree tree, Sentence sentence) {
        if (tree.label().value().equals("PP")) {
            System.out.println("Found a PP");
            validatePP(tree, sentence);
        }
        for (final Tree child : tree.getChildrenAsList()) {
            processTree(child, sentence);
        }
    }

    private void validatePP(Tree pp, Sentence sentence) {
        // Check the preposition of the PP
        if (pp.firstChild().firstChild().value().equals("in")) {
            System.out.println("PP starts with 'in'");
            final String phrase = Joiner.on(' ').join(pp.getChild(1).getLeaves());
            // Check the NP part of the PP to see if it is a location
            if (sentence.getNamedEntities().get(phrase) == NamedEntity.LOCATION) {
                System.out.println("NP is a location");
                // Check that the PP is contained within a VP
                final Tree ppParent = pp.parent(sentence.getPosTree());
                if (ppParent.label().value().equalsIgnoreCase("vp")) {
                    System.out.println("PP contained within VP");
                    pullVerb(ppParent, sentence);
                }
            }
        }
    }

    private void pullVerb(Tree vp, Sentence sentence) {
        final String verb = vp.firstChild().firstChild().value();

        final Tree verbTree = vp.getLeaves().get(0);
        final List<TypedDependency> verbDependencies = sentence.getDependenciesForLeaf(verbTree);
        String subject = "";
        for(final TypedDependency typedDependency : verbDependencies) {
            if(typedDependency.reln().getLongName().toLowerCase().contains("subj")) {
                subject = sentence.getNp(typedDependency.dep());
            }
        }
        //final String subject = sentence.getDependencies();
        System.out.printf("Where %s %s?\n", subject, verb);
    }
}
