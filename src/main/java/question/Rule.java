package question;

import tagging.Sentence;

import java.util.Set;

public interface Rule {
    Set<String> generateQuestions(Sentence sentence);
}
