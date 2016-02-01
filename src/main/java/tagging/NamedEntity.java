package tagging;

public enum NamedEntity {
    PERSON, LOCATION, DATE;

    public static NamedEntity getNamedEntity(String label) {
        switch (label.toLowerCase()) {
            case "person": return PERSON;
            case "location": return LOCATION;
            case "date":
                return DATE;
            default: return PERSON;
        }
    }
}
