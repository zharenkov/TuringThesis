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

    public List<String> collectOutput(boolean error) throws IOException {
        String line;
        List<String> output = new ArrayList<String>();
        BufferedReader input = new BufferedReader(
                new InputStreamReader(error ? process.getErrorStream() : process.getInputStream()));
        long start = System.currentTimeMillis();
        while((line=input.readLine()) != null && (System.currentTimeMillis() - start < timeout)){
            //System.out.println(line);
            output.add(line);
        }
        input.close();
        OutputStream outputStream = process.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println();
        printStream.flush();
        printStream.close();
        return output;
    }
}
