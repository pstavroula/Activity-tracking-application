package org.structures;

import java.io.Serializable;
import java.util.ArrayList;

public class Curve implements Serializable {
    private ArrayList<Wpt> points = new ArrayList<>();

    public Curve() {
    }

    public ArrayList<Wpt> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Wpt> points) {
        this.points = points;
    }
}
