package generation;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class Test {
    public static void main(String[] args) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);

        final SPhraseSpec clause = nlgFactory.createClause();
        clause.setSubject("who");
        clause.setObject("the first president of the United States");
        clause.setVerb("is");
        clause.setFeature(Feature.TENSE, Tense.PAST);

        final NLGElement nlgElement = realiser.realise(clause);
        System.out.println(turnSentenceIntoQuestion(nlgElement.toString()));
    }

    private static String turnSentenceIntoQuestion(String sentence) {
        sentence = sentence.trim();
        final Character firstCharacter = sentence.charAt(0);
        return Character.toUpperCase(firstCharacter) + sentence.substring(1) + "?";
    }
}
