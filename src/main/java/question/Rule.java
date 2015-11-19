package question;

import tagging.Sentence;

import java.util.List;

public interface Rule {
    List<String> generateQuestions(Sentence sentence);
}
