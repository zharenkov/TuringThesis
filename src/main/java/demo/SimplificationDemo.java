package demo;

import simplification.SentenceSimplifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimplificationDemo {
    private static final String TOPIC_SENTENCES_FILE_NAME = "topic_sentences.txt";
    private static final String DIVIDER = "\n---------------------------------\n";
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

        System.out.printf("Found %d sentences\n", sentences.size());

        System.setOut(DUMMY_STREAM);
        System.setErr(DUMMY_STREAM);
        final List<List<String>> simplifiedSentences = new ArrayList<>();
        int totalSimplifiedSentences = 0;
        for (final String sentence : sentences) {
            final List<String> strings = SentenceSimplifier.simplifySentence(sentence);
            simplifiedSentences.add(strings);
            totalSimplifiedSentences += strings.size();
        }
        System.setOut(OUT);
        System.setErr(ERR);
        System.out.printf("%d total simplified sentences\n", totalSimplifiedSentences);

        for (int i = 0; i < simplifiedSentences.size(); i++) {
            final List<String> listOfSimplifiedSentences = simplifiedSentences.get(i);
            System.out.println(DIVIDER);
            System.out.println("Original Sentence: " + sentences.get(i) + "\n");
            for (final String simplifiedSentece : listOfSimplifiedSentences) {
                System.out.println(simplifiedSentece);
            }
            System.out.println(DIVIDER);
        }
    }
}
