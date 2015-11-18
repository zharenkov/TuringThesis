package tagging;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

public class StanfordParser {
    private final static String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(),
            "invertible=true");
    private final LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);

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

    public Sentence parseSentence(String sentence) {
        final Tree posTree = getPosTree(sentence);
        return new Sentence(posTree, getDependencies(posTree));
    }
}
