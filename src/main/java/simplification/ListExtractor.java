package simplification;

import edu.stanford.nlp.simple.Sentence;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ListExtractor implements Extractor {
    private final static Pattern nounAdjPattern = Pattern.compile("(((jj)\\s(nn)|(jj)|(nn))\\s((cc)|,)\\s){2,}((jj)\\s(nn)|(jj)|(nn))");
    private final static Pattern verbPatter = Pattern.compile("((vb\\snn|vb)\\s((cc)|,)\\s){2,}((vb\\snn)|vb)");

    private Pattern pattern;

    public ListExtractor(Pattern pattern) {
        this.pattern = pattern;
    }

    public static ListExtractor getExtractor(Pattern pattern) {
        return new ListExtractor(pattern);
    }

    @Override
    public SimplificationResult extract(String sentence) {
        final Sentence parsed = new Sentence(sentence);
        final List<String> words = parsed.words();
        List<String> pos = parsed.posTags().stream()
                .map(tag -> tag.length() < 2 ? tag.toLowerCase() : tag.substring(0, 2).toLowerCase())
                .collect(Collectors.toList());
        String posString = pos.stream().collect(Collectors.joining(" "));
        Matcher matcher = pattern.matcher(posString);
        if (!matcher.find()) {
            return new SimplificationResult(Collections.singleton(sentence));
        }
        return new SimplificationResult(simplify(posString, matcher, words));
    }

    private Set<String> simplify(String posString, Matcher matcher, List<String> words) {
        String startPOS = posString.substring(0, matcher.start());
        String endPOS = posString.substring(matcher.end());
        int wordsBeforeCnt = StringUtils.isEmpty(startPOS) ? 0 : startPOS.trim().split("\\s+").length;
        int wordsAfterCnt = StringUtils.isEmpty(endPOS) ? 0 : endPOS.trim().split("\\s+").length;
        String wordsBefore = words.subList(0, wordsBeforeCnt)
                .stream()
                .collect(Collectors.joining(" "));
        String wordsAfter = words.subList(words.size() - wordsAfterCnt, words.size())
                .stream()
                .collect(Collectors.joining(" "));
        List<String> homogeneousPart = words.subList(wordsBeforeCnt, words.size() - wordsAfterCnt);
        Set<String> splitWords = new HashSet<>(Arrays.asList(",", "and"));
        Set<String> simplifiedSentences = new HashSet<>();
        StringBuilder sb = new StringBuilder(wordsBefore);
        for (int i = 0; i < homogeneousPart.size(); i++) {
            String part = homogeneousPart.get(i);
            if (!splitWords.contains(part)) {
                sb.append(" ").append(part);
                if (i == homogeneousPart.size() - 1) {
                    sb.append(" ").append(wordsAfter).append(" ");
                    simplifiedSentences.add(sb.toString());
                }
            } else {
                sb.append(" ").append(wordsAfter).append(" ");
                simplifiedSentences.add(sb.toString());
                sb = new StringBuilder(wordsBefore);
            }
        }
        return simplifiedSentences;
    }

    public static void main(String[] args) {
        ListExtractor e = new ListExtractor(verbPatter);
        e.extract("I love,hate,adore my beautiful mom, dad and cool sister.");
        e.extract("phone consist of screen, button, battery, body.");
    }
}
