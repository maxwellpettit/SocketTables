
package socket.table.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import socket.table.server.SocketTableMessageHandler;

public class SocketTableServer {

    // Number of threads to handle incoming requests
    protected static final int CLIENT_POOL_SIZE = 1;

    // As far as I know, available "team use" ports are numbered 5800-5810
    public static final int DEFAULT_ROBOT_SERVER_PORT = 5801;
    private int port = 7777;

    protected boolean stopped = false;

    private SocketTableData socketTableData = new SocketTableData();

    public SocketTableServer() {
    }

    public SocketTableServer(int port) {
        this.port = port;
    }

    public void stop() {
        this.stopped = true;
    }

    public void start() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(CLIENT_POOL_SIZE);

        Runnable serverTask = () -> {

            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(SocketTableServer.this.port);
                System.out.println("SocketTableServer is listning on port: " + SocketTableServer.this.port);
            } catch (Exception ex) {
                SocketTableServer.this.stopped = true;
                System.out.println("Failed to start SocketTableServer on port: " + SocketTableServer.this.port);
                ex.printStackTrace();
            }

            while (!SocketTableServer.this.stopped) {
                Socket clientSocket = null;

                try {
                    clientSocket = serverSocket.accept();
                } catch (Exception ex) {
                    System.out.println("Failure on serverSocket accept.");
                    ex.printStackTrace();
                }

                if (clientSocket != null) {
                    // clientProcessingPool.submit(new SocketTableMessageHandler(socketTableData, clientSocket));
                    new SocketTableMessageHandler(socketTableData, clientSocket).run();
                    System.out.println("SocketTableServer waiting...");
                }
            }

            System.out.println("SocketTableServer stopped.");
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    /**
     * @return the stopped
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        SocketTableServer server = new SocketTableServer();
        server.start();
    }

}