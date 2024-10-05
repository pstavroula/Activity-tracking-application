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

    private class ServiceClientRunnable extends SocketRunnable {

        public ServiceClientRunnable(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {

            try {
                System.out.println("Sending route to server ... ");

                String json = new Gson().toJson(selectedCurve);

                dataOutputStream.write(0);
                dataOutputStream.flush();

                dataOutputStream.writeUTF(properties.getProperty("USERNAME"));
                dataOutputStream.flush();

                dataOutputStream.writeUTF(json);
                dataOutputStream.flush();

                String result_son = dataInputStream.readUTF();

                Result result =  new Gson().fromJson(result_son, Result.class);

                String result_json_cumulative = dataInputStream.readUTF();

                Result cumulativeResult =  new Gson().fromJson(result_json_cumulative, Result.class);

                System.out.println("Result            : " + result);
                System.out.println("Result (cumulative: " + cumulativeResult);
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

    public void process() {
        try {
            Socket socket = new Socket(properties.getProperty("SERVER_IP"), Integer.parseInt(properties.getProperty("SERVER_PORT")));
            ControllerThread thread= new ControllerThread(new ServiceClientRunnable(socket));
            thread.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
