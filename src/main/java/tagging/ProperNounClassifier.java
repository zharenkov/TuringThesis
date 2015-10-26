package tagging;

import com.google.common.base.Joiner;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.IOException;

public class ProperNounClassifier {
    private static final String serializedClassifier = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";

    public static String classifyProperNoun(String properNoun) throws IOException, ClassNotFoundException {
        final AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
        return classifier.classifyToString(properNoun);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println(classifyProperNoun(Joiner.on(' ').join(args)));
    }
}
