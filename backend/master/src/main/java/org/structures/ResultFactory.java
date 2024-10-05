package org.structures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultFactory {
    static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }

    public static Result build(Curve curve) {
        double totalDistance = 0.0;
        double totalElevation = 0.0;
        double totalTime = 0.0;

        int n = curve.getPoints().size();

        for (int i=0;i<n-1;i++) {
            Wpt a = curve.getPoints().get(i);
            Wpt b = curve.getPoints().get(i+1);

            totalDistance += Math.abs(haversine(a.getLatitude(), a.getLon(), b.getLatitude(), b.getLon()));

            if (b.getEle() > a.getEle()) {
                totalElevation += Math.abs(b.getEle() - a.getEle());
            }

            OffsetDateTime odt1 = OffsetDateTime.parse(a.getTime());
            OffsetDateTime odt2 = OffsetDateTime.parse(b.getTime());

            totalTime += odt2.toEpochSecond() - odt1.toEpochSecond();
        }

        return new Result(totalDistance, totalElevation, totalTime);
    }
}
