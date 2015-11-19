package generation;

import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class GenerationTest {
    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static final Realiser realiser = new Realiser(lexicon);

    public static void main(String[] args) {
        //printQuestion("George Washington", "was the first president of the United States");

        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase("born");
        vpPhraseSpec.setFeature(Feature.TENSE, Tense.PAST);
        final NPPhraseSpec npPhraseSpec = nlgFactory.createNounPhrase("George Washington");
        final SPhraseSpec sPhraseSpec = nlgFactory.createClause(npPhraseSpec, vpPhraseSpec);
        sPhraseSpec.setFeature(Feature.TENSE, Tense.PAST);
        sPhraseSpec.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE);
        System.out.println(realiser.realiseSentence(sPhraseSpec));
    }

    private static String turnSentenceIntoQuestion(String sentence) {
        sentence = sentence.trim().replaceAll("\\.", "");
        final Character firstCharacter = sentence.charAt(0);
        return Character.toUpperCase(firstCharacter) + sentence.substring(1) + "?";
    }

    private static void printQuestion(String np, String vp) {
        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase(vp);
        final NPPhraseSpec npPhraseSpec = nlgFactory.createNounPhrase(np);
        // TODO Set the tense of the verb in the question to be the same as the tense in the statement
        final SPhraseSpec sPhraseSpec = nlgFactory.createClause(npPhraseSpec, vpPhraseSpec);

        sPhraseSpec.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
        System.out.println(realiser.realiseSentence(sPhraseSpec));
    }
}
