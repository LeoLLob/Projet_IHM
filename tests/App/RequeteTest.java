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
    public void readJsonFromUrl() {
        JSONObject actual = Requete.readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/3?scientificname=Selachii");
        assertEquals(163261, actual.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getInt("n"));
    }

    @Test
    public void creerRechercheNom(){
        Requete.creerRechercheNom("Selachii", 3);
        assertEquals(163261, Requete.getListeRechercheNom().get(0).getOccurence());
        assertEquals(163261, Requete.getMaxOccurence());
    }

    @Test
    public void creerRechercheDate(){
        Requete.creerRechercheDate("Selachii", 3, "2013-11-03", "2021-05-03");
        assertEquals(53787, Requete.getListeRechercheDate().get(0).getOccurence());
        assertEquals(53787, Requete.getMaxOccurence());
    }

    @Test
    public void creerRechercheZone(){
        Requete.creerRechercheZone("Selachii", "spd");
        assertEquals("Oxynotus centrina", Requete.getListeRechercheZone().get(0).getScientificName());
        assertEquals("bauchot", Requete.getListeRechercheZone().get(0).getRecordedBy());
        assertEquals("Oxynotus centrina", Requete.getListeRechercheZone().get(0).getSpecies());
        assertEquals("spd", Requete.getListeRechercheZone().get(0).getGeoHash());

        Requete.creerRechercheZone("", "spd");
        assertEquals("Prorocentrum", Requete.getListeRechercheZone().get(0).getScientificName());
        assertEquals("spd", Requete.getListeRechercheZone().get(0).getGeoHash());
    }

    @Test
    public void listeNom(){
        Requete.listeNom("Selach");
        assertEquals("Selachii", Requete.getListeNom().get(0));
    }






}