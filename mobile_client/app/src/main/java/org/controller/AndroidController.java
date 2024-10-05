package org.controller;

import android.content.Context;

import com.google.gson.Gson;

import org.structures.Curve;
import org.structures.Result;
import org.threads.ControllerThread;
import org.threads.SocketRunnable;

import java.net.Socket;
import java.util.HashMap;

//
// Controller for second assignment
//
public class AndroidController extends BaseController {
    private final Curve selectedCurve;
    private class ServiceClientRunnable extends SocketRunnable {
        private Result result = null;

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

                dataOutputStream.writeUTF("GPX");
                dataOutputStream.flush();

                dataOutputStream.writeUTF(json);
                dataOutputStream.flush();

                String result_son = dataInputStream.readUTF();

                Result result =  new Gson().fromJson(result_son, Result.class);

                String result_json_cumulative = dataInputStream.readUTF();

                Result cumulativeResult =  new Gson().fromJson(result_json_cumulative, Result.class);

                System.out.println("Result            : " + result);
                System.out.println("Result (cumulative: " + cumulativeResult);

                this.result = result;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                super.cleanup();
            }
        }
    }

    private class ServiceClientRunnableProfile extends SocketRunnable {
        private Result profile = null;
        private Result globalProfile = null;

        public ServiceClientRunnableProfile(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {

            try {
                System.out.println("Sending route to server ... ");

                dataOutputStream.write(0);
                dataOutputStream.flush();

                dataOutputStream.writeUTF(properties.getProperty("USERNAME"));
                dataOutputStream.flush();

                dataOutputStream.writeUTF("PROFILE");
                dataOutputStream.flush();

                String result_son = dataInputStream.readUTF();

                Result profile = new Gson().fromJson(result_son, Result.class);

                String result_json_cumulative = dataInputStream.readUTF();

                Result globalProfile =  new Gson().fromJson(result_json_cumulative, Result.class);

                System.out.println("Result            : " + profile);
                System.out.println("Result (cumulative: " + globalProfile);

                this.profile = profile;
                this.globalProfile = globalProfile;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                super.cleanup();
            }
        }
    }

    public AndroidController(Context context, Curve selectedCurve) {
        super(context);
        this.selectedCurve = selectedCurve;
    }

    public Result processGpx() {
        try {
            Socket socket = new Socket(properties.getProperty("SERVER_IP"), Integer.parseInt(properties.getProperty("SERVER_PORT")));
            ServiceClientRunnable serviceClientRunnable = new ServiceClientRunnable(socket);
            ControllerThread thread= new ControllerThread(serviceClientRunnable);
            thread.await();
            return serviceClientRunnable.result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public HashMap<String, Result> processProfile() {
        try {
            Socket socket = new Socket(properties.getProperty("SERVER_IP"), Integer.parseInt(properties.getProperty("SERVER_PORT")));
            ServiceClientRunnableProfile runnable = new ServiceClientRunnableProfile(socket);
            ControllerThread thread= new ControllerThread(runnable);
            thread.await();

            HashMap<String, Result> map = new HashMap<String, Result>();
            map.put("profile", runnable.profile);
            map.put("global_profile", runnable.globalProfile);

            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
