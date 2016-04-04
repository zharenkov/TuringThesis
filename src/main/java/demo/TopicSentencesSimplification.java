package demo;

import data.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TopicSentencesSimplification implements Serializable {
    private final Map<Text, Set<Text>> sentenceToSimplifiedSentences;
    private final int numberOfSimplifiedSentences;

    public TopicSentencesSimplification(Map<Text, Set<Text>> sentenceToSimplifiedSentences) {
        this.sentenceToSimplifiedSentences = sentenceToSimplifiedSentences;
        int numberOfSimplifiedSentences = 0;
        for (final Set<Text> value : sentenceToSimplifiedSentences.values()) {
            numberOfSimplifiedSentences += value.size();
        }
        this.numberOfSimplifiedSentences = numberOfSimplifiedSentences;
    }

    public List<Text> getSentences() {
        final List<Text> sentences = new ArrayList<>(sentenceToSimplifiedSentences.size());
        sentences.addAll(sentenceToSimplifiedSentences.keySet());
        return sentences;
    }

    public Map<Text, Set<Text>> getSentenceToSimplifiedSentences() {
        return sentenceToSimplifiedSentences;
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
        for (Entry<Text, Set<Text>> entry : sentenceToSimplifiedSentences.entrySet()) {
            builder.append("---------------------------------\n\n");
            builder.append("Original Sentence:\n");
            builder.append(entry.getKey()).append("\n\n");
            builder.append("Simplified Sentences:\n");
            for (final Text simplifiedSentence : entry.getValue()) {
                builder.append(simplifiedSentence).append("\n");
            }
            builder.append("\n---------------------------------\n\n");
        }

        return builder.toString();
    }
}
