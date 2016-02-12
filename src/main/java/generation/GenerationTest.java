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
        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase("bear in Virginia");
        System.out.println(vpPhraseSpec.getVerb());
        System.out.println(vpPhraseSpec.getVerb().getAllFeatures());
        vpPhraseSpec.setFeature(Feature.PASSIVE, true);
        vpPhraseSpec.setFeature(Feature.TENSE, Tense.PAST);
        System.out.println(realiser.realise(vpPhraseSpec));

        final VPPhraseSpec vpPhraseSpec2 = nlgFactory.createVerbPhrase("die in Virginia");
        System.out.println(vpPhraseSpec2.getVerb());
        System.out.println(vpPhraseSpec2.getVerb().getAllFeatures());
        vpPhraseSpec2.setFeature(Feature.PASSIVE, false);
        vpPhraseSpec2.setFeature(Feature.TENSE, Tense.PAST);
        System.out.println(realiser.realise(vpPhraseSpec2));
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
