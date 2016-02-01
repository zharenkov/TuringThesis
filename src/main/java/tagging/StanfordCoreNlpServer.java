package tagging;

import simplenlg.features.Tense;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class StanfordCoreNlpServer implements NlpServer {
    public static String RMI_REGISTRY_KEY = "NLP";

    private final StanfordParser parser = new StanfordParser();

    public static void main(String args[]) {
        try {
            StanfordCoreNlpServer obj = new StanfordCoreNlpServer();
            NlpServer stub = (NlpServer) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(RMI_REGISTRY_KEY, stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public ParsedSentence parseSentence(String sentence, boolean removePunctuation) throws RemoteException {
        return parser.parseSentence(sentence, removePunctuation);
    }

    @Override
    public Tense calculateTense(String clause) throws RemoteException {
        return parser.calculateTense(clause);
    }

    @Override
    public Map<String, NamedEntity> findNamedEntities(String sentence) throws RemoteException {
        return parser.findNamedEntities(sentence);
    }
}
