package org.client;

import org.controller.ClientConsoleController;
import org.structures.Curve;
import org.structures.CurveFactory;

import java.util.*;

public class Main  {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String prefix = "src/main/resources/";
        String [] files = new String[] { "route1.gpx", "route2.gpx", "route3.gpx", "route4.gpx","route5.gpx", "route6.gpx", "segment1.gpx", "segment2.gpx" };
        int index = 0;

        System.out.println("Client starting ... ");
        System.out.println("Client loading files to memory ... ");
        Map<String, Curve> curves = CurveFactory.build(prefix, files);

        for (Map.Entry<String,Curve> entry : curves.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Points = " + entry.getValue().getPoints().size());
        }

        ClientConsoleController controller = new ClientConsoleController(curves);

        controller.run();



    }
}