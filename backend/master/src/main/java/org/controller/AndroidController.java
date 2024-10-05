package org.controller;

import com.google.gson.Gson;
import org.structures.Curve;
import org.structures.Result;
import org.threads.ControllerThread;
import org.threads.SocketRunnable;

import java.net.Socket;
import java.util.Properties;

//
// Controller for second assignment
//
public class AndroidController {
    private final Curve selectedCurve;
    private final Properties properties;
    private Result result;

    private class ServiceClientRunnable extends SocketRunnable {

        public ServiceClientRunnable(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {

            try {
                System.out.println("Sending route to worker ... ");

                String json = new Gson().toJson(selectedCurve);

                dataOutputStream.writeUTF(json);
                dataOutputStream.flush();

                String result_son = dataInputStream.readUTF();

                AndroidController.this.result = new Gson().fromJson(result_son, Result.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                super.cleanup();
            }
        }
    }

    public AndroidController(Curve selectedCurve, Properties properties) {
        this.selectedCurve = selectedCurve;
        this.properties = properties;
    }

    public Result process() {
        try {
            Socket socket = new Socket(properties.getProperty("SERVER_IP"), Integer.parseInt(properties.getProperty("SERVER_PORT")));
            ControllerThread thread= new ControllerThread(new ServiceClientRunnable(socket));
            thread.await();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
