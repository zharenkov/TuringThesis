package question;

import edu.stanford.nlp.simple.Sentence;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Rule {
    public Set<String> generateQuestions(String sentence) {
        final Set<String> questions = new LinkedHashSet<>();
        System.out.println("Starting " + getRuleName() + " scanning\n-----------------------------------");
        System.out.println("Examining: '" + sentence + "'");
        final Sentence parsedSentence = new Sentence(sentence);
        findQuestions(parsedSentence, questions);
        System.out.println("\nGenerated Questions:\n" + questions);
        System.out.println("-----------------------------------\nEnding " + getRuleName() + " scanning");
        return questions;
    }

    protected abstract void findQuestions(Sentence sentence, Set<String> questions);

    protected abstract String getRuleName();
}
