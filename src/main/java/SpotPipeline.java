import data.Text;
import org.apache.commons.io.FileUtils;
import questionGeneration.vo.Output;
import service.QGService;
import service.SimplificationService;
import vo.ParagraphWithSimplifications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpotPipeline {

    private static final String PHRASE_TABLE = "replacements.txt";
    private static final String TOPIC_SENTENCES_FILE_NAME = "iphone.txt";
    private static Map<String, String> phraseTable = new HashMap<>();
    private static Map<String, String> reversedPhraseTable = new HashMap<>();
    private final static String SIMPLE_SENT_TEMPLATE = "simple_sent_%d.txt";
    private final static String SIMPLE_PARA_TEMPLATE = "simple_para_%d.txt";
    private final static String SIMPLE_SENT_L_TEMPLATE = "simple_sent_l_%d.txt";
    private final static String SIMPLE_PARA_L_TEMPLATE = "simple_para_l_%d.txt";


    public static void main(String[] args) throws IOException, InterruptedException {
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


        //run full simplification
        for (int i = 0; i < sourceParagraphs.size(); i++) {
            final int finalI = i;
            executor.execute(() -> {
                SimplificationService simplificationService = new SimplificationService();
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
                List<Output> questions = null;
                try {
                    questions = new QGService(properties, reversedPhraseTable).generateQuestions(
                            simpleSentLPath,
                            simpleParaLPath,
                            simpleSentPath
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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

    }

    private static void readPhraseTable(String phraseTablePath) throws FileNotFoundException {
        ClassLoader classLoader = SpotPipeline.class.getClassLoader();

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
//        ClassLoader classLoader = SpotPipeline.class.getClassLoader();
//        final URL resource = classLoader.getResource(inputFilePath);
//        if (resource == null) {
//            System.err.println("Cannot load topic sentences");
//            throw new RuntimeException("Cannot load topic sentences");
//        }
        final Scanner scanner = new Scanner(new File(inputFilePath));
        final List<Text> paragraphs = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            paragraphs.add(new Text(replaceWords(line)));
        }
        return paragraphs;
    }

    private static String replaceWords(String input) {
        for (Map.Entry<String, String> entry : phraseTable.entrySet()) {
            input = input.replaceAll(entry.getKey(), entry.getValue());
        }
        return input;
    }

    private static void saveFilesForQG(String paragraph, List<String> sentences, long timestamp) throws IOException {
        List<String> paragraphs = new ArrayList<>();
        for (String sentence : sentences) {
            paragraphs.add(paragraph);
        }
        FileUtils.writeLines(new File(String.format(SIMPLE_SENT_TEMPLATE, timestamp)), sentences);
        FileUtils.writeLines(new File(String.format(SIMPLE_PARA_TEMPLATE, timestamp)), paragraphs);
        FileUtils.writeLines(new File(String.format(SIMPLE_SENT_L_TEMPLATE, timestamp)), sentences.stream().map(String::toLowerCase).collect(Collectors.toList()));
        FileUtils.writeLines(new File(String.format(SIMPLE_PARA_L_TEMPLATE, timestamp)), paragraphs.stream().map(String::toLowerCase).collect(Collectors.toList()));

    }
}
