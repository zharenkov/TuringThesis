package simplification;

import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.TreeUtil;

import java.util.Collections;

public class ExistentialIgnore implements Extractor {
    private static ExistentialIgnore extractor;

    private ExistentialIgnore() {
    }

    public static ExistentialIgnore getExtractor() {
        if (extractor == null) {
            extractor = new ExistentialIgnore();
        }
        return extractor;
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final Tree root = parsed.parse();
        for (final Tree leaf : root.getLeaves()) {
            final Tree posTag = TreeUtil.getParent(root, leaf);
            if (TreeUtil.labelEquals(posTag, "ex")) {
//                System.out.println("Found evidence of existential sentence: Aborting!");
                return new SimplificationResult(Collections.<String>emptySet());
            }
        }
        return new SimplificationResult(ImmutableSet.of(sentence));
    }
}
