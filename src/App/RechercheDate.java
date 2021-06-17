package App;


import javafx.geometry.Point2D;

import java.util.ArrayList;

public class RechercheDate extends RechercheNom{
    private String dateDebut;
    private String dateFin;

    public RechercheDate(String scientificName, ArrayList<Point2D> coord, int occurence, int precision, String dateDebut, String dateFin){
        super(scientificName, coord, occurence, precision);
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
}