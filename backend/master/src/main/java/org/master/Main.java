package org.master;

import org.controller.MasterController;

public class Main {
    public static void main(String[] args) {
        MasterController controller = new MasterController();
        controller.run();
        controller.cleanup();
    }
}