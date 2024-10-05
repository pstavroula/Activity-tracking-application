package org.client;

import android.content.Context;

import org.structures.Curve;
import org.structures.CurveFactory;

import java.util.Map;

public class Main  {
    public static final String prefix = "";
    public static final String [] files = new String[] { "route1.gpx", "route2.gpx", "route3.gpx", "route4.gpx","route5.gpx", "route6.gpx", "segment1.gpx", "segment2.gpx" };
    public static Map<String, Curve> curves;

    public static void main(Context context) {
        System.out.println("Client starting ... ");
        System.out.println("Client loading files to memory ... ");
        curves = CurveFactory.build(context, prefix, files);

        for (Map.Entry<String,Curve> entry : curves.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Points = " + entry.getValue().getPoints().size());
        }
//
//        ClientConsoleController controller = new ClientConsoleController(curves);
//
//        controller.run();
    }
}