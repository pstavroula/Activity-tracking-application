package org.controller;

import android.content.Context;

import org.threads.ControllerThread;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseController {
    private String prefix = "";
    protected Properties properties = new Properties();

    public BaseController(Context context) {
        try (InputStream input = context.getResources().openRawResource(context.getResources().getIdentifier("config", "raw", context.getPackageName()))) {
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
