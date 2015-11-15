package lemmatizing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import static edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import static edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;

public class Lemmatizer {
    private static final StanfordCoreNLP pipeline;

    static {
        // Create StanfordCoreNLP object properties, with POS tagging (required for lemmatization), and lemmatization
        final Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public static List<String> lemmatizeSentence(String string) {
        final List<String> lemmas = new ArrayList<>();

        final Annotation document = new Annotation(string);
        pipeline.annotate(document);

        final List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (final CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (final CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }

        return lemmas;
    }
}
