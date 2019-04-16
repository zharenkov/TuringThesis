package service;

import org.apache.commons.lang3.StringUtils;
import questionGeneration.runners.ArgLineCollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EvaluationService {

    private String home;
    private String hypothesisFile;
    private String referenceFile;

    public EvaluationService(String home, String hypothesisFile, String referenceFile) {
        this.home = home;
        this.hypothesisFile = hypothesisFile;
        this.referenceFile = referenceFile;
    }

    public List<Double> evaluateScores() throws IOException, InterruptedException {


        String command = "python answerability_score.py --data_type squad" +
                " --ref_file %s " +
                "--hyp_file %s " +
                "--ner_weight 0.6 --qt_weight 0.2 --re_weight 0.1 --delta 0.7" +
                " --ngram_metric ROUGE_L " +
                " --output_dir %s";

        ProcessBuilder pb = new ProcessBuilder(String.format(command, referenceFile, hypothesisFile, UUID.randomUUID().toString()).split("\\s+"));
        pb.directory(new File(home));
        Process run = pb.start();
        List<String> out = new ArgLineCollector(run).collectOutput(true);
        run.waitFor();
        return processOutput(out);
    }

    private List<Double> processOutput(List<String> output) throws IOException {
        List<String> questions = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        for (String s : output) {
            if (StringUtils.isNotEmpty(s)){
                s = s.trim();
                if (s.startsWith("Question:")) {
                    questions.add(s.replace("Question:","").trim());
                }
                if (s.startsWith("Score:")) {
                    scores.add(Double.valueOf(s.replace("Score:","").trim()));
                }
            }
        }
        return scores;
    }


}
