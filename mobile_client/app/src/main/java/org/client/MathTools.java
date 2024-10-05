package org.client;

public class MathTools {

    public static double roundOff(double x, int position) {
        double a = x;
        double temp = Math.pow(10.0, position);

        a *= temp;
        a = Math.round(a);

        return (a / (double)temp);
    }


    public static double percent(double x, double y) {
        try {
            return 100 * x / y;
        } catch (Exception ex) {
            return -1;
        }
    }
}

