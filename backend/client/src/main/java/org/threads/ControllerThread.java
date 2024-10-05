package org.threads;

public class ControllerThread extends Thread {
    public ControllerThread(Runnable runnable) {
        super(runnable);
        this.start();
    }

    public void await() {
        try {
            System.out.println("Waiting for thread to exit ... ");
            this.join();
        } catch (InterruptedException e) {

        }
    }
}
