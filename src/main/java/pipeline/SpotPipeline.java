package pipeline;

import data.Text;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.pipeline.StanfordCoreNLPServer;
import org.apache.commons.io.FileUtils;
import questionGeneration.vo.GeneratedQuestion;
import service.QGService;
import service.SimplificationService;
import vo.ParagraphWithSimplifications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpotPipeline {

    public static Map<String, String> phraseTable = new HashMap<>();
    public static Map<String, String> reversedPhraseTable = new HashMap<>();
    private final static String SIMPLE_SENT_TEMPLATE = "simple_sent_%d.txt";
    private final static String SIMPLE_SENT_L_TEMPLATE = "simple_sent_l_%d.txt";
    private final static String SIMPLE_PARA_L_TEMPLATE = "simple_para_l_%d.txt";


    public static void main(String[] args) throws IOException, InterruptedException {
       long start = System.currentTimeMillis();
        Properties properties = new Properties();
        properties.load(new FileReader("runner.properties"));
        String phraseTablePath = properties.getProperty("phraseTable");
        String root = properties.getProperty("home");

        //read phrase table and text
        readPhraseTable(phraseTablePath);
        final List<Text> sourceParagraphs = readText(args[0]);
        Map<Integer, ParagraphWithSimplifications> simplified = new HashMap<>();
        // create executor service
        final int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);

        //create stanford core nlp pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, coref");
        props.setProperty("ssplit.boundaryTokenRegex", "\\.|[!?]+");
        props.setProperty("coref.algorithm", "neural");//"statistical" : "neural"
        props.setProperty("coref.neural.greedyness", "0.5");
        props.setProperty("tokenize.options","strictTreebank3=true");
        props.setProperty("threads", String.valueOf(processors));
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        //run full simplification
        for (int i = 0; i < sourceParagraphs.size(); i++) {
            final int finalI = i;
            executor.execute(() -> {
                SimplificationService simplificationService = new SimplificationService(pipeline);
                String sourceParagraph = sourceParagraphs.get(finalI).getString();
                String simplifiedParagraph = simplificationService.simplyfyParagraph(sourceParagraph);
                List<String> simplifiedSentences = simplificationService.splitParagraph(simplifiedParagraph);
                long timestamp = System.currentTimeMillis();
                try {
                    saveFilesForQG(simplifiedParagraph, simplifiedSentences, timestamp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String simpleSentLPath = root + String.format(SIMPLE_SENT_L_TEMPLATE, timestamp);
                String simpleParaLPath = root + String.format(SIMPLE_PARA_L_TEMPLATE, timestamp);
                String simpleSentPath = root + String.format(SIMPLE_SENT_TEMPLATE, timestamp);
                List<GeneratedQuestion> questions = null;
                try {
                    questions = new QGService(properties).generateQuestions(
                            simpleSentLPath,
                            simpleParaLPath,
                            simpleSentPath
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                simplified.put(finalI, new ParagraphWithSimplifications(
                        sourceParagraph,
                        simplifiedParagraph,
                        simplifiedSentences,
                        questions)
                );
                new File(simpleParaLPath).delete();
                new File(simpleSentLPath).delete();
                new File(simpleSentPath).delete();
            });
        }
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.MINUTES);
        FileUtils.writeLines(new File("result.txt"), simplified.values().stream().map(s -> s.toString()).collect(Collectors.toList()));
        System.out.println("Total time: " + (System.currentTimeMillis()-start)/1000);
    }

    private static void readPhraseTable(String phraseTablePath) throws FileNotFoundException {
        Scanner phraseTableScanner = new Scanner(new File(phraseTablePath));
        while (phraseTableScanner.hasNext()) {
            String replacement = phraseTableScanner.nextLine();
            String[] parts = replacement.split("\\|\\|");
            phraseTable.put(parts[0], parts[1]);
            phraseTable.put(parts[0].toLowerCase(), parts[1].toLowerCase());
            reversedPhraseTable.put(parts[1], parts[0]);
            reversedPhraseTable.put(parts[1].toLowerCase(), parts[0].toLowerCase());
        }
    }

    private static List<Text> readText(String inputFilePath) throws FileNotFoundException {
        final Scanner scanner = new Scanner(new File(inputFilePath));
        final List<Text> paragraphs = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            paragraphs.add(new Text(line));
        }
        return paragraphs;
    }

    private static void saveFilesForQG(String paragraph, List<String> sentences, long timestamp) throws IOException {
        List<String> paragraphs = new ArrayList<>();
        for (String sentence : sentences) {
            paragraphs.add(paragraph);
        }
        FileUtils.writeLines(new File(String.format(SIMPLE_SENT_TEMPLATE, timestamp)), sentences);
        FileUtils.writeLines(new File(String.format(SIMPLE_SENT_L_TEMPLATE, timestamp)), sentences.stream().map(String::toLowerCase).collect(Collectors.toList()));
        FileUtils.writeLines(new File(String.format(SIMPLE_PARA_L_TEMPLATE, timestamp)), paragraphs.stream().map(String::toLowerCase).collect(Collectors.toList()));

    }
}
