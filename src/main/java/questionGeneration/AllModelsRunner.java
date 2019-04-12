package questionGeneration;

import questionGeneration.runners.HeilmanRunner;
import questionGeneration.runners.NQGRunner;

import java.io.*;
import java.util.*;

public class AllModelsRunner {
    private static Map<String, String> phraseTable = new HashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException {
//        String inputFile = args[0];
//        String inputFilePar = null;
//        if (args.length > 1) {
//            inputFilePar = args[1];
//        }
//        Properties properties = new Properties();
//        properties.load(new FileReader("runner.properties"));
//        String phraseTablePath = properties.getProperty("phraseTable");
//        NQGRunner nqgRunner = new NQGRunner(
//                properties.getProperty("nqg.home"),
//                properties.getProperty("nqg.model"),
//                properties.getProperty("nqg.config"),
//                inputFile,
//                inputFilePar
//                );
//        HeilmanRunner heilmanRunner = new HeilmanRunner(
//                properties.getProperty("heilman.home"),
//                inputFile
//        );
//        if (phraseTablePath != null) {
//            Scanner phraseTableScanner = new Scanner(new File(properties.getProperty("phraseTable")));
//            while (phraseTableScanner.hasNext()) {
//                String replacement = phraseTableScanner.nextLine();
//                String[] parts = replacement.split("\\|\\|\\|");
//                phraseTable.put(parts[0], parts[1]);
//            }
//        }
//
//        List<Output> heilmanOutputs = heilmanRunner.getQuestions();
//        List<Output> nqgOutputs = nqgRunner.getQuestions();
////        List<DeepMiptOutput> deepMiptOutputs = deepMiptRunner.getQuestions();
//        List<Output> allout = new ArrayList<>();
//
//
//        allout.addAll(nqgOutputs);
//        allout.addAll(heilmanOutputs);
//        Collections.sort(allout);
//        allout.forEach(output -> {
//            String q = output.getQuestion();
//
//        });
////        allout.addAll(deepMiptOutputs.subList(0, deepMiptOutputs.size()));//(int) (deepMiptOutputs.size()*0.4)));
//        try (PrintWriter writer = new PrintWriter(new File("result.csv"))) {
//
//            StringBuilder sb = new StringBuilder();
//            sb.append("model,")
//                    .append("question,")
//                    .append("sentence,")
//                    .append("score")
//                    .append(",")
//                    .append("paragraph")
//                    .append('\n');
//
//
//            allout.forEach(o-> {
//                sb.append("\""+ o.getModel() +"\"");
//                sb.append(",");
//                sb.append("\"" + replaceWords(o.getQuestion().replaceAll("\"",""))+ "\"");
//                sb.append(",");
//                sb.append("\"" + o.getSentence().replaceAll("\"","") + "\"");
//                sb.append(",");
//                sb.append(o.getScore());
//                sb.append(",");
//                sb.append("\""  +o.getParagraph().replaceAll("\"","") + "\"");
//                sb.append("\n");
//            });
//
//            writer.write(sb.toString());
//
//        } catch (FileNotFoundException e) {
//            System.out.println(e.getMessage());
//        }

    }

    private static String replaceWords (String input) {
        for (Map.Entry<String, String> entry : phraseTable.entrySet()){
            input = input.replaceAll(entry.getKey(), entry.getValue());
        }
        return input;
    }
}
