package App;

import GeoHash.GeoHash;

public class RechercheZone {
    private String geoHash;
    private String scientificName;
    private String order;
    private String superclass;
    private String recordedBy;
    private String species;

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