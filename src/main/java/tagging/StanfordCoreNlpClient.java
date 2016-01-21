package tagging;

import simplenlg.features.Tense;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

public class StanfordCoreNlpClient {
    private static NlpServer nlpServer;

    static {
        System.err.print("Searching for Stanford CoreNLP server running on localhost...");
        try {
            final Registry registry = LocateRegistry.getRegistry();
            nlpServer = (NlpServer) registry.lookup(StanfordCoreNlpServer.RMI_REGISTRY_KEY);
            System.err.println("FOUND");
        } catch (Exception e) {
            System.err.println("NOT FOUND");
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            System.err.println("Creating local fallback instance...");
            nlpServer = new StanfordCoreNlpServer();
            System.err.println("DONE");
        }
    }

    public static Sentence parseSentence(String sentence, boolean removePunctuation) {
        try {
            return nlpServer.parseSentence(sentence, removePunctuation);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static Tense calculateTense(String clause) {
        try {
            return nlpServer.calculateTense(clause);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, NamedEntity> findNamedEntities(String sentence) {
        try {
            return nlpServer.findNamedEntities(sentence);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
