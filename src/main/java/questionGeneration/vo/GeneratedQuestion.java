package questionGeneration.vo;

public class GeneratedQuestion implements Comparable {

    String sentence;
    String question;
    Double score;
    String model;

    public GeneratedQuestion(String model, String sentence, String question, Double score) {
        this.model = model;
        this.sentence = sentence;
        this.question = question;
        this.score = score;
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


    public void setScore(Double score) {
        this.score = score;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public int compareTo(Object o) {
        if (this.score < ((GeneratedQuestion)o).score) return -1;
        if (this.score > ((GeneratedQuestion)o).score) return 1;
        return 0;
    }

    @Override
    public String toString() {
        return "{" + "\n" +
                //"sentence='" + sentence + '\'' +"\n" +
                ", question='" + question + '\'' +"\n" +
                ", score=" + score +"\n" +
                //", model='" + model + '\'' +"\n" +
                '}';
    }
}
