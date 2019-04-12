package questionGeneration.runners;


import questionGeneration.vo.Output;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NQGRunner implements ModelRunner {

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> ke) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(ke.apply(t), Boolean.TRUE) == null;
    }

    private String home;
    private String model;
    private String config;
    private String inputFileSrc;
    private String inputFilePar;


    public NQGRunner(String home, String model, String config, String inputFileSrc, String inputFilePar) {
        this.home = home;
        this.model = model;
        this.config = config;
        this.inputFileSrc = inputFileSrc;
        this.inputFilePar = inputFilePar;
    }

    @Override
    public List<Output> getQuestions() throws IOException, InterruptedException {
        String command = String.format(
                "th translate.lua -config %s -model model/%s -src %s ", config, model,  inputFileSrc);
        if (inputFilePar != null) {
            command = command + " -par " + inputFilePar;
        }


        ProcessBuilder pb = new ProcessBuilder(command.split("\\s+"));
        pb.directory(new File(home));
        pb.redirectError(new File("nqg.log"));
        Process run = pb.start();
        List<String> out = new ArgLineCollector(run).collectOutput();
        run.waitFor();
        return processOutputs(out);
    }

    private List<Output> processOutputs(List<String> out) {
        List<String> sentences= new ArrayList<>();
        List<String> questions = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        List<String> paragraphs = new ArrayList<>();

        for (String s : out) {
            if (s.startsWith("SENT")) {
                sentences.add(s.substring(s.indexOf(":")+1));
            } else if (s.startsWith("PRED SCORE")) {
                scores.add(Double.valueOf(s.substring((s.indexOf(":")+1)).trim()));
            } else if (s.startsWith("PRED")) {
                questions.add(s.substring(s.indexOf(":")+1));
            } else if (s.startsWith("PARA")) {
                paragraphs.add(s.substring(s.indexOf(":")+1));
            }
        }

//        questions = questions
//                .stream()
//                .map( q -> q.split("\\s+"))
//                .map(arr -> cleanDuplicates(arr))
//                .collect(Collectors.toList());

        List<Output> outputs = new ArrayList<>();
        for (int i=0; i<sentences.size();i++) {
            outputs.add(new Output("NQG", sentences.get(i),questions.get(i),scores.get(i), paragraphs.get(i)));
        }
        //deduplicate
        outputs = outputs.stream().filter(distinctByKey(n->n.getQuestion())).collect(Collectors.toList());
        //sort

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

    private String cleanDuplicates(String[] words) {
        String res = "";
        List<String> list = new ArrayList<>();
        list.add(words[0]);
        for (int i =1; i<words.length;i++) {
            if (!list.get(list.size()-1).equals(words[i])) {
                list.add(words[i]);
            }
        }
        return list.stream().collect(Collectors.joining(" "));
    }
}
