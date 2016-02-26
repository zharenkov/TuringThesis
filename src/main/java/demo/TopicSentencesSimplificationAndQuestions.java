package demo;

import question.Rules;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TopicSentencesSimplificationAndQuestions implements Serializable {
    private final Map<String, Set<String>> sentenceToSimplifiedSentences;
    private final Map<String, Set<String>> simplifiedSentenceToQuestions;
    private final int numberOfSimplifiedSentences;
    private final int numberOfGeneratedQuestions;

    public TopicSentencesSimplificationAndQuestions(Map<String, Set<String>> sentenceToSimplifiedSentences) {
        this.sentenceToSimplifiedSentences = sentenceToSimplifiedSentences;
        simplifiedSentenceToQuestions = new HashMap<>();
        int numberOfSimplifiedSentences = 0;
        int numberOfGeneratedQuestions = 0;
        for (final Set<String> value : sentenceToSimplifiedSentences.values()) {
            numberOfSimplifiedSentences += value.size();
            for (final String simplifiedSentence : value) {
                final Set<String> generatedQuestions = Rules.generateQuestions(simplifiedSentence);
                simplifiedSentenceToQuestions.put(simplifiedSentence, generatedQuestions);
                numberOfGeneratedQuestions += generatedQuestions.size();
            }
        }
        this.numberOfSimplifiedSentences = numberOfSimplifiedSentences;
        this.numberOfGeneratedQuestions = numberOfGeneratedQuestions;
    }

    @Override
    public int hashCode() {
        return numberOfSimplifiedSentences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TopicSentencesSimplificationAndQuestions that = (TopicSentencesSimplificationAndQuestions) o;

        return sentenceToSimplifiedSentences.equals(that.sentenceToSimplifiedSentences);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d topic sentences\n", sentenceToSimplifiedSentences.size()));
        builder.append(String.format("%d simplified sentences\n", numberOfSimplifiedSentences));
        builder.append(String.format("%d generated questions\n", numberOfGeneratedQuestions));
        for (Entry<String, Set<String>> entry : sentenceToSimplifiedSentences.entrySet()) {
            builder.append("---------------------------------\n\n");
            builder.append("Original Sentence:\n");
            builder.append(entry.getKey()).append("\n\n");
            builder.append("Simplified Sentences:\n");
            for (final String simplifiedSentence : entry.getValue()) {
                builder.append(simplifiedSentence).append("\n");
                final Set<String> questions = simplifiedSentenceToQuestions.get(simplifiedSentence);
                if (questions != null) {
                    for (final String question : questions) {
                        builder.append("\t").append(question).append("\n");
                    }
                }
            }
            builder.append("\n---------------------------------\n\n");
        }

        return builder.toString();
    }
}
