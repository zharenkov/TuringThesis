package data;

/**
 * Wrapper class for {@link String}. Equality checking for this class ignores all non-word character.
 */
public class Text {
    protected final String string;
    protected final String canonicalString;

    public Text(String string) {
        this.string = string;
        this.canonicalString = string.toLowerCase().replaceAll("\\W", "");
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Text otherText = (Text) o;
        return canonicalString.equals(otherText.canonicalString);
    }

    @Override
    public int hashCode() {
        return canonicalString.hashCode();
    }

    @Override
    public String toString() {
        return string;
    }
}
