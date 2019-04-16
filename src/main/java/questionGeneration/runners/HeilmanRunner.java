package questionGeneration.runners;


import questionGeneration.vo.GeneratedQuestion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeilmanRunner implements ModelRunner {

    private String home;
    private String inputFile;

    public HeilmanRunner(String home, String inputFile) {
        this.home = home;
        this.inputFile = inputFile;
    }

    @Override
    public List<GeneratedQuestion> getQuestions() throws IOException, InterruptedException {
        //String home = "/home/opc/QuestionGeneration/";
        //String home = "/Volumes/MacintoshHDD/Downloads/Safari/QuestionGeneration/";
        String command = "java -Xmx500m -cp question-generation.jar " +
                "edu/cmu/ark/QuestionAsker " +
                "--verbose --model models/linear-regression-ranker-reg500.ser.gz " +
                "--prefer-wh --max-length 30 --downweight-pro --inputfile %s ";

        ProcessBuilder pb = new ProcessBuilder(String.format(command, inputFile).split("\\s+"));
        pb.directory(new File(home));
        pb.redirectError(new File("heilman.log"));
        Process run = pb.start();
        List<String> out = new ArgLineCollector(run).collectOutput(false);
        run.waitFor();
        return processOutput(out);
    }

    private List<GeneratedQuestion> processOutput(List<String> out) {
        List<String> sentences= new ArrayList<>();
        List<String> questions = new ArrayList<>();
        List<Double> scores = new ArrayList<>();


        for (String s : out) {
            if (s.startsWith("SENT:")) {
                sentences.add(s.replace("SENT:","").trim());
            } else if (s.startsWith("SCORE:")) {
                scores.add(Double.valueOf(s.replace("SCORE:","").trim()));
            } else if (s.startsWith("PRED:")) {
                questions.add(s.replace("PRED:","").trim());
            }
        }

        List<GeneratedQuestion> generatedQuestions = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
            generatedQuestions.add(
                    new GeneratedQuestion("Heilman", sentences.get(i),questions.get(i),scores.get(i))
            );
        }



        // min normalization
//        Double max = generatedQuestions.stream().mapToDouble(GeneratedQuestion::getScore).map(Math::abs).max().getAsDouble();
//        Double min = generatedQuestions.stream().mapToDouble(GeneratedQuestion::getScore).map(Math::abs).min().getAsDouble();
//        Double maxlength = generatedQuestions.stream().mapToDouble(o-> o.getQuestion().length()).max().getAsDouble();
//        generatedQuestions.stream().forEach(output -> output.setScore((0.01+Math.abs(output.getScore())- min)/(max-min)*(output.getQuestion().length()/maxlength)));
//        //get top 30%
//        Collections.sort(generatedQuestions);
//        generatedQuestions = generatedQuestions.subList(0, (int) (generatedQuestions.size()*0.3));
        return generatedQuestions;
    }

    @Override
    public List<GeneratedQuestion> call() throws Exception {
        return getQuestions();
    }
}
