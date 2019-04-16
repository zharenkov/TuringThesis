package vo;

import questionGeneration.vo.GeneratedQuestion;

import java.util.List;

public class ParagraphWithSimplifications {

    private String sourceParagraph;
    private String simplifiedParagraph;
    private List<String> simplifiedSentences;
    private List<GeneratedQuestion> questions;

    public ParagraphWithSimplifications(String sourceParagraph, String simplifiedParagraph, List<String> simplifiedSentences, List<GeneratedQuestion> questions) {
        this.sourceParagraph = sourceParagraph;
        this.simplifiedParagraph = simplifiedParagraph;
        this.simplifiedSentences = simplifiedSentences;
        this.questions = questions;
    }

    public String getSourceParagraph() {
        return sourceParagraph;
    }

    public String getSimplifiedParagraph() {
        return simplifiedParagraph;
    }

    public List<String> getSimplifiedSentences() {
        return simplifiedSentences;
    }

    public List<GeneratedQuestion> getQuestions() {
        return questions;
    }

    @Override
    public String toString() {
        return "ParagraphWithSimplifications{" +
                "sourceParagraph='" + sourceParagraph + '\'' + "\n" +
                //", simplifiedParagraph='" + simplifiedParagraph + '\'' +"\n" +
                //", simplifiedSentences=" + simplifiedSentences +"\n" +
                ", questions=" + questions +"\n" +
                '}';
    }
}
