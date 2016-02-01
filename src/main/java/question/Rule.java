package question;

import edu.stanford.nlp.trees.Tree;
import tagging.ParsedSentence;

import java.util.HashSet;
import java.util.Set;

public abstract class Rule {
    public Set<String> generateQuestions(ParsedSentence sentence) {
        final Set<String> questions = new HashSet<>();
        System.out.println("Starting " + getRuleName() + " scanning\n-----------------------------------");
        System.out.println("ParsedSentence: '" + sentence.getString() + "'");
        System.out.println("NERs: " + sentence.getNamedEntities());
        findQuestions(sentence.getPosTree(), sentence, questions);
        System.out.println("-----------------------------------\nEnding " + getRuleName() + " scanning");
        return questions;
    }

    protected abstract void findQuestions(Tree tree, ParsedSentence sentence, Set<String> questions);

    protected abstract String getRuleName();
}
