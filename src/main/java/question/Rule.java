package question;

import edu.stanford.nlp.trees.Tree;
import tagging.Sentence;

import java.util.HashSet;
import java.util.Set;

public abstract class Rule {
    public Set<String> generateQuestions(Sentence sentence) {
        final Set<String> questions = new HashSet<>();
        System.out.println("Starting " + getRuleName() + " scanning\n-----------------------------------");
        System.out.println("Sentence: '" + sentence.getString() + "'");
        System.out.println("NERs: " + sentence.getNamedEntities());
        findQuestions(sentence.getPosTree(), sentence, questions);
        System.out.println("-----------------------------------\nEnding " + getRuleName() + " scanning");
        return questions;
    }

    protected abstract void findQuestions(Tree tree, Sentence sentence, Set<String> questions);

    protected abstract String getRuleName();
}
