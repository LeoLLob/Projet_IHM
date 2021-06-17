package App;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequeteTest {

    @Test
    public void getStartJSon() {
        JSONObject actual = Requete.getStartJSon("src/Selachii.json");
        assertEquals(163261, actual.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getInt("n"));
    }

    @Test
    public void getURL() {
        assertEquals("https://api.obis.org/v3/occurrence/grid/3?scientificname=Delphinidae", Requete.getURL("Delphinidae",3));
    }

    @Test
    public void getURLDate() {
        assertEquals("https://api.obis.org/v3/occurrence/grid/3?scientificname=Delphinidae&startdate=2012-12-12&enddate=2021-01-01",
                Requete.getURLDate("Delphinidae",3,"2012-12-12","2021-01-01"));
    }

    @Test
    public void getURLZone() {
        assertEquals("https://api.obis.org/v3/occurrence?geometry=spd", Requete.getURLZone("","spd"));
        assertEquals("https://api.obis.org/v3/occurrence?scientificname=Delphinidae&geometry=spd", Requete.getURLZone("Delphinidae","spd"));
    }

    @Test
    public void getURLNom() {
        assertEquals("https://api.obis.org/v3/taxon/complete/verbose/de", Requete.getURLNom("de"));
    }

    @Test
    public void readJsonFromUrl() {
        JSONObject actual = Requete.readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/3?scientificname=Selachii");
        assertEquals(163261, actual.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getInt("n"));
    }
}