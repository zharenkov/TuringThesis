package tagging;

public enum NamedEntity {
    PERSON, LOCATION;

    public static NamedEntity getNamedEntity(String label) {
        switch (label.toLowerCase()) {
            case "person": return PERSON;
            case "location": return LOCATION;
            default: return PERSON;
        }
    }
}
