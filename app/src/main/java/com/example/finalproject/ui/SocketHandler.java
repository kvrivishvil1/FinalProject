package com.example.finalproject.ui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandler {
    private static Socket socket;

    private static ServerSocket serverSocket;
    private static final int PORT = 8383;
    private static final String STOP_WORD = "3g8ABPAk1RrDXX0";

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }


    public static synchronized void setServerSocket(ServerSocket serverSocket) {
        SocketHandler.serverSocket = serverSocket;
    }

    public static synchronized int getPort() {
        return PORT;
    }

    public static synchronized String getStopWord() { return STOP_WORD; }

    public static void stopSocket() {
        if (socket == null) return;
        new Thread() {
            @Override
            public void run() {
                try {
                    socket.getOutputStream().write(getStopWord().getBytes());
                    closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public static synchronized boolean closeSocket() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;

    }

    public static void write(final String msg) {
        if (socket == null || socket.isClosed()) return;
        try {
            (new Thread() {
                @Override
                public void run() {
                    try {
                        socket.getOutputStream().write(msg.getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}