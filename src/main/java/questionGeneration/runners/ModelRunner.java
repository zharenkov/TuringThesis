package questionGeneration.runners;


import questionGeneration.vo.Output;

import java.io.IOException;
import java.util.List;

public interface ModelRunner {

    List<? extends Output> getQuestions() throws IOException, InterruptedException;
}
