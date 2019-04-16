package questionGeneration.runners;


import questionGeneration.vo.GeneratedQuestion;

import java.io.*;
import java.util.ArrayList;
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
    public List<GeneratedQuestion> getQuestions() throws IOException, InterruptedException {
        String command = String.format(
                "th translate.lua -config %s -model model/%s -src %s ", config, model,  inputFileSrc);
        if (inputFilePar != null) {
            command = command + " -par " + inputFilePar;
        }


        ProcessBuilder pb = new ProcessBuilder(command.split("\\s+"));
        pb.directory(new File(home));
        pb.redirectError(new File("nqg.log"));
        Process run = pb.start();
        List<String> out = new ArgLineCollector(run).collectOutput(false);
        run.waitFor();
        return processOutputs(out);
    }

    private List<GeneratedQuestion> processOutputs(List<String> out) {
        List<String> sentences= new ArrayList<>();
        List<String> questions = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
//        List<String> paragraphs = new ArrayList<>();

        for (String s : out) {
            if (s.startsWith("SENT")) {
                sentences.add(s.substring(s.indexOf(":")+1));
            } else if (s.startsWith("PRED SCORE")) {
                scores.add(Double.valueOf(s.substring((s.indexOf(":")+1)).trim()));
            } else if (s.startsWith("PRED")) {
                questions.add(s.substring(s.indexOf(":") + 1));
//            } else if (s.startsWith("PARA")) {
//                paragraphs.add(s.substring(s.indexOf(":")+1));
//            }
            }
        }

//        questions = questions
//                .stream()
//                .map( q -> q.split("\\s+"))
//                .map(arr -> cleanDuplicates(arr))
//                .collect(Collectors.toList());

        List<GeneratedQuestion> generatedQuestions = new ArrayList<>();
        for (int i=0; i<sentences.size();i++) {
            generatedQuestions.add(new GeneratedQuestion("NQG", sentences.get(i),questions.get(i),scores.get(i)));
        }
        //deduplicate
        generatedQuestions = generatedQuestions.stream().filter(distinctByKey(n->n.getQuestion())).collect(Collectors.toList());
        //sort

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

    private String cleanDuplicates(String[] words) {
        List<String> list = new ArrayList<>();
        list.add(words[0]);
        for (int i =1; i<words.length;i++) {
            if (!list.get(list.size()-1).equals(words[i])) {
                list.add(words[i]);
            }
        }
        return list.stream().collect(Collectors.joining(" "));
    }

    @Override
    public List<GeneratedQuestion> call() throws Exception {
        return getQuestions();
    }
}
