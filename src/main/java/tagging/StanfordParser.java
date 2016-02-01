package tagging;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Triple;
import simplenlg.features.Tense;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StanfordParser {
    private final String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(),
            "invertible=true");
    private final LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);
    private final String serializedClassifier = "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf" +
            ".ser.gz";
    private final AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(
            serializedClassifier);

    public Sentence parseSentence(String sentence, boolean removePunctuation) {
        if (removePunctuation) {
            sentence = cleanSentence(sentence);
        }

        final Tree posTree = getPosTree(sentence);
        return new Sentence(posTree, getDependencies(posTree), findNamedEntities(sentence));
    }

    public Tense calculateTense(String clause) {
        final Tree posTree = getPosTree(clause);
        final Tree word = posTree.getLeaves().get(0);
        final String pos = word.parent(posTree).label().value().toLowerCase();
        if (pos.equals("md")) {
            return Tense.FUTURE;
        }
        if (pos.equals("vbd") || pos.equals("vbn")) {
            return Tense.PAST;
        }
        return Tense.PRESENT;
    }

    public Map<String, NamedEntity> findNamedEntities(String sentence) {
        final Map<String, NamedEntity> namedEntities = new HashMap<>();
        final List<Triple<String, Integer, Integer>> nerSubstrings = findNerSubstrings(sentence);
        for (final Triple<String, Integer, Integer> substring : nerSubstrings) {
            namedEntities.put(sentence.substring(substring.second(), substring.third()),
                    NamedEntity.getNamedEntity(substring.first()));
        }
        return namedEntities;
    }

    private List<Triple<String, Integer, Integer>> findNerSubstrings(String sentence) {
        return classifier.classifyToCharacterOffsets(sentence);
    }

    private String cleanSentence(String sentence) {
        return sentence.replaceAll("\\p{Punct}", "").replaceAll("[ ]+", " ");
    }

    private Tree getPosTree(String sentence) {
        final Tokenizer<CoreLabel> tokenizer = tokenizerFactory.getTokenizer(new StringReader(sentence));
        final List<CoreLabel> tokens = tokenizer.tokenize();
        return parser.apply(tokens);
    }

    private Collection<TypedDependency> getDependencies(Tree sentenceParseTree) {
        final TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        final GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        final GrammaticalStructure gs = gsf.newGrammaticalStructure(sentenceParseTree);
        return gs.typedDependenciesCollapsed();
    }
}
