package service;

import questionGeneration.runners.HeilmanRunner;
import questionGeneration.runners.NQGRunner;
import questionGeneration.vo.Output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class QGService {

    private Properties properties;
    private Map<String, String> reversedPhraseTable;
    public QGService(Properties properties, Map<String, String> reversedPhraseTable) {
        this.properties = properties;
        this.reversedPhraseTable = reversedPhraseTable;
    }

    public List<Output> generateQuestions(String sentFileLower, String paraFileLower, String sentFile) throws IOException, InterruptedException {

        NQGRunner nqgRunner = new NQGRunner(
                properties.getProperty("nqg.home"),
                properties.getProperty("nqg.model"),
                properties.getProperty("nqg.config"),
                sentFileLower,
                paraFileLower);
        HeilmanRunner heilmanRunner = new HeilmanRunner(
                properties.getProperty("heilman.home"),
                sentFile
        );

        List<Output> heilmanOutputs = heilmanRunner.getQuestions();
        List<Output> nqgOutputs = nqgRunner.getQuestions();
        List<Output> allout = new ArrayList<>();
        allout.addAll(nqgOutputs);
        allout.addAll(heilmanOutputs);
        for (Map.Entry<String, String> entry : reversedPhraseTable.entrySet()) {
            for (Output o : allout) {
                o.setQuestion(o.getQuestion().replaceAll(entry.getKey(), entry.getValue()));
            }
        }
        return allout;

    }
}
