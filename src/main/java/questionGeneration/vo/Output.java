package questionGeneration.vo;

public class Output implements Comparable {

    String sentence;
    String question;
    Double score;
    String model;
    String paragraph;

    public Output(String model, String sentence, String question, Double score, String paragraph) {
        this.model = model;
        this.sentence = sentence;
        this.question = question;
        this.score = score;
        this.paragraph = paragraph;
    }

    public String getSentence() {
        return sentence;
    }

    public String getQuestion() {
        return question;
    }

    public Double getScore() {
        return score;
    }

    public String getModel() {
        return model;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public int compareTo(Object o) {
        if (this.score < ((Output)o).score) return -1;
        if (this.score > ((Output)o).score) return 1;
        return 0;
    }

    @Override
    public String toString() {
        return "Output{" +
                "sentence='" + sentence + '\'' +"\n" +
                ", question='" + question + '\'' +"\n" +
                ", score=" + score +"\n" +
                ", model='" + model + '\'' +"\n" +
                ", paragraph='" + paragraph + '\'' +"\n" +
                '}';
    }
}
