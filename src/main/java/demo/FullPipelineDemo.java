package demo;

import com.google.common.base.Charsets;
import data.Text;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import simplification.SentenceSimplifier;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static util.OptionUtil.createOptionalOptionNoArgument;

public class FullPipelineDemo {
    private static final String TOPIC_SENTENCES_FILE_NAME = "topic_sentences.txt";
    private static final String OUTPUT_FILE_NAME = "output/demo/pipeline/result_%d.txt";
    private static final String SIMPLIFICATION_OUTPUT_FILE_NAME = "output/demo/simplification/result.ser";
    private static final String NO_OUTPUT = "no_output";
    private static final String NO_SIMPLIFICATION = "no_simplification";
    private static final PrintStream OUT = System.out;
    private static final PrintStream ERR = System.err;
    private static final PrintStream DUMMY_STREAM = new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    });

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        final Options options = new Options();
        options.addOption(createOptionalOptionNoArgument(NO_OUTPUT, "disable output to file"));
        options.addOption(createOptionalOptionNoArgument(NO_SIMPLIFICATION, "disable simplification system"));

        final CommandLineParser clp = new DefaultParser();
        CommandLine cmdLine;
        try {
            cmdLine = clp.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage() + "\n");
            return;
        }

        final ClassLoader classLoader = SimplificationDemo.class.getClassLoader();
        final TopicSentencesSimplificationAndQuestions result;
        if (cmdLine.hasOption(NO_SIMPLIFICATION)) {
            System.err.println("Skipping simplification system. Loading from file instead.");
            try (
                    InputStream file = new FileInputStream(SIMPLIFICATION_OUTPUT_FILE_NAME);
                    InputStream buffer = new BufferedInputStream(file);
                    ObjectInput input = new ObjectInputStream(buffer)
            ) {
                final TopicSentencesSimplification simplification = (TopicSentencesSimplification) input.readObject();
                System.setOut(DUMMY_STREAM);

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

                result = new TopicSentencesSimplificationAndQuestions(simplification.getSentenceToSimplifiedSentences(),
                        sentences);
                System.setOut(OUT);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        } else {
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
            final int processors = Runtime.getRuntime().availableProcessors();
            final ExecutorService executor = Executors.newFixedThreadPool(processors);
            for (final Text sentence : sentences) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Set<Text> strings = SentenceSimplifier.simplifySentence(sentence.getString());
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

            if (cmdLine.hasOption(NO_OUTPUT)) {
                System.err.println("Not writing simplification result to file.");
            } else {
                try (
                        OutputStream file = new FileOutputStream(SIMPLIFICATION_OUTPUT_FILE_NAME);
                        OutputStream buffer = new BufferedOutputStream(file);
                        ObjectOutput output = new ObjectOutputStream(buffer)
                ) {
                    output.writeObject(new TopicSentencesSimplification(sentenceToSimplifiedSentences));
                } catch (IOException ex) {
                    System.err.println("Cannot write object to file.");
                }
            }

            result = new TopicSentencesSimplificationAndQuestions(sentenceToSimplifiedSentences, sentences);
            System.setOut(OUT);
            //System.setErr(ERR);
        }

        if (cmdLine.hasOption(NO_OUTPUT)) {
            System.err.println("Skipping output to file. Writing to console instead.");
            System.out.println(result);
        } else {
            int n = 0;
            File file = new File(String.format(OUTPUT_FILE_NAME, n));
            while (file.exists()) {
                n++;
                file = new File(String.format(OUTPUT_FILE_NAME, n));
            }
            try {
                FileUtils.writeStringToFile(file, result.toString(), Charsets.UTF_8.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
