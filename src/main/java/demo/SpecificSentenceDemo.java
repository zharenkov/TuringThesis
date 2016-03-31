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
            int numberOfSentences = 0;
            int numberOfSimplifiedSentences = 0;
            int numberOfQuestionsGenerated = 0;
            while (scanner.hasNext()) {
                numberOfSentences++;
                final String originalSentence = scanner.nextLine();
                final Set<String> simplifiedSentences = SentenceSimplifier.simplifySentence(originalSentence);
                numberOfSimplifiedSentences += simplifiedSentences.size();
                final Set<String> generatedQuestions = new LinkedHashSet<>();
                for (final String simplifiedSentence : simplifiedSentences) {
                    final Set<String> questions = Rules.generateQuestions(simplifiedSentence);
                    generatedQuestions.addAll(questions);
                    numberOfQuestionsGenerated += questions.size();
                }
                stringBuilder.append(originalSentence).append("\n");
                for (final String question : generatedQuestions) {
                    stringBuilder.append("\t").append(question).append("\n");
                }
                stringBuilder.append("\n");
            }
            final File outputFile = new File(sentenceFile.replace(".txt", "_result.txt"));
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
