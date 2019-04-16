package simplification;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import util.PosUtil;
import util.TreeUtil;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static generation.TextRealization.realizeSentence;
import static util.TreeUtil.labelEquals;
import static util.WordListUtil.constructPhraseFromWordList;
import static util.WordListUtil.removeParts;

public class SubVpExtractor implements Extractor {
    private static final Set<String> LABEL_BLACKLIST = ImmutableSet.of("s", "sbar");

    private static SubVpExtractor extractor;

    private SubVpExtractor() {
    }

    public static SubVpExtractor getExtractor() {
        if (extractor == null) {
            extractor = new SubVpExtractor();
        }
        return extractor;
    }

    public static void main(String[] args) {
//        System.out.println(getExtractor().extract(Joiner.on(' ').join(args)));
    }

    @Override
    public SimplificationResult extract(String sentence) {
//        System.out.println("Checking for sub VPs in the sentence: " + sentence);
        final Sentence parsed = new Sentence(sentence);
        final Tree root = parsed.parse();
        final RangeSet<Integer> partsToRemove = TreeRangeSet.create();
        final Set<String> simplifiedSentences = new LinkedHashSet<>();
        for (final Tree tree : root.getChild(0).children()) {
            if (labelEquals(tree, "vp")) {
//                System.out.println("Found main VP: " + tree);
                final String beforeVp = TreeUtil.getStringBeforeTree(root, tree);
                if (Strings.isNullOrEmpty(beforeVp)) {
                    continue;
                }
//                System.out.println("\tBefore VP: " + beforeVp);

                final Queue<Tree> trees = new LinkedList<>();
                trees.addAll(Arrays.asList(tree.children()));
                boolean npBetween = false;
                while (!trees.isEmpty()) {
                    final Tree subTree = trees.poll();
                    if (LABEL_BLACKLIST.contains(subTree.label().value().toLowerCase())) {
                        continue;
                    }

                    if (labelEquals(subTree, "np")) {
                        npBetween = true;
                    }

                    if (labelEquals(subTree, "vp") && npBetween) {
//                        System.out.println("Found sub VP: " + subTree);
                        final Tree subVpHead = TreeUtil.findHead(subTree).getLeaves().get(0);
//                        System.out.println("\tHead: " + subVpHead);

                        final String beVerb;
                        if (PosUtil.isPastTenseVerb(root, subVpHead)) {
                            beVerb = "was";
                        } else {
                            beVerb = "is";
                        }

                        final String subVp = TreeUtil.constructPhraseFromTree(subTree);

                        simplifiedSentences.add(realizeSentence(beforeVp, beVerb, subVp));

                        final int leftIndex = TreeUtil.getLeafIndex(root, subTree.getLeaves().get(0));
                        final int rightIndex = TreeUtil.getLeafIndex(root, Iterables.getLast(subTree.getLeaves()));
                        partsToRemove.add(Range.closed(leftIndex, rightIndex));
                    } else {
                        trees.addAll(Arrays.asList(subTree.children()));
                    }
                }
            }
        }
        if (!partsToRemove.isEmpty()) {
            final String removedParts = realizeSentence(
                    constructPhraseFromWordList(removeParts(parsed.words(), partsToRemove)));
            simplifiedSentences.add(removedParts);
        }

        if (simplifiedSentences.isEmpty()) {
            return new SimplificationResult(ImmutableSet.of(sentence));
        }
        return new SimplificationResult(simplifiedSentences);
    }
}
