package util;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ReplaceUtils {

    public static String replaceWords(String input, Map<String, String> phraseTable) {
        int size = phraseTable.size();
        input = StringUtils.replaceEach(input,
                phraseTable.keySet().toArray(new String[size]),
                phraseTable.values().toArray(new String[size]));
        return input;
    }
}
