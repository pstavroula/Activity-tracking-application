package org.structures;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CurveFactory {
    public static Curve build(Context context, String filename) {
        Curve curve = new Curve();

        filename = filename.replace(".gpx", "");

        InputStream ins = null;
        try {
            ins = context.getResources().openRawResource(context.getResources().getIdentifier(filename, "raw", context.getPackageName()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        try (Reader fr = new InputStreamReader(ins); BufferedReader br = new BufferedReader(fr); Stream<String> lines = br.lines();) {
            List<String> collect = lines.collect(Collectors.toList());

            //<wpt lat="37.95001155239993" lon="23.69503479744284">
            //    <ele>12.08</ele>
            //    <time>2023-03-19T17:36:01Z</time>
            //</wpt>

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

    public static Map<String, Curve> build(Context context, String prefix, String[] files) {
        Map<String, Curve> curves = new TreeMap();

        for (String s : files) {
            curves.put(s, build(context, prefix + s));
        }
        return curves;
    }
}
