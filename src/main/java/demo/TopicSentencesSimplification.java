package demo;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TopicSentencesSimplification implements Serializable {
    private final Map<String, Set<String>> sentenceToSimplifiedSentences;
    private final int numberOfSimplifiedSentences;

    public TopicSentencesSimplification(Map<String, Set<String>> sentenceToSimplifiedSentences) {
        this.sentenceToSimplifiedSentences = sentenceToSimplifiedSentences;
        int numberOfSimplifiedSentences = 0;
        for (final Set<String> value : sentenceToSimplifiedSentences.values()) {
            numberOfSimplifiedSentences += value.size();
        }
        this.numberOfSimplifiedSentences = numberOfSimplifiedSentences;
    }

    @Override
    public int hashCode() {
        return numberOfSimplifiedSentences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TopicSentencesSimplification that = (TopicSentencesSimplification) o;

        return sentenceToSimplifiedSentences.equals(that.sentenceToSimplifiedSentences);

    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d topic sentences\n", sentenceToSimplifiedSentences.size()));
        builder.append(String.format("%d simplified sentences\n", numberOfSimplifiedSentences));
        for (Entry<String, Set<String>> entry : sentenceToSimplifiedSentences.entrySet()) {
            builder.append("---------------------------------\n\n");
            builder.append("Original Sentence:\n");
            builder.append(entry.getKey()).append("\n\n");
            builder.append("Simplified Sentences:\n");
            for (final String simplifiedSentence : entry.getValue()) {
                builder.append(simplifiedSentence).append("\n");
            }
            builder.append("\n---------------------------------\n\n");
        }

        return builder.toString();
    }
}
