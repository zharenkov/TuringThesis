package util;

import com.google.common.base.Joiner;
import edu.stanford.nlp.trees.Tree;

import java.util.List;

/**
 * Class to encapsulate information about a VP.
 */
public class Vp {
    private static final Joiner SPACE_JOINER = Joiner.on(' ');

    private final List<String> verbs;
    private final Tree highestVp;

    public Vp(List<String> verbs, Tree highestVp) {
        this.verbs = verbs;
        this.highestVp = highestVp;
    }

    /**
     * Returns the tree containing the current VP.
     *
     * @return the tree containing the current VP
     */
    public Tree getTree() {
        return highestVp;
    }

    /**
     * Returns whether the current VP contains one or more auxiliary verbs.
     *
     * @return {@code true} if the current VP has at least one auxiliary verb
     */
    public boolean hasAuxiliary() {
        return verbs.size() > 1;
    }

    /**
     * Returns the first auxiliary verb of the current VP if one exists. If the current VP does not have any auxiliary
     * verbs, an empty string is returned.
     *
     * @return the first auxiliary verb or an empty string if there are none
     */
    public String getFirstAuxiliary() {
        if (hasAuxiliary()) {
            return verbs.get(0);
        }
        return "";
    }

    /**
     * Returns the current VP excluding the first auxiliary verb if one exists.
     *
     * @return the current VP excluding the first auxiliary verb if one exists
     */
    public String getAllButFirstAuxiliary() {
        if (hasAuxiliary()) {
            return SPACE_JOINER.join(verbs.subList(1, verbs.size()));
        }
        return SPACE_JOINER.join(verbs);
    }

    @Override
    public String toString() {
        return verbs.toString();
    }
}
