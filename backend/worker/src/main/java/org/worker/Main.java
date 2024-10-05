package org.worker;

import org.controller.WorkerController;

public class Main {
    public static void main(String[] args) {
        System.out.println("Worker starting ... ");

        WorkerController controller = new WorkerController();

        controller.run();
    }
}