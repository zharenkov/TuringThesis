package tagging;

import edu.stanford.nlp.simple.Sentence;

public class SimpleCoreNLPTest {
    public static void main(String[] args) {
        final Sentence sentence = new Sentence(
                "George Washington (February 22, 1732 – December 14, 1799) was the first President of the United States (1789–97).");
        System.out.println(sentence.nerTags());
        System.out.println(sentence.mentions("PERSON"));
        System.out.println(sentence.dependencyGraph());
        System.out.println(sentence.lemmas());
        System.out.println(sentence.posTags());
    }
}
