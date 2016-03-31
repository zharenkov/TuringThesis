package demo;

import org.apache.commons.io.FileUtils;
import question.Rules;
import simplification.SentenceSimplifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class SpecificSentenceDemo {
    public static void main(String[] args) throws IOException {
        final String sentenceFile = args[0];
        try {
            final Scanner scanner = new Scanner(new File(sentenceFile));
            final StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                final String originalSentence = scanner.nextLine();
                final Set<String> simplifiedSentences = SentenceSimplifier.simplifySentence(originalSentence);
                final Set<String> generatedQuestions = new LinkedHashSet<>();
                for (final String simplifiedSentence : simplifiedSentences) {
                    generatedQuestions.addAll(Rules.generateQuestions(simplifiedSentence));
                }
                stringBuilder.append(originalSentence).append("\n");
                for (final String question : generatedQuestions) {
                    stringBuilder.append("\t").append(question).append("\n");
                }
                stringBuilder.append("\n");
            }
            final File outputFile = new File(sentenceFile.replace(".txt", "_result.txt"));
            FileUtils.writeStringToFile(outputFile, stringBuilder.toString());
        } catch (FileNotFoundException e) {
            System.err.println("Specified file does not exist");
        }
    }
}
