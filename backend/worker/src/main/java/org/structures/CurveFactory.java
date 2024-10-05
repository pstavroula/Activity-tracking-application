package org.structures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CurveFactory {
    public static Curve build(String filename) {
        Curve curve = new Curve();

        try (FileReader fr = new FileReader(filename); BufferedReader br = new BufferedReader(fr); Stream<String> lines = br.lines();) {
            List<String> collect = lines.collect(Collectors.toList());

            for (int i = 2; i < collect.size()-4; i = i + 4) {
                String l1 = collect.get(i);
                String l2 = collect.get(i + 1);
                String l3 = collect.get(i + 2);

                String [] l1_tokens =l1.split("\"");
                double latitude = Double.parseDouble(l1_tokens[1].trim());
                double longitude = Double.parseDouble(l1_tokens[3].trim());
                double elevation = Double.parseDouble(l2.replaceAll("<ele>","").replaceAll("</ele>", "").trim());
                String time = l3.replaceAll("<time>","").replaceAll("</time>", "").trim();
                curve.getPoints().add(new Wpt(latitude, longitude, elevation, time));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return curve;
    }

    public static Map<String, Curve> build(String prefix, String[] files) {
        Map<String, Curve> curves = new TreeMap();

        for (String s : files) {
            curves.put(s, build(prefix + s));
        }
        return curves;
    }
}
