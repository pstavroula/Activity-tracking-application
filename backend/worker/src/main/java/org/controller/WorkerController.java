package org.controller;

import com.google.gson.Gson;
import org.structures.Curve;
import org.structures.Result;
import org.structures.ResultFactory;
import org.threads.ControllerThread;
import org.threads.SocketRunnable;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

//
// Controller for second assignment
//
public class WorkerController extends BaseController{
    private ServerSocket socket;
    private String myIP;

    //
    // Runnable to authenticate to server
    //
    private class WorkerAuthenticateRunnable extends SocketRunnable {

        public WorkerAuthenticateRunnable(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {

            try {
                System.out.println("Sending route to server ... ");

                dataOutputStream.write(1);
                dataOutputStream.flush();

                dataOutputStream.writeUTF(myIP);
                dataOutputStream.flush();

                dataOutputStream.writeUTF(String.valueOf(WorkerController.this.socket.getLocalPort()));
                dataOutputStream.flush();

                System.out.println("Authenticated successfully ");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                super.cleanup();
            }
        }
    }

    private class ServiceMasterRunnable extends SocketRunnable {
        public ServiceMasterRunnable(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {
            try {
                System.out.println("Receiving route from master ... ");

                String json = dataInputStream.readUTF();

                Curve selectedCurve = new Gson().fromJson(json, Curve.class);

                System.out.println("Received => subpath with " + selectedCurve.getPoints().size() + " points received. ");

                System.out.println("Calculating results for this subpath... ");

                Result result = ResultFactory.build(selectedCurve);

                String json_response = new Gson().toJson(result);

                System.out.println("Sending results to master ... ");

                dataOutputStream.writeUTF(json_response);
                dataOutputStream.flush();

                System.out.println("Service complete ... ");
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                super.cleanup();
            }
    }
    }

    public WorkerController() {
        try {
            socket = new ServerSocket(0);

            myIP = Inet4Address.getLocalHost().toString().split("/")[1];
            System.out.println("Server port opened at: " + myIP + ":" + socket.getLocalPort() + "successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            Socket authSocket = new Socket(properties.getProperty("SERVER_IP"), Integer.parseInt(properties.getProperty("SERVER_PORT")));
            ControllerThread thread= new ControllerThread(new WorkerAuthenticateRunnable(authSocket));
            thread.await();

            while (true) {
                System.out.println("Waiting for clients at local port: " + socket.getLocalPort() + " ... ");

                Socket clientSocket = socket.accept();
                Runnable runnable = new ServiceMasterRunnable(clientSocket);
                startThread(runnable);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
