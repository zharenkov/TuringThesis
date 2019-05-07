package service;

import data.Text;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import pipeline.SpotPipeline;
import simplification.SentenceSimplifier;
import util.ReplaceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimplificationService {

    private StanfordCoreNLP pipeline;

    public SimplificationService(StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }

    public String simplyfyParagraph(String paragraph) {
        //remove braces
        String deparanthesisParagraph = "";
        Annotation para = new Annotation(paragraph);
        pipeline.annotate(para);
        StringBuilder sb = new StringBuilder();
        for (CoreMap cm : para.get(CoreAnnotations.SentencesAnnotation.class)) {
            if (cm.get(CoreAnnotations.TokensAnnotation.class).stream().filter(s->!StringUtils.isPunct(s.word())).count()<4) continue;
            try {

                final Set<Text> strings = SentenceSimplifier.simplifyParanteticalSentence(cm.toString());
                sb.append(strings.stream().map(Text::getString).collect(Collectors.joining(" "))).append(" ");
            } catch (Exception e) {
                continue;
            }

        }
        deparanthesisParagraph = sb.toString();

        // coref
        String corefedParagraph = ReplaceUtils.replaceWords(
                CorefService.doCoref(deparanthesisParagraph, pipeline),
                SpotPipeline.phraseTable
                );

        // simplification
        Annotation annotatedCorefedParagraph = new Annotation(corefedParagraph);
        pipeline.annotate(annotatedCorefedParagraph);
        sb = new StringBuilder();
        for (CoreMap cm : annotatedCorefedParagraph.get(CoreAnnotations.SentencesAnnotation.class)) {
            if (".".equals(cm.toString()) || cm.get(CoreAnnotations.TokensAnnotation.class).stream().filter(s->!StringUtils.isPunct(s.word())).count()<4) continue;
            try {
                final Set<Text> strings = SentenceSimplifier.simplifySentence(cm.toString());
                sb.append(strings.stream().map(Text::getString).collect(Collectors.joining(" "))).append(" ");
            } catch (Exception e) {
                continue;
            }
        }
        String simplifiedParagraph = sb.toString();

       return simplifiedParagraph;
    }

    public List<String> splitParagraph(String paragraph) {
        List<String> sents = new ArrayList<>();
        Annotation annotation = new Annotation(paragraph);
        pipeline.annotate(annotation);
        for (CoreMap cm : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            if (cm.get(CoreAnnotations.TokensAnnotation.class).stream().filter(s->!StringUtils.isPunct(s.word())).count()<4) continue;
            sents.add((cm.toString()));
        }
        return sents;
   }
}