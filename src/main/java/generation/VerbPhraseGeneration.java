package generation;

import com.google.common.collect.ImmutableSet;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.Set;

public class VerbPhraseGeneration {
    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static final Realiser realiser = new Realiser(lexicon);

    private static final Set<String> INTRANSITIVE_VERBS = ImmutableSet.of("die");

    /**
     * Realizes the given verb phrase with the given features.
     *
     * @param vp           the given verb phrase
     * @param passiveVoice whether the realization should be in passive voice
     * @param tense        the tense of the realization
     * @return the realized verb phrase
     */
    public static String realizeVerbPhraseWithFeatures(String vp, boolean passiveVoice, Tense tense) {
        final VPPhraseSpec vpPhraseSpec = nlgFactory.createVerbPhrase(vp);
        final String verb = vp.split(" ")[0];

        // Intransitive verbs cannot take on passive voice
        if (passiveVoice && !INTRANSITIVE_VERBS.contains(verb)) {
            vpPhraseSpec.setFeature(Feature.PASSIVE, true);
        }
        vpPhraseSpec.setFeature(Feature.TENSE, tense);

        return realiser.realise(vpPhraseSpec).toString();
    }
}
