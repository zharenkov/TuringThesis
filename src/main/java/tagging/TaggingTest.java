package tagging;

import com.google.common.base.Joiner;
import edu.stanford.nlp.trees.Tree;
import lemmatizing.Lemmatizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class TaggingTest {
    final static StanfordParser parser = new StanfordParser();

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            final URL topicSentences = TaggingTest.class.getResource("/topic_sentences.txt");
            final Scanner scanner = new Scanner(new File(topicSentences.getFile()));
            while (scanner.hasNext()) {
                final String sentence = scanner.nextLine();
                printSentenceParseTree(sentence);
            }
        } else {

            // Wait for the English model to load in order to keep debug text out of the results
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final String sentence = Joiner.on(' ').join(args);
            printSentenceParseTree(sentence);
        }
    }

    private static void printSentenceParseTree(String sentence) {
        System.out.println("Sentence being analyzed: \"" + sentence + "\"\n---------------------------");

        sentence = sentence.replaceAll("\\.", "");

        final Tree tree = parser.parse(sentence);
        final List<Tree> trees = tree.getChild(0).getChildrenAsList();
        for (final Tree part : trees) {
            System.out.print(part.label());
            System.out.println(part);
        }
        tree.indentedListPrint();

        System.out.println();

        final List<Tree> leaves = tree.getLeaves();
        for (final Tree leaf : leaves) {
            System.out.printf("(%s - %s), ", leaf.parent(tree).label(), leaf);
        }
        System.out.println("\n");

        System.out.print("Lemmatized: ");
        for (final Tree leaf : leaves) {
            System.out.printf("%s ", Lemmatizer.lemmatize(leaf.nodeString(), leaf.parent(tree).label().value()));
        }
        System.out.println("\n");

        System.out.println(parser.getDependencies(tree));
    }
}
