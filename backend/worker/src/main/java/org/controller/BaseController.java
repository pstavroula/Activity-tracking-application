package org.controller;

import org.threads.ControllerThread;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseController {
    private String prefix = "src/main/resources/";
    protected Properties properties = new Properties();

    public BaseController() {
        try (InputStream input = new FileInputStream(prefix + "config.properties")) {
            properties.load(input);

            properties.list(System.out);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void startThread(Runnable runnable) {
        new ControllerThread(runnable);
    }
}
