package App;

import GeoHash.*;
import org.json.*;


import javafx.geometry.Point2D;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class Requete {
    private static ArrayList<RechercheNom> listeRechercheNom = new ArrayList<RechercheNom>();
    private static ArrayList<RechercheDate> listeRechercheDate = new ArrayList<RechercheDate>();
    private static ArrayList<RechercheZone> listeRechercheZone = new ArrayList<RechercheZone>();;
    private static ArrayList<String> listeNom = new ArrayList<String>();
    private static int maxOccurence;
    private static int minOccurence;

    public Requete(){
        this.listeRechercheNom = new ArrayList<>();
        this.listeRechercheDate = new ArrayList<>();
        this.listeRechercheZone = new ArrayList<>();
        this.listeNom = new ArrayList<>();

    }

    public static ArrayList<RechercheDate> getListeRechercheDate() {
        return listeRechercheDate;
    }

    public static ArrayList<RechercheNom> getListeRechercheNom() {
        return listeRechercheNom;
    }

    public static ArrayList<RechercheZone> getListeRechercheZone() {
        return listeRechercheZone;
    }

    public static ArrayList<String> getListeNom() {
        return listeNom;
    }

    public static int getMaxOccurence() {
        return maxOccurence;
    }

    public static int getMinOccurence() {
        return minOccurence;
    }

    private static String readAll(Reader rd) throws IOException{
        StringBuilder sb = new StringBuilder();
        int cp;
        while((cp = rd.read()) != -1){
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject getStartJSon(String path){
        try (Reader reader = new FileReader(path)){
            BufferedReader rd = new BufferedReader(reader);
            String jsonText = readAll(rd);
            JSONObject jsonRoot = new JSONObject(jsonText);
            return jsonRoot;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void startApp(){
        JSONObject JsonRoot = getStartJSon("src/Selachii.json");
        maxOccurence = Integer.MIN_VALUE;
        minOccurence = Integer.MAX_VALUE;

        JSONArray resultatRecherche = JsonRoot.getJSONArray("features");
        for(Object object : resultatRecherche ) {
            JSONObject recherche = (JSONObject) object;
            ArrayList<Point2D> coordonnees = new ArrayList<>();
            JSONArray fauxJsonCoords = recherche.getJSONObject("geometry").getJSONArray("coordinates");
            for(Object fauxObjectPoint2D : fauxJsonCoords){
                JSONArray ArrayPoint2D = (JSONArray) fauxObjectPoint2D;
                for (Object objectPoint2D : ArrayPoint2D){
                    JSONArray jsonPoint2D = (JSONArray) objectPoint2D;
                    double x = jsonPoint2D.getDouble(0);
                    double y = jsonPoint2D.getDouble(1);

                    Point2D point2D = new Point2D(x,y);
                    coordonnees.add(point2D);
                }
            }


            Object objectOccurence = recherche.getJSONObject("properties").getInt("n");
            int occurence = (int) objectOccurence;

            if (occurence > maxOccurence) maxOccurence = occurence;
            if (occurence < minOccurence) minOccurence = occurence;

            RechercheNom rechercheNom = new RechercheNom("Selachii", coordonnees, occurence, 3);
            listeRechercheNom.add(rechercheNom);
        }
    }

    public static String getURL(String nomScientifique, int precision){
        String url = "https://api.obis.org/v3/occurrence/grid/" + precision +"?scientificname=" + nomScientifique;
        return url;
    }

    public static String getURLDate(String nomScientifique, int precision, String dateDebut, String dateFin){
        String url = "https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nomScientifique +
                "&startdate=" + dateDebut + "&enddate=" + dateFin;
        return url;
    }

    public static String getURLZone(String nomScientifique, String geohash){
        if(nomScientifique.isEmpty()) {
            String url = "https://api.obis.org/v3/occurrence?geometry=" +geohash;
            return url;
        }else{
            String url = "https://api.obis.org/v3/occurrence?scientificname=" + nomScientifique + "&geometry=" +geohash;
            return url;
        }
    }

    public static String getURLNom(String chaine){

        String url = "https://api.obis.org/v3/taxon/complete/verbose/"  + chaine;
        return url;
    }

    public static JSONObject readJsonFromUrl(String url) {
        String json = "";
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            json = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(json);
    }

    public static JSONArray readJsonFromUrlListeNom(String url) {
        String json = "";
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            json = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray(json);
    }


    public static void creerRechercheNom(String scientificName, int precision){
        maxOccurence = Integer.MIN_VALUE;
        minOccurence = Integer.MAX_VALUE;
        
        listeRechercheNom.clear();
        String url = getURL(scientificName, precision);
        System.out.println(url);
        JSONObject JsonRoot = readJsonFromUrl(url);
        JSONArray resultatRecherche = JsonRoot.getJSONArray("features");
        for(Object object : resultatRecherche ) {
            JSONObject recherche = (JSONObject) object;
            ArrayList<Point2D> coordonnees = new ArrayList<>();
            JSONArray fauxJsonCoords = recherche.getJSONObject("geometry").getJSONArray("coordinates");
            for(Object fauxObjectPoint2D : fauxJsonCoords){
                JSONArray ArrayPoint2D = (JSONArray) fauxObjectPoint2D;
                for (Object objectPoint2D : ArrayPoint2D){
                    JSONArray jsonPoint2D = (JSONArray) objectPoint2D;
                    double x = jsonPoint2D.getDouble(0);
                    double y = jsonPoint2D.getDouble(1);

                    Point2D point2D = new Point2D(x,y);
                    coordonnees.add(point2D);
                }
            }


            Object objectOccurence = recherche.getJSONObject("properties").getInt("n");
            int occurence = (int) objectOccurence;

            if (occurence > maxOccurence) maxOccurence = occurence;
            if (occurence < minOccurence) minOccurence = occurence;

            RechercheNom rechercheNom = new RechercheNom(scientificName, coordonnees, occurence, precision);
            listeRechercheNom.add(rechercheNom);
        }
    }

    // nombre de signalements par zone par intervalle de temps
    public static void creerRechercheDate(String scientificName, int precision, String dateDebut, String dateFin){
        maxOccurence = Integer.MIN_VALUE;
        minOccurence = Integer.MAX_VALUE;

        listeRechercheDate.clear();
        String url = getURLDate(scientificName, precision, dateDebut, dateFin);
        JSONObject JsonRoot = readJsonFromUrl(url);

        JSONArray resultatRecherche = JsonRoot.getJSONArray("features");
        for(Object object : resultatRecherche ) {
            JSONObject recherche = (JSONObject) object;
            ArrayList<Point2D> coordonnees = new ArrayList<>();
            JSONArray fauxJsonCoords = recherche.getJSONObject("geometry").getJSONArray("coordinates");
            for(Object fauxObjectPoint2D : fauxJsonCoords){
                JSONArray ArrayPoint2D = (JSONArray) fauxObjectPoint2D;
                for (Object objectPoint2D : ArrayPoint2D){
                    JSONArray jsonPoint2D = (JSONArray) objectPoint2D;
                    double x = jsonPoint2D.getDouble(0);
                    double y = jsonPoint2D.getDouble(1);

                    Point2D point2D = new Point2D(x,y);
                    coordonnees.add(point2D);
                }
            }


            Object objectOccurence = recherche.getJSONObject("properties").getInt("n");
            int occurence = (int) objectOccurence;

            if (occurence > maxOccurence) maxOccurence = occurence;
            if (occurence < minOccurence) minOccurence = occurence;

            RechercheDate rechercheDate = new RechercheDate(scientificName, coordonnees, occurence, precision, dateDebut, dateFin);
            listeRechercheDate.add(rechercheDate);
        }
    }

    public static void creerRechercheZone(String scientificName, String geoHash){
        listeRechercheZone.clear();
        String url = getURLZone(scientificName, geoHash);
        JSONObject JsonRoot = readJsonFromUrl(url);
        JSONArray resultatRecherche = JsonRoot.getJSONArray("results");
        for(Object object : resultatRecherche ) {
            JSONObject recherche = (JSONObject) object;
            if(!recherche.isNull("scientificName")) {
                scientificName = recherche.getString("scientificName");
            }
            String order = "";
            if(!recherche.isNull("order")) {
                order = recherche.getString("order");
            }
            String superclass = "";
            if(!recherche.isNull("superclass")) {
                superclass = recherche.getString("superclass");
            }
            String recordedBy = "";
            if(!recherche.isNull("recordedBy")) {
                recordedBy = recherche.getString("recordedBy");
            }
            String species = "";
            if(!recherche.isNull("species")) {
                species = recherche.getString("species");
            }

            RechercheZone rechercheZone = new RechercheZone(geoHash, scientificName, order, superclass, recordedBy, species);
            listeRechercheZone.add(rechercheZone);
        }
    }

    public static void listeNom(String chaine){
        listeNom.clear();
        String url = getURLNom(chaine);
        JSONArray JsonRoot = readJsonFromUrlListeNom(url);
        for(Object Object : JsonRoot) {
            JSONObject recherche = (JSONObject) Object;
            String nom = recherche.getString("scientificName");
            listeNom.add(nom);
        }
    }
}