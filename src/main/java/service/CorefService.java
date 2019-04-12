package service;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class CorefService {

    public static void main(String[] args) throws IOException {
        doCoref("");
    }

    public static String doCoref(String text) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Annotation doc = new Annotation(text);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
        props.setProperty("ssplit.boundaryTokenRegex", "\\.|[!?]+");
        props.setProperty("coref.algorithm", "neural");//"statistical" : "neural"
        props.setProperty("coref.neural.greedyness", "0.5");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

//
//        PTBTokenizer ptbTokenizer = new PTBTokenizer(
//                new FileReader(classLoader.getResource("simplifiedParagraphs.txt").getFile())
//                , new WordTokenFactory()
//                , "untokenizable=allKeep,tokenizeNLs=true,ptb3Escaping=true,strictTreebank3=true,unicodeEllipsis=true");
//        List<String> strings = ptbTokenizer.tokenize();
        pipeline.annotate(doc);

        Map<Integer, CorefChain> corefs = doc.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);

        ArrayList<CorefChain> corefsList = new ArrayList(corefs.values());
        List<String> corefedSentences = new ArrayList<>();

        List<String> resolved = new ArrayList<String>();

//        for (CoreMap sentence : sentences) {
//
//            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
//
//            for (CoreLabel token : tokens) {
//
//                Integer corefClustId= token.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
//                System.out.println(token.word() +  " --> corefClusterID = " + corefClustId);
//
//
//                CorefChain chain = corefs.get(corefClustId);
//                System.out.println("matched chain = " + chain);
//
//
//                if(chain==null){
//                    resolved.add(token.word());
//                    System.out.println("Adding the same word "+token.word());
//                }else{
//
//                    int sentINdx = chain.getRepresentativeMention().sentNum -1;
//                    System.out.println("sentINdx :"+sentINdx);
//                    CoreMap corefSentence = sentences.get(sentINdx);
//                    List<CoreLabel> corefSentenceTokens = corefSentence.get(CoreAnnotations.TokensAnnotation.class);
//                    String newwords = "";
//                    CorefChain.CorefMention reprMent = chain.getRepresentativeMention();
//                    System.out.println("reprMent :"+reprMent);
//                    System.out.println("Token index "+token.index());
//                    System.out.println("Start index "+reprMent.startIndex);
//                    System.out.println("End Index "+reprMent.endIndex);
//                    if (token.index() <= reprMent.startIndex || token.index() >= reprMent.endIndex) {
//
//                        for (int i = reprMent.startIndex; i < reprMent.endIndex; i++) {
//                            CoreLabel matchedLabel = corefSentenceTokens.get(i - 1);
//                            resolved.add(matchedLabel.word().replace("'s", ""));
//                            System.out.println("matchedLabel : "+matchedLabel.word());
//                            newwords += matchedLabel.word() + " ";
//
//                        }
//                    }
//
//                    else {
//                        resolved.add(token.word());
//                        System.out.println("token.word() : "+token.word());
//                    }
//
//
//
//                    System.out.println("converting " + token.word() + " to " + newwords);
//                }
//
//
//                System.out.println();
//                System.out.println();
//                System.out.println("-----------------------------------------------------------------");
//
//            }
//
//        }
//
//
//        String resolvedStr ="";
//        System.out.println();
//        for (String str : resolved) {
//            resolvedStr+=str+" ";
//        }
//        System.out.println(resolvedStr);
        //        return resolvedStr;

        for (int i = 0; i < sentences.size(); i++) {
            String sent = sentences.get(i).toString();
            for (CorefChain ch : corefsList) {
                String representative = ch.getMentionsInTextualOrder().get(0).mentionSpan;
                List<CorefChain.CorefMention> mentions = ch.getMentionsInTextualOrder();
                for (CorefChain.CorefMention mention : mentions) {
                    int sentNum = mention.sentNum - 1;
                    if (sentNum == i) {

                        sent = sent.replace(mention.mentionSpan.replaceAll(" 's","'s") + " ", representative+ " ");
                    }
                }
            }
            corefedSentences.add(sent);

        }
        return corefedSentences.stream().collect(Collectors.joining(" "));

//        List<String> resolved = new ArrayList<String>();
//
//        for (CoreMap sentence : sentences) {
//
//            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
//
//            for (CoreLabel token : tokens) {
//
//                Integer corefClustId = token.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
//                System.out.println(token.word() + " --> corefClusterID = " + corefClustId);
//
//
//                CorefChain chain = corefs.get(corefClustId);
//                System.out.println("matched chain = " + chain);
//
//
//                if (chain == null || chain.getMentionsInTextualOrder().size() == 1) {
//                    resolved.add(token.word());
//                } else {
//
//                    int sentINdx = chain.getRepresentativeMention().sentNum - 1;
//                    CoreMap corefSentence = sentences.get(sentINdx);
//                    List<CoreLabel> corefSentenceTokens = corefSentence.get(CoreAnnotations.TokensAnnotation.class);
//
//                    String newwords = "";
//                    CorefChain.CorefMention reprMent = chain.getRepresentativeMention();
//                    System.out.println(reprMent);
//                    if (token.index() < reprMent.startIndex || token.index() > reprMent.endIndex) {
//
//                        for (int i = reprMent.startIndex; i < reprMent.endIndex; i++) {
//                            CoreLabel matchedLabel = corefSentenceTokens.get(i - 1);
//                            resolved.add(matchedLabel.word());
//
//                            newwords += matchedLabel.word() + " ";
//
//                        }
//                    } else {
//                        resolved.add(token.word());
//
//                    }
//
//
//                    System.out.println("converting " + token.word() + " to " + newwords);
//                }
//
//
//                System.out.println();
//                System.out.println();
//                System.out.println("-----------------------------------------------------------------");
//
//            }
//
//        }
//
//        String resolvedStr = "";
//        System.out.println();
//        for (String str : resolved) {
//            resolvedStr += str + " ";
//        }
//        return resolvedStr;
//        Annotation annotation = new Annotation(resolvedStr);
//        pipeline.annotate(annotation);
//
//        return annotation.get(CoreAnnotations.SentencesAnnotation.class).stream().map(CoreMap::toString).collect(Collectors.toList());
    }
}
