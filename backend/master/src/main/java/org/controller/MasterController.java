package org.controller;

import org.structures.Curve;
import org.structures.Result;
import org.structures.ResultFactory;
import org.threads.SocketRunnable;
import com.google.gson.Gson;
import sun.awt.windows.ThemeReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MasterController extends BaseController {
    private ServerSocket socket;
    private HashMap<String, Result> userCumulativeResults = new HashMap<>();
    private ArrayList<String> workerConnectionData = new ArrayList<>();

    private class ServiceClientRunnable extends SocketRunnable {
        public ServiceClientRunnable(Socket socket) {
            super(socket);
        }


        private void requestFromWorker(int i, Curve curve, Result result) {
            String socket_port = workerConnectionData.get(i);
            String [] fields = socket_port.split(":");
            String ip = fields[0];
            int port = Integer.parseInt(fields[1]);

            Properties properties = new Properties();
            properties.put("SERVER_IP",ip);
            properties.put("SERVER_PORT",String.valueOf(port));

            AndroidController androidController = new AndroidController(curve, properties);

            Result partialResult = androidController.process();

            result.setTotalDistance(partialResult.getTotalDistance());
            result.setTotalElevation(partialResult.getTotalElevation());
            result.setTotalTime(partialResult.getTotalTime());
        }

        public Result distributeAndCollect(Curve selectedCurve) {
            int workers = workerConnectionData.size();

            if (workers == 0) {
                return new Result();
            }

            List<Curve> subcurves = new ArrayList<>();
            List<Result> results = new ArrayList<>();

            for (int i=0;i<workers;i++) {
                subcurves.add(new Curve());
                results.add(new Result());
            }

            int pointsPerWorker = selectedCurve.getPoints().size()/workers;
            int p = 0;

            for (int i=0;i<workers;i++) {
                for (int j=0;j<pointsPerWorker;j++) {
                    subcurves.get(i).getPoints().add(selectedCurve.getPoints().get(p));
                    p++;
                }
            }

            while (p < selectedCurve.getPoints().size()) {
                subcurves.get(workers-1).getPoints().add(selectedCurve.getPoints().get(p));
                p++;
            }

            List<Thread> threads = new ArrayList<>();

            for (int i=0;i<workers;i++) {
                final int j = i;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestFromWorker(j, subcurves.get(j), results.get(j));
                    }
                });

                threads.add(t);

                t.start();
            }

            for (int i=0;i<workers;i++) {
                try {
                    threads.get(i).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Result result = new Result();

            for (int i=0;i<workers;i++) {
                Result partial = results.get(i);
                result.setTotalDistance(result.getTotalDistance() + partial.getTotalDistance());
                result.setTotalElevation(result.getTotalElevation() + partial.getTotalElevation());
                result.setTotalTime(result.getTotalTime() + partial.getTotalTime());;
            }

            return result;
        }

        @Override
        public void run() {
            try {
                System.out.println("Receiving route from client ... ");

                String username = dataInputStream.readUTF();

                String json = dataInputStream.readUTF();

                Curve selectedCurve = new Gson().fromJson(json, Curve.class);

                System.out.println("Received from username:" + username + " => path with " + selectedCurve.getPoints().size() + " points received. ");

                Result result = distributeAndCollect(selectedCurve);

                if (!userCumulativeResults.containsKey(username)) {
                    userCumulativeResults.put(username, result);
                } else {
                    Result oldresult = userCumulativeResults.get(username);
                    oldresult.setTotalDistance(oldresult.getTotalDistance() + result.getTotalDistance());
                    oldresult.setTotalElevation(oldresult.getTotalElevation() + result.getTotalElevation());
                    oldresult.setTotalTime(oldresult.getTotalTime() + result.getTotalTime());;
                    userCumulativeResults.put(username, oldresult);
                }

                Result cumulativeUserResult = userCumulativeResults.get(username);

                String json_response = new Gson().toJson(result);
                String json_response_cumulative = new Gson().toJson(cumulativeUserResult);

                dataOutputStream.writeUTF(json_response);
                dataOutputStream.flush();

                dataOutputStream.writeUTF(json_response_cumulative);
                dataOutputStream.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                super.cleanup();
            }
        }
    }

    private class ServiceWorkerRunnable extends SocketRunnable {

        public ServiceWorkerRunnable(Socket socket) {
            super(socket);

            try {
                System.out.println("Receiving socket data from worker  ... ");

                String ip = dataInputStream.readUTF();
                String port = dataInputStream.readUTF();

                workerConnectionData.add(ip + ":" + port);

                System.out.println(workerConnectionData);

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                super.cleanup();
            }
        }

        @Override
        public void run() {

            super.cleanup();
        }
    }

    private class ServiceUnknownClient extends SocketRunnable {

        public ServiceUnknownClient(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {
            System.out.println("Unidentified client connected. Client  rejected");
            super.cleanup();
        }
    }


    public MasterController() {
        try {
            socket = new ServerSocket(Integer.parseInt(properties.getProperty("SERVER_PORT")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Waiting for clients at " + properties.getProperty("SERVER_PORT") + " ... ");

                Socket clientSocket = socket.accept();
                int x = clientSocket.getInputStream().read();
                Runnable runnable;

                switch (x) {
                    case 0:
                        System.out.println("Client connected");
                        runnable = new ServiceClientRunnable(clientSocket);
                        break;
                    case 1:
                        System.out.println("Worker connected");
                        runnable = new ServiceWorkerRunnable(clientSocket);
                        break;
                    default :
                        runnable = new ServiceUnknownClient(clientSocket);
                }

                startThread(runnable);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanup() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
