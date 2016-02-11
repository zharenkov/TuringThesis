package demo;

import simplification.SentenceSimplifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

public class SimplificationDemo {
    private static final String TOPIC_SENTENCES_FILE_NAME = "topic_sentences.txt";
    private static final PrintStream OUT = System.out;
    private static final PrintStream ERR = System.err;
    private static final PrintStream DUMMY_STREAM = new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    });

    public static void main(String[] args) throws FileNotFoundException {
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
        System.setErr(DUMMY_STREAM);
        final Map<String, List<String>> sentenceToSimplifiedSentences = new LinkedHashMap<>();
        for (final String sentence : sentences) {
            final List<String> strings = SentenceSimplifier.simplifySentence(sentence);
            sentenceToSimplifiedSentences.put(sentence, strings);
        }
        System.setOut(OUT);
        System.setErr(ERR);

        final TopicSentencesSimplification simplification = new TopicSentencesSimplification(
                sentenceToSimplifiedSentences);
        System.out.println(simplification);
    }
}
