package org.voicemessanger.server.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolServer implements Runnable {
    protected int serverPort = 30003;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    public ThreadPoolServer(int port) {
        this.serverPort = port;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            int i = 0; // Счётчик подключений
            try {
                clientSocket = this.serverSocket.accept();
                System.err.println("Clients accepted");
                System.out.println("Clients ЗДЕСЬ НОВЫЙ");

            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("[Сервер] Остановка сервера...");
                    break;
                }
                throw new RuntimeException(
                        "Ощибка подтверждения соединения с клиентом", e);
            }
            Server serv = new Server();
            serv.setThisObj(serv);
            serv.setSocket(i++, clientSocket);
            this.threadPool.execute(
                    serv
            );
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.");
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            InetAddress ia;
            ia = InetAddress.getByName("localhost");
            //ia = InetAddress.getByName("94.228.117.231");
            System.out.println("ip "+ ia.getHostAddress() + " and " + "SERVER");
            this.serverSocket = new ServerSocket(this.serverPort,0, ia);

           // srvSocket = new ServerSocket(port, 0, ia);
            System.out.println("Server started\n\n");
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }

}
