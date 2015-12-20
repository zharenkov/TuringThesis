package generation;

import simplenlg.features.*;
import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.phrasespec.*;
import simplenlg.realiser.english.*;
import tagging.*;

public class QuestionGenerator {
    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static final Realiser realiser = new Realiser(lexicon);

    public static String generateLocationQuestion(String vp, String subject) {
        final Tense tense = StanfordParser.calculateTense(vp);
        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase(vp);
        vpPhraseSpec.setFeature(Feature.TENSE, tense);
        final NPPhraseSpec npPhraseSpec = nlgFactory.createNounPhrase(subject);

        final SPhraseSpec sPhraseSpec = nlgFactory.createClause(npPhraseSpec, vpPhraseSpec);
        sPhraseSpec.setFeature(Feature.TENSE, tense);
        sPhraseSpec.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE);

        return realiser.realiseSentence(sPhraseSpec);
    }

    public static String generateNpVpQuestion(String np, String vp, InterrogativeType type) {
        final Tense tense = StanfordParser.calculateTense(vp);
        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase(vp);
        vpPhraseSpec.setFeature(Feature.TENSE, tense);
        final NPPhraseSpec npPhraseSpec = nlgFactory.createNounPhrase(np);

        final SPhraseSpec sPhraseSpec = nlgFactory.createClause(npPhraseSpec, vpPhraseSpec);
        sPhraseSpec.setFeature(Feature.INTERROGATIVE_TYPE, type);

        return realiser.realiseSentence(sPhraseSpec);
    }

    public static String generateCopulaQuestion(String np, String vp, InterrogativeType type) {
        final Tense tense = StanfordParser.calculateTense(vp);
        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase(vp);
        final NPPhraseSpec npPhraseSpec = nlgFactory.createNounPhrase(np);

        final SPhraseSpec sPhraseSpec = nlgFactory.createClause(npPhraseSpec, vpPhraseSpec);
        sPhraseSpec.setFeature(Feature.INTERROGATIVE_TYPE, type);
        sPhraseSpec.setFeature(Feature.TENSE, tense);

        return realiser.realiseSentence(sPhraseSpec);
    }
}
