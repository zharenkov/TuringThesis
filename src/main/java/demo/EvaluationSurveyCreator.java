package demo;

import data.Text;
import question.Rules;
import simplification.SentenceSimplifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import static org.apache.commons.io.FileUtils.writeStringToFile;

public class EvaluationSurveyCreator {
    private static final String OUTPUT_FILENAME = "output/demo/pipeline/evaluation_survey_%d.txt";

    public static void main(String[] args) throws IOException {
        if (args[0].equalsIgnoreCase("pair")) {
            final Scanner scanner = new Scanner(new File(args[1]));
            final Set<String> sentences = new HashSet<>();
            while (scanner.hasNext()) {
                sentences.add(scanner.nextLine());
            }

            int totalQuestionsGenerated = 0;
            final Map<Text, String> questionsToOriginalSentence = new HashMap<>();
            for (final String sentence : sentences) {
                final Set<Text> simplifiedSentences = SentenceSimplifier.simplifySentence(sentence);
                for (final Text simplifiedSentence : simplifiedSentences) {
                    final Set<Text> generatedQuestions = Rules.generateQuestions(simplifiedSentence.getString());
                    totalQuestionsGenerated += generatedQuestions.size();
                    for (final Text question : generatedQuestions) {
                        questionsToOriginalSentence.put(question, sentence);
                    }
                }
            }
            System.out.println("Total questions generated: " + totalQuestionsGenerated);
            final List<Entry<Text, String>> questionsAndOriginalSentences = new ArrayList<>(
                    questionsToOriginalSentence.entrySet());

            final Random random = new Random();
            for (int i = 0; i < 80; i++) {
                final Entry<Text, String> entry = questionsAndOriginalSentences.remove(
                        random.nextInt(questionsAndOriginalSentences.size()));
                System.out.println(entry.getValue());
                System.out.println(entry.getKey());
                System.out.println();
            }
        } else if (args[0].equalsIgnoreCase("format")) {
            final Scanner scanner = new Scanner(new File(args[1]));
            StringBuilder stringBuilder = new StringBuilder();

            int index = 0;
            int numCompleted = 0;
            while (scanner.hasNext()) {
                final String sentence = scanner.nextLine();
                final String question = scanner.nextLine();
                scanner.nextLine();

                stringBuilder.append("\n[[PageBreak]]\n");
                stringBuilder.append("\n");
                stringBuilder.append("[[Question:DB]]\n");
                stringBuilder.append("<p><b>Sentence:</b> \"" + sentence + "\"</p>\n");
                stringBuilder.append("<p><br></p>\n");
                stringBuilder.append("<p><b>Question:</b> \"" + question + "\"</p>\n");
                stringBuilder.append("\n");
                stringBuilder.append("[[Question:MC]]\n");
                stringBuilder.append("How grammatically correct is the question?\n");
                stringBuilder.append("[[Choices]]\n");
                stringBuilder.append("5 - Very Good\n");
                stringBuilder.append("4 - Good\n");
                stringBuilder.append("3 - Okay\n");
                stringBuilder.append("2 - Bad\n");
                stringBuilder.append("1 - Very Bad\n");
                stringBuilder.append("\n");
                stringBuilder.append("[[Question:MC]]\n");
                stringBuilder.append("How well does the sentence answer the question?\n");
                stringBuilder.append("[[Choices]]\n");
                stringBuilder.append("5 - Very Good\n");
                stringBuilder.append("4 - Good\n");
                stringBuilder.append("3 - Okay\n");
                stringBuilder.append("2 - Bad\n");
                stringBuilder.append("1 - Very Bad\n");

                numCompleted++;
                if (numCompleted % 20 == 0) {
                    writeStringToFile(new File(String.format(OUTPUT_FILENAME, index)), stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    index++;
                }
            }
        }
    }
}
