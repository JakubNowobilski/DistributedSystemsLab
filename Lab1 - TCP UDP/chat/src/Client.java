import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Client {
    private String hostName;
    private int port;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private InetAddress hostAddress;
    private volatile boolean state;
    private int clientId;
    private String mulHost;
    private MulticastSocket mulSocket;
    private InetAddress groupAddress;
    private int mulPort;

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.start();
    }

    private Client(){
        this.hostName = "localhost";
        this.port = 12345;
        this.tcpSocket = null;
        this.udpSocket = null;
        this.hostAddress = null;
        this.state = true;
        this.clientId = -1;
        this.mulHost = "228.5.6.7";
        this.mulSocket = null;
        this.groupAddress = null;
        this.mulPort = 56789;
    }

    private void start() throws IOException {
        System.out.println("\u001B[33m" + "Chat client.");
        System.out.println("Basic usage: (msg), (-U msg), (-M msg), (-A n)" + "\u001B[0m");
        PrintWriter tcpWriter = null;
        BufferedReader tcpReader = null;

        try {
            tcpSocket = new Socket(hostName, port);
            tcpWriter = new PrintWriter(tcpSocket.getOutputStream(), true);
            tcpReader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

            udpSocket = new DatagramSocket();
            hostAddress = InetAddress.getByName(this.hostName);

            groupAddress = InetAddress.getByName(this.mulHost);
            mulSocket = new MulticastSocket(mulPort);
            mulSocket.joinGroup(groupAddress);

            this.setClientId(Integer.parseInt(tcpReader.readLine()));
            System.out.println("\u001B[33m" + "Connected to chat server at: " + hostName + ":" + port + "\nAcquired id: <" + clientId + ">\n" + "\u001B[0m");


            ClientTcpReader clientTcpReader = new ClientTcpReader(tcpReader, this);
            ClientWriter clientTcpWriter = new ClientWriter(tcpWriter, this, udpSocket, hostAddress, port, groupAddress, mulPort);
            ClientMulUdpReader clientMulUdpReader = new ClientMulUdpReader(mulSocket, this, clientId);
            clientTcpReader.start();
            clientTcpWriter.start();
            clientMulUdpReader.start();

            clientTcpReader.join();
            mulSocket.leaveGroup(groupAddress);
            System.out.println("\u001B[33m" + "Disconnected from chat server at: " + hostName + ":" + port + "\n" + "\u001B[0m");
        } catch (ConnectException | SocketTimeoutException e){
            System.out.println("\u001B[33m" + "Cannot connect to chat server at: " + hostName + ":" + port + "\n" + "\u001B[0m");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tcpSocket != null){
                tcpSocket.close();
            }
            if (tcpWriter != null) {
                tcpWriter.close();
            }
            if (tcpReader != null) {
                tcpReader.close();
            }
        }
    }

    public void setState(boolean state){
        this.state = state;
    }

    public boolean getState(){
        return this.state;
    }

    private void setClientId(int clientId){
        this.clientId = clientId;
    }

    public int getClientId(){
        return this.clientId;
    }
}
