package service;

import data.Text;
import service.CorefService;
import demo.SimplificationDemo;
import demo.TopicSentencesSimplificationAndQuestions;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.io.FileUtils;
import simplification.SentenceSimplifier;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SimplificationService {

    private StanfordCoreNLP pipeline;

    private static final PrintStream DUMMY_STREAM = new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    });

    public SimplificationService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
        props.setProperty("tokenize.options","strictTreebank3=true");
        pipeline = new StanfordCoreNLP(props);
    }

    public String simplyfyParagraph(String paragraph) {
        final ClassLoader classLoader = SimplificationDemo.class.getClassLoader();
        final TopicSentencesSimplificationAndQuestions result;
        System.setOut(DUMMY_STREAM);
        //System.setErr(DUMMY_STREAM);
        final int processors = Runtime.getRuntime().availableProcessors();

        //remove braces
        String deparanthesisParagraph = "";
        Annotation para = new Annotation(paragraph);
        pipeline.annotate(para);
        StringBuilder sb = new StringBuilder();
        for (CoreMap cm : para.get(CoreAnnotations.SentencesAnnotation.class)) {
            final Set<Text> strings = SentenceSimplifier.simplifyParanteticalSentence(cm.toString());
            sb.append(strings.stream().map(Text::getString).collect(Collectors.joining(" "))).append(" ");
        }
        deparanthesisParagraph = sb.toString();

        // coref
        String corefedParagraph = CorefService.doCoref(deparanthesisParagraph);
        // simplification
        Annotation annotatedCorefedParagraph = new Annotation(corefedParagraph);
        pipeline.annotate(para);
        sb = new StringBuilder();
        for (CoreMap cm : para.get(CoreAnnotations.SentencesAnnotation.class)) {
            if (".".equals(cm.toString())) continue;
            final Set<Text> strings = SentenceSimplifier.simplifySentence(cm.toString());
            sb.append(strings.stream().map(Text::getString).collect(Collectors.joining(" "))).append(" ");
        }
        String simplifiedParagraph = sb.toString();

       return simplifiedParagraph;
    }

    public List<String> splitParagraph(String paragraph) {
        List<String> sents = new ArrayList<>();
        Annotation annotation = new Annotation(paragraph);
        pipeline.annotate(annotation);
        for (CoreMap cm : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            sents.add((cm.toString()));
        }
        return sents;
   }
}