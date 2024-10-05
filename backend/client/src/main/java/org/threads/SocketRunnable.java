package org.threads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class SocketRunnable implements Runnable {
    protected final DataInputStream dataInputStream;
    protected final DataOutputStream dataOutputStream;
    protected Socket socket;

    public SocketRunnable(Socket socket) {
        try {
            this.socket = socket;
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanup() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
