package questionGeneration.runners;


import questionGeneration.vo.GeneratedQuestion;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public interface ModelRunner extends Callable<List<GeneratedQuestion>> {

    List<? extends GeneratedQuestion> getQuestions() throws IOException, InterruptedException;
}
