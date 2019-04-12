package demo;

import data.Text;
import org.apache.commons.io.FileUtils;
import question.Rules;
import simplification.SentenceSimplifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class SpecificSentenceDemo {
    public static void main(String[] args) throws IOException {
        String sentenceFile = "simple.txt";
        final InputStream sentenceFileS = ClassLoader.getSystemClassLoader().getResourceAsStream(sentenceFile);//args[0];
        try {
            final Scanner scanner = new Scanner(sentenceFileS);
            final StringBuilder stringBuilder = new StringBuilder();
            int numberOfSentences = 0;
            int numberOfSimplifiedSentences = 0;
            int numberOfQuestionsGenerated = 0;
            while (scanner.hasNext()) {
                numberOfSentences++;
                final String originalSentence = scanner.nextLine();
                stringBuilder.append(originalSentence).append("\n");
                final Set<Text> simplifiedSentences = SentenceSimplifier.simplifySentence(originalSentence);
                numberOfSimplifiedSentences += simplifiedSentences.size();
                final Set<Text> generatedQuestions = new LinkedHashSet<>();
                for (final Text simplifiedSentence : simplifiedSentences) {
                    final Set<Text> questions = Rules.generateQuestions(simplifiedSentence.getString());
                    generatedQuestions.addAll(questions);
                    stringBuilder.append("\t").append(simplifiedSentence).append("\n");
                    for (final Text question : questions) {
                        stringBuilder.append("\t\t").append(question).append("\n");
                    }
                }
                numberOfQuestionsGenerated += generatedQuestions.size();
                stringBuilder.append("\n");
            }

            int fileNumber = 0;
            File outputFile = new File(sentenceFile.replace(".txt", "_" + fileNumber + "_result.txt"));
            while (outputFile.exists()) {
                fileNumber++;
                outputFile = new File(sentenceFile.replace(".txt", "_" + fileNumber + "_result.txt"));
            }

            final StringBuilder finalBuilder = new StringBuilder();
            finalBuilder.append("Number of sentences: ").append(numberOfSentences);
            finalBuilder.append("\n");
            finalBuilder.append("Number of simplified sentences: ").append(numberOfSimplifiedSentences);
            finalBuilder.append("\n");
            finalBuilder.append("Number of questions: ").append(numberOfQuestionsGenerated);
            finalBuilder.append("\n");
            finalBuilder.append("\n");
            finalBuilder.append(stringBuilder.toString());
            FileUtils.writeStringToFile(outputFile, finalBuilder.toString());
        } catch (FileNotFoundException e) {
            System.err.println("Specified file does not exist");
        }
    }
}
