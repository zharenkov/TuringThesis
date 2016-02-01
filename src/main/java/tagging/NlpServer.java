package tagging;

import simplenlg.features.Tense;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface NlpServer extends Remote {
    ParsedSentence parseSentence(String sentence, boolean removePunctuation) throws RemoteException;

    Tense calculateTense(String clause) throws RemoteException;

    Map<String, NamedEntity> findNamedEntities(String sentence) throws RemoteException;
}
