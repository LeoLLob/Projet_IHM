package App;

public class RechercheZone {
    private final String geoHash;
    private final String scientificName;
    private final String order;
    private final String superclass;
    private final String recordedBy;
    private final String species;

    public RechercheZone(String geoHash, String scientificName, String order, String superclass, String recordedBy, String species){
        this.geoHash = geoHash;
        this.scientificName = scientificName;
        this.order = order;
        this.superclass = superclass;
        this.recordedBy = recordedBy;
        this.species = species;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getOrder() {
        return order;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public String getSpecies() {
        return species;
    }

    public String getSuperclass() {
        return superclass;
    }
}