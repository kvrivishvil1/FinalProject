package com.example.finalproject.ui;

import java.io.IOException;
import java.net.Socket;

public class SocketHandler {
    private static Socket socket;
    private static final int PORT = 8888;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }

    public static synchronized int getPort() {
        return PORT;
    }

    public static synchronized boolean closeSocket() {
        if (socket == null) return false;
        try {
            socket.close();
            socket = null;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}