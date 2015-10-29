package tagging;

import com.google.common.base.Joiner;
import edu.stanford.nlp.trees.Tree;

public class TaggingTest {
    final static StanfordParser parser = new StanfordParser();

    public static void main(String[] args) {
        final String sentence = Joiner.on(' ').join(args);
        final Tree tree = parser.parse(sentence);
        System.out.println(tree);
    }
}
