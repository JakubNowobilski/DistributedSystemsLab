import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

public class ServerUdpThread extends Thread {
    private DatagramSocket serverUdpSocket;
    private byte[] receiveBuffer;
    private final HashMap<Integer, PrintWriter> clientsMap;

    public ServerUdpThread(DatagramSocket serverUdpSocket, HashMap<Integer, PrintWriter> clientsMap){
        this.serverUdpSocket = serverUdpSocket;
        this.receiveBuffer = new byte[1024];
        this.clientsMap = clientsMap;
    }

    public void run(){
        while (true){
            Arrays.fill(receiveBuffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            try {
                serverUdpSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String data = new String(receivePacket.getData());
            int clientId = Integer.parseInt(data.substring(1, data.indexOf('>')));
            String msg = data.substring(data.indexOf('>') + 1);

            String timeStamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String finalMsg = "[<" + clientId + "> - " + timeStamp + "] " + msg;
            System.out.println("\u001B[32m" + "U " + finalMsg + "\u001B[0m");
            
            clientsMap.entrySet().stream().filter((c -> c.getKey() != clientId)).
                    forEach(c -> c.getValue().println(finalMsg));
        }
    }
}
