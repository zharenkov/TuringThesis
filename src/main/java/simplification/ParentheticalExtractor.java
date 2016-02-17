package simplification;

import com.google.common.base.Joiner;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import generation.TextRealization;
import simplenlg.features.Tense;
import util.TreeUtil;
import util.WordListUtil;

import java.util.*;
import java.util.regex.Pattern;

public class ParentheticalExtractor implements Extractor {
    private static final String LEFT_PARENTHESIS = "-LRB-";
    private static final String RIGHT_PARENTHESIS = "-RRB-";

    private static ParentheticalExtractor extractor;

    private ParentheticalExtractor() {
    }

    public static ParentheticalExtractor getExtractor() {
        if (extractor == null) {
            extractor = new ParentheticalExtractor();
        }
        return extractor;
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        System.out.println("Original sentence: " + words);

        int start = -1;
        final Stack<String> parenthesis = new Stack<>();
        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        final Set<String> simplifiedSentences = new HashSet<>();
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals(LEFT_PARENTHESIS)) {
                if (start == -1) {
                    start = i;
                }
                parenthesis.add("(");
            } else if (words.get(i).equals(RIGHT_PARENTHESIS)) {
                if (parenthesis.isEmpty()) {
                    System.err.println("Mismatching parenthesis! Aborting extraction.");
                }
                parenthesis.pop();
                if (parenthesis.isEmpty()) {
                    rangeSet.add(Range.closed(start, i));
                    start = -1;
                }
            }
        }

        // Check to see if we can derive simple sentences from each parenthetical
        for (final Range<Integer> parenthetical : rangeSet.asRanges()) {
            final List<String> nerTags = parsed.nerTags();
            // If the word before the parenthetical is a person
            if (nerTags.get(parenthetical.lowerEndpoint() - 1).equalsIgnoreCase("person")) {
                final List<String> dates = getDates(words, nerTags, parenthetical);
                final String personName = getPersonName(words, nerTags, parenthetical.lowerEndpoint() - 1);
                System.out.println(dates);
                if (dates.size() == 2) {
                    simplifiedSentences.add(TextRealization.realizeSentence(personName, "was born", dates.get(0)));
                    simplifiedSentences.add(TextRealization.realizeSentence(personName, "died", dates.get(1)));
                }
                final List<String> posTags = parsed.posTags();
                // If the first word of the parenthetical is a verb, construct a simple sentence with the VP
                final String posFirstWordParenthetical = posTags.get(parenthetical.lowerEndpoint() + 1).toLowerCase();
                if (posFirstWordParenthetical.startsWith("vb")) {
                    final Tree parse = parsed.parse();
                    final Tree vp = TreeUtil.getVpFromWord(parse,
                            parse.getLeaves().get(parenthetical.lowerEndpoint() + 1));
                    final String vpString = WordListUtil.constructPhraseFromTree(vp);
                    final String realizedVp;
                    if (posFirstWordParenthetical.equals("vbd") || posFirstWordParenthetical.equals("vbn")) {
                        realizedVp = TextRealization.realizeVerbPhraseWithFeatures(vpString, true, Tense.PAST);
                    } else {
                        realizedVp = TextRealization.realizeVerbPhraseWithFeatures(vpString, true, Tense.PRESENT);
                    }
                    simplifiedSentences.add(TextRealization.realizeSentence(personName, realizedVp));
                }
            }
            // Check if the parenthetical is an acronym (one word with all upper-case letters)
            if (parenthetical.upperEndpoint() - parenthetical.lowerEndpoint() == 2) {
                final String word = words.get(parenthetical.lowerEndpoint() + 1);
                final Pattern pattern = Pattern.compile("[A-Z]+");
                if (pattern.matcher(word).matches()) {
                    final String fullName = getFullName(parsed.parse(), words, nerTags,
                            parenthetical.lowerEndpoint() - 1);
                    simplifiedSentences.add(TextRealization.realizeSentence(word, "stands for", fullName + "."));
                }
            }
        }

        final List<String> modified = WordListUtil.removeParts(words, rangeSet);
        System.out.println("With parentheticals removed: " + modified);
        simplifiedSentences.add(WordListUtil.constructSentenceFromWordList(modified));
        return new SimplificationResult(simplifiedSentences);
    }

    private static List<String> getDates(List<String> words, List<String> nerTags, Range<Integer> range) {
        final List<String> dates = new ArrayList<>();
        List<String> currentDate = new ArrayList<>();
        for (int i = range.lowerEndpoint(); i < range.upperEndpoint(); i++) {
            if (nerTags.get(i).equalsIgnoreCase("date")) {
                currentDate.add(words.get(i));
            } else {
                if (!currentDate.isEmpty()) {
                    dates.add(WordListUtil.constructSentenceFromWordList(currentDate));
                    currentDate.clear();
                }
            }
        }
        if (!currentDate.isEmpty()) {
            dates.add(WordListUtil.constructSentenceFromWordList(currentDate));
            currentDate.clear();
        }
        return dates;
    }

    private static String getPersonName(List<String> words, List<String> nerTags, int indexOfLastPartOfName) {
        final List<String> nameParts = new ArrayList<>();
        for (int i = indexOfLastPartOfName; i >= 0; i--) {
            if (!nerTags.get(i).equalsIgnoreCase("person")) {
                break;
            }
            nameParts.add(words.get(i));
        }
        Collections.reverse(nameParts);
        return Joiner.on(' ').join(nameParts);
    }

    private static String getFullName(Tree root, List<String> words, List<String> nerTags, int indexOfLastPartOfName) {
        if (nerTags.get(indexOfLastPartOfName).equalsIgnoreCase("organization")) {
            final List<String> nameParts = new ArrayList<>();
            for (int i = indexOfLastPartOfName; i >= 0; i--) {
                if (!nerTags.get(i).equalsIgnoreCase("organization")) {
                    break;
                }
                nameParts.add(words.get(i));
            }
            Collections.reverse(nameParts);
            return Joiner.on(' ').join(nameParts);
        } else {
            return WordListUtil.constructPhraseFromTree(
                    TreeUtil.getNpFromWord(root, root.getLeaves().get(indexOfLastPartOfName)));
        }
    }
}
