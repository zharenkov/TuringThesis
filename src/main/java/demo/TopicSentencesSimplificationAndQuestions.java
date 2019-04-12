package demo;

import data.Text;
import question.Rules;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TopicSentencesSimplificationAndQuestions implements Serializable {
    private final List<Text> sentences;
    private final Map<Text, Set<Text>> sentenceToSimplifiedSentences;
    private final Map<Text, Set<Text>> simplifiedSentenceToQuestions;
    private final int numberOfSimplifiedSentences;
    private final int numberOfGeneratedQuestions;

    public TopicSentencesSimplificationAndQuestions(Map<Text, Set<Text>> sentenceToSimplifiedSentences, List<Text> sentences) {
        this.sentences = sentences;
        this.sentenceToSimplifiedSentences = sentenceToSimplifiedSentences;
        simplifiedSentenceToQuestions = new HashMap<>();
        int numberOfSimplifiedSentences = 0;
        final AtomicInteger numberOfGeneratedQuestions = new AtomicInteger(0);
        final int processors = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(processors);
        for (final Set<Text> value : sentenceToSimplifiedSentences.values()) {
            numberOfSimplifiedSentences += value.size();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    for (final Text simplifiedSentence : value) {
                        final Set<Text> generatedQuestions = Rules.generateQuestions(simplifiedSentence.getString());
                        simplifiedSentenceToQuestions.put(simplifiedSentence, generatedQuestions);
                        numberOfGeneratedQuestions.addAndGet(generatedQuestions.size());
                    }
                }
            });
        }

        executor.shutdown();
        final long startTime = System.currentTimeMillis();
        System.err.println("Waiting for all generation tasks to finish");
        try {
            executor.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("All generation tasks finished");
        final long endTime = System.currentTimeMillis();
        final long secondsToFinish = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.MILLISECONDS);
        System.err.println("Time to generation questions: " + secondsToFinish + " seconds");

        this.numberOfSimplifiedSentences = numberOfSimplifiedSentences;
        this.numberOfGeneratedQuestions = numberOfGeneratedQuestions.get();
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
        for (final Text sentence : sentences) {
            builder.append("---------------------------------\n\n");
            builder.append("Original Sentence:\n");
            builder.append(sentence).append("\n\n");
            builder.append("Simplified Sentences:\n");
            for (final Text simplifiedSentence : sentenceToSimplifiedSentences.get(sentence)) {
                builder.append(simplifiedSentence).append("\n");
                final Set<Text> questions = simplifiedSentenceToQuestions.get(simplifiedSentence);
                if (questions != null) {
                    for (final Text question : questions) {
                        builder.append("\t").append(question).append("\n");
                    }
                }
            }
            builder.append("\n---------------------------------\n\n");
        }

        return builder.toString();
    }

    public List<Text> getSentences() {
        return sentences;
    }

    public Map<Text, Set<Text>> getSentenceToSimplifiedSentences() {
        return sentenceToSimplifiedSentences;
    }

    public Map<Text, Set<Text>> getSimplifiedSentenceToQuestions() {
        return simplifiedSentenceToQuestions;
    }
}
