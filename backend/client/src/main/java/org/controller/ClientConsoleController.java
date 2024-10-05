package org.controller;

import org.structures.Curve;

import java.util.Map;
import java.util.Scanner;

//
// Controller for console
//
public class ClientConsoleController extends BaseController {
    private final Map<String, Curve> curves;
    private Scanner scanner = new Scanner(System.in);

    public ClientConsoleController(Map<String, Curve> curves) {
        this.curves = curves;

    }

    public void run() {
        String [] files = curves.keySet().toArray(new String[curves.keySet().size()]);
        int index = 0;

        while (true) {
            System.out.print("Which file do you want to send (index=0, press enter for: "+ files[index] + ") ?");
            String file;

            if (super.properties.getProperty("AUTOMODE").equals("true")) {
                file = "";
            } else {
                file = scanner.nextLine();
            }

            if (file.toLowerCase().equals("exit") || file.toLowerCase().equals("quit")) {
                break;
            }

            if (file.trim().isEmpty() && index < files.length) {
                file = files[index++];
                if (index >= files.length) {
                    index = 0;
                }
                System.out.println("File set to: " + file);
            }

            if (!curves.keySet().contains(file)) {
                System.out.println("File not found: " + file);
                continue;
            }

            Curve selectedCurve = curves.get(file);

            AndroidController controller = new AndroidController(selectedCurve, properties);

            controller.process();

            if (super.properties.getProperty("AUTOMODE").equals("true") && index == 0) {
                break;
            }
        }
    }
}
