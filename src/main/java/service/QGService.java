package service;

import org.apache.commons.io.FileUtils;
import pipeline.SpotPipeline;
import questionGeneration.runners.HeilmanRunner;
import questionGeneration.runners.NQGRunner;
import questionGeneration.vo.GeneratedQuestion;
import util.ReplaceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class QGService {

    public static final String HYPO_TEMPLATE = "hypo_%d.txt";
    public static final String REF_TEMPLATE = "ref_%d.txt";

    private Properties properties;

    public QGService(Properties properties) {
        this.properties = properties;
    }

    public List<GeneratedQuestion> generateQuestions(String sentFileLower, String paraFileLower, String sentFile) throws IOException, InterruptedException, ExecutionException {

        String root = properties.getProperty("home");
        String evalRoot = properties.getProperty("eval.home");
        String threshold = properties.getProperty("eval.threshold");
        //generate questions
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
        List<GeneratedQuestion> allout = new ArrayList<>();
//        allout.addAll(nqgRunner.getQuestions());
//        allout.addAll(heilmanRunner.getQuestions());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<List<GeneratedQuestion>>> questionsFutureList = executor.invokeAll(Arrays.asList(nqgRunner, heilmanRunner));
        for (Future<List<GeneratedQuestion>> f : questionsFutureList) {
            allout.addAll(f.get());
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);

        //evaluation
        //prepare files
        long millis = System.currentTimeMillis();
        String hypoFilePath = root + String.format(HYPO_TEMPLATE, millis);
        String refFilePath = root + String.format(REF_TEMPLATE, millis);
        File hypoFile = new File(hypoFilePath);
        File refFile = new File(refFilePath);
        FileUtils.writeLines(hypoFile, allout.stream().map(GeneratedQuestion::getQuestion).collect(Collectors.toList()));
        FileUtils.writeLines(refFile, allout.stream().map(GeneratedQuestion::getSentence).collect(Collectors.toList()));
        //evaluate
        EvaluationService evaluationService = new EvaluationService(evalRoot, hypoFilePath, refFilePath);
        List<Double> scores = evaluationService.evaluateScores();
        for (int i = 0; i < allout.size(); i++) {
            allout.get(i).setScore(scores.get(i));
        }
        //rm files
        hypoFile.delete();
        refFile.delete();

        //filter best questions
        allout = allout.stream().filter(output ->
                output.getScore() >= Double.valueOf(threshold))
                        //            && output.getQuestion().split("\\s+").length < 10 )
                        .collect(Collectors.toList());

        //replace word by phrasetable
        allout.forEach(out ->
                out.setQuestion(ReplaceUtils.replaceWords(out.getQuestion(), SpotPipeline.reversedPhraseTable))
        );

        return allout;

    }
}
