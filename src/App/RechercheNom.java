package App;


import javafx.geometry.Point2D;

import java.util.ArrayList;

public class RechercheNom {
    private String scientificName;
    private ArrayList<Point2D> coord;
    private int occurence;
    private int precision;

    public RechercheNom(String scientificName, ArrayList<Point2D> coord, int occurence, int precision){
        this.scientificName = scientificName;
        this.coord = coord;
        this.occurence = occurence;
        this.precision = precision;
    }

    public String getScientificName() {
        return scientificName;
    }

    public ArrayList<Point2D> getCoord() {
        return coord;
    }

    public int getOccurence() {
        return occurence;
    }

    public int getPrecision() {
        return precision;
    }
}