package simplification;

import com.google.common.base.Joiner;

public class SentenceSimplifier {
    public static void main(String[] args) {
        String sentence = Joiner.on(' ').join(args);
        sentence = ParentheticalExtractor.removeParentheticals(sentence);
        sentence = AppositiveAndRelativeClauseExtractor.removeNonRestrictiveAppositivesAndRelativeClauses(sentence);
        System.out.println(sentence);
    }
}
