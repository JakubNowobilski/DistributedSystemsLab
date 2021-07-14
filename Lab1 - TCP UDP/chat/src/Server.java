import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.*;
import java.net.*;

public class Server {
    private ServerSocket serverTcpSocket;
    private DatagramSocket serverUdpSocket;
    private int port;
    private ExecutorService tcpThreadPool;
    private HashMap<Integer, PrintWriter> clientsMap;
    private int clientsCount;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private Server(){
        this.serverTcpSocket = null;
        this.serverUdpSocket = null;
        this.port = 12345;
        this.tcpThreadPool = Executors.newCachedThreadPool();
        this.clientsMap = new HashMap<>();
        this.clientsCount = 0;
    }

    private void start(){
        System.out.println("\u001B[33m" + "Chat server.\nAccepting connections on port: 12345\n" + "\u001B[0m");

        try {
            serverTcpSocket = new ServerSocket(port);
            serverUdpSocket = new DatagramSocket(port);
            ServerUdpThread serverUdpThread = new ServerUdpThread(serverUdpSocket, clientsMap);
            serverUdpThread.start();

            while (true){
                Socket clientTcpSocket = serverTcpSocket.accept();
                clientsCount++;
                PrintWriter tcpWriter = new PrintWriter(clientTcpSocket.getOutputStream(), true);
                clientsMap.put(clientsCount, tcpWriter);

                Callable<Object> tcpCallable = new ServerTcpCallable(clientsCount, clientTcpSocket, clientsMap);
                tcpThreadPool.submit(tcpCallable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
