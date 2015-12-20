package question.who_what;

import simplenlg.features.*;
import tagging.*;

public class SubjectIdentifier {
    static InterrogativeType findInterrogativeTypeSubject(Sentence sentence, String npString) {
        if (sentence.getNamedEntities().get(npString) == NamedEntity.PERSON) {
            return InterrogativeType.WHO_SUBJECT;
        }
        return InterrogativeType.WHAT_SUBJECT;
    }

    static InterrogativeType findInterrogativeTypeObject(Sentence sentence, String npString) {
        if (sentence.getNamedEntities().get(npString) == NamedEntity.PERSON) {
            return InterrogativeType.WHO_OBJECT;
        }
        return InterrogativeType.WHAT_OBJECT;
    }
}
