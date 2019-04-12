package questionGeneration.runners;


import questionGeneration.vo.Output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeilmanRunner implements ModelRunner {

    private String home;
    private String inputFile;

    public HeilmanRunner(String home, String inputFile) {
        this.home = home;
        this.inputFile = inputFile;
    }

    @Override
    public List<Output> getQuestions() throws IOException, InterruptedException {
        //String home = "/home/opc/QuestionGeneration/";
        //String home = "/Volumes/MacintoshHDD/Downloads/Safari/QuestionGeneration/";
        String command = "java -Xmx1200m -cp question-generation.jar " +
                "edu/cmu/ark/QuestionAsker " +
                "--verbose --model models/linear-regression-ranker-reg500.ser.gz " +
                "--prefer-wh --max-length 30 --downweight-pro --inputfile %s ";

        ProcessBuilder pb = new ProcessBuilder(String.format(command, inputFile).split("\\s+"));
        pb.directory(new File(home));
        pb.redirectError(new File("heilman.log"));
        Process run = pb.start();
        List<String> out = new ArgLineCollector(run).collectOutput();
        run.waitFor();
        return processOutput(out);
    }

    private List<Output> processOutput(List<String> out) {
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

        List<Output> outputs = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
            outputs.add(
                    new Output("Heilman", sentences.get(i),questions.get(i),scores.get(i), "-")
            );
        }



        // min normalization
        Double max = outputs.stream().mapToDouble(Output::getScore).map(Math::abs).max().getAsDouble();
        Double min = outputs.stream().mapToDouble(Output::getScore).map(Math::abs).min().getAsDouble();
        Double maxlength = outputs.stream().mapToDouble(o-> o.getQuestion().length()).max().getAsDouble();
        outputs.stream().forEach(output -> output.setScore((0.01+Math.abs(output.getScore())- min)/(max-min)*(output.getQuestion().length()/maxlength)));
        //get top 30%
        Collections.sort(outputs);
        outputs = outputs.subList(0, (int) (outputs.size()*0.3));
        return outputs;
    }

}
