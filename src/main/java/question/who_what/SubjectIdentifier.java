package question.who_what;

import simplenlg.features.InterrogativeType;
import tagging.NamedEntity;
import tagging.ParsedSentence;

public class SubjectIdentifier {
    static InterrogativeType findInterrogativeTypeSubject(ParsedSentence sentence, String npString) {
        if (sentence.getNamedEntities().get(npString) == NamedEntity.PERSON) {
            return InterrogativeType.WHO_SUBJECT;
        }
        return InterrogativeType.WHAT_SUBJECT;
    }

    static InterrogativeType findInterrogativeTypeObject(ParsedSentence sentence, String npString) {
        if (sentence.getNamedEntities().get(npString) == NamedEntity.PERSON) {
            return InterrogativeType.WHO_OBJECT;
        }
        return InterrogativeType.WHAT_OBJECT;
    }
}
