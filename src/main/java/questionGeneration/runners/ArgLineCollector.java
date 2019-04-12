package questionGeneration.runners;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArgLineCollector {

    private static final long timeout = 60*3*1000;//3 minutes in ms

    private Process process;

    public ArgLineCollector(Process process) {
        this.process = process;
    }

    public List<String> collectOutput() throws IOException {
        String line;
        List<String> output = new ArrayList<String>();
//        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        System.out.println("POINT 1");
        long start = System.currentTimeMillis();
//        while((line = error.readLine()) != null && (System.currentTimeMillis() - start < timeout) ){
//            System.out.println("POINT 2");
//            System.out.println(line);
//        }
//        System.out.println("POINT 3");
//        error.close();

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        System.out.println("POINT 4");
        start = System.currentTimeMillis();
        while((line=input.readLine()) != null && (System.currentTimeMillis() - start < timeout)){
            System.out.println("POINT 5");
            System.out.println(line);
            output.add(line);
        }
        System.out.println("POINT 6");

        input.close();

        OutputStream outputStream = process.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println();
        printStream.flush();
        printStream.close();
        System.out.println("POINT 7");

        return output;
    }
}
