package demo;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import simplification.SentenceSimplifier;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FullPipelineDemo {
    private static final String TOPIC_SENTENCES_FILE_NAME = "topic_sentences.txt";
    private static final String OUTPUT_FILE_NAME = "output/demo/pipeline/result_%d.txt";
    private static final PrintStream OUT = System.out;
    private static final PrintStream ERR = System.err;
    private static final PrintStream DUMMY_STREAM = new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    });

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        final ClassLoader classLoader = SimplificationDemo.class.getClassLoader();
        final URL resource = classLoader.getResource(TOPIC_SENTENCES_FILE_NAME);
        if (resource == null) {
            System.err.println("Cannot load topic sentences");
            return;
        }
        final Scanner scanner = new Scanner(new File(resource.getFile()));
        final List<String> sentences = new ArrayList<>();
        while (scanner.hasNext()) {
            sentences.add(scanner.nextLine());
        }

        System.setOut(DUMMY_STREAM);
        //System.setErr(DUMMY_STREAM);
        final Map<String, Set<String>> sentenceToSimplifiedSentences = new LinkedHashMap<>();
        final int processors = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(processors);
        for (final String sentence : sentences) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Set<String> strings = SentenceSimplifier.simplifySentence(sentence);
                    sentenceToSimplifiedSentences.put(sentence, strings);
                }
            });
        }
        executor.shutdown();
        final long startTime = System.currentTimeMillis();
        System.err.println("Waiting for all simplification tasks to finish");
        executor.awaitTermination(60, TimeUnit.MINUTES);
        System.err.println("All simplification tasks finished");
        final long endTime = System.currentTimeMillis();
        final long secondsToFinish = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.MILLISECONDS);
        System.err.println("Time to finish simplification: " + secondsToFinish + " seconds");

        final TopicSentencesSimplificationAndQuestions simplification = new TopicSentencesSimplificationAndQuestions(
                sentenceToSimplifiedSentences, sentences);
        System.setOut(OUT);
        //System.setErr(ERR);
        System.out.println(simplification);

        if (args.length == 1 && args[0].equals("out")) {
            int n = 0;
            File file = new File(String.format(OUTPUT_FILE_NAME, n));
            while (file.exists()) {
                n++;
                file = new File(String.format(OUTPUT_FILE_NAME, n));
            }
            try {
                FileUtils.writeStringToFile(file, simplification.toString(), Charsets.UTF_8.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
