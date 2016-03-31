package crawling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomParagraphPicker {
    private static final String PARAGRAPHS_FILE = "output/wikipedia_featured_articles/first_paragraphs.txt";

    public static void main(String[] args) {
        final int desiredNumberOfParagraphs;
        if (args.length == 0) {
            System.out.println("No number specified. Assuming one paragraph.");
            desiredNumberOfParagraphs = 1;
        } else {
            desiredNumberOfParagraphs = Integer.parseInt(args[0]);
        }

        try {
            final Scanner scanner = new Scanner(new File(PARAGRAPHS_FILE));
            final List<String> paragraphs = new ArrayList<>();
            while (scanner.hasNext()) {
                paragraphs.add(scanner.nextLine());
            }
            if (desiredNumberOfParagraphs > paragraphs.size()) {
                System.err.println("Not enough paragraphs to meet requested number");
                return;
            }

            final Random random = new Random();
            for (int i = 0; i < desiredNumberOfParagraphs; i++) {
                final int randomIndex = random.nextInt(paragraphs.size());
                final String paragraph = paragraphs.remove(randomIndex);
                System.out.println(paragraph);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Paragraph file does not exit");
        }
    }
}
