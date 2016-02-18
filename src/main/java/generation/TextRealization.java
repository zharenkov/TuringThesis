package generation;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.simple.Sentence;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.Set;

public class TextRealization {
    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static final Realiser realiser = new Realiser(lexicon);

    private static final Set<String> INTRANSITIVE_VERBS = ImmutableSet.of("die");
    private static final Joiner SPACES = Joiner.on(' ');
    private static final char QUESTION_MARK = '?';
    private static final char PERIOD = '.';

    /**
     * Realizes a sentence by joining the given parts with spaces.
     * <p>
     * A period will be added at the end of the sentence if one does not already exist.
     * <br>
     * The first letter of the sentence will be capitalized if it is not already.
     *
     * @param parts the given parts
     * @return the realized sentence.
     */
    public static String realizeSentence(String... parts) {
        return realizeWithPunctuation(PERIOD, parts);
    }

    /**
     * Realizes a sentence by joining the given parts with spaces.
     * <p>
     * A question mark will be added at the end of the sentence if one does not already exist.
     * <br>
     * The first letter of the sentence will be capitalized if it is not already.
     *
     * @param parts the given parts
     * @return the realized question.
     */
    public static String realizeQuestion(String... parts) {
        return realizeWithPunctuation(QUESTION_MARK, parts);
    }

    private static String realizeWithPunctuation(char punctuation, String... parts) {
        final StringBuilder builder = new StringBuilder(SPACES.join(parts));
        if (builder.charAt(builder.length() - 1) != punctuation) {
            builder.append(punctuation);
        }
        return Character.toUpperCase(builder.charAt(0)) + builder.substring(1);
    }

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
        final String lemma = new Sentence(verb).lemma(0);
        System.out.printf("Realizing verb '%s' (lemma '%s')\n", verb, lemma);

        // Intransitive verbs cannot take on passive voice
        if (passiveVoice && !INTRANSITIVE_VERBS.contains(lemma)) {
            vpPhraseSpec.setFeature(Feature.PASSIVE, true);
        }
        vpPhraseSpec.setFeature(Feature.TENSE, tense);

        return realiser.realise(vpPhraseSpec).toString();
    }
}
