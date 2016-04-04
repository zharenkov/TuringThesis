package demo;

import com.google.common.base.Charsets;
import data.Text;
import org.apache.commons.io.FileUtils;
import simplification.SentenceSimplifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class SimplificationDemo {
    private static final String TOPIC_SENTENCES_FILE_NAME = "topic_sentences.txt";
    private static final String OUTPUT_FILE_NAME = "output/demo/simplification/result_%d.txt";
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
        final List<Text> sentences = new ArrayList<>();
        while (scanner.hasNext()) {
            sentences.add(new Text(scanner.nextLine()));
        }

        System.setOut(DUMMY_STREAM);
        //System.setErr(DUMMY_STREAM);
        final Map<Text, Set<Text>> sentenceToSimplifiedSentences = new LinkedHashMap<>();
        for (final Text sentence : sentences) {
            final Set<Text> strings = SentenceSimplifier.simplifySentence(sentence.getString());
            sentenceToSimplifiedSentences.put(sentence, strings);
        }
        System.setOut(OUT);
        //System.setErr(ERR);

        final TopicSentencesSimplification simplification = new TopicSentencesSimplification(
                sentenceToSimplifiedSentences);
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
