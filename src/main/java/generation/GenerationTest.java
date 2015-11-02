package generation;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.Arrays;

public class GenerationTest {
    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static final Realiser realiser = new Realiser(lexicon);

    public static void main(String[] args) {
        printQuestion("George Washington", "was the first president of the United States");
    }

    private static String turnSentenceIntoQuestion(String sentence) {
        sentence = sentence.trim().replaceAll("\\.", "");
        final Character firstCharacter = sentence.charAt(0);
        return Character.toUpperCase(firstCharacter) + sentence.substring(1) + "?";
    }

    private static void printQuestion(String np, String vp) {
        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase(vp);
        final NPPhraseSpec npPhraseSpec = nlgFactory.createNounPhrase(np);
        // TODO Turn the NP into a WH based on classification
        // TODO Set the tense of the verb in the question to be the same as the tense in the statement
        final DocumentElement sentence = nlgFactory.createSentence(Arrays.asList(npPhraseSpec, vpPhraseSpec));
        System.out.println(turnSentenceIntoQuestion(realiser.realiseSentence(sentence)));
    }
}
