import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ClientMulUdpReader extends Thread{
    private MulticastSocket mulSocket;
    private byte[] receiveBuffer;
    private Client client;
    private int clientId;

    public ClientMulUdpReader(MulticastSocket mulSocket, Client client, int clientId){
        this.mulSocket = mulSocket;
        this.receiveBuffer = new byte[1024];
        this.client = client;
        this.clientId = clientId;
    }

    public void run(){
        try {
            mulSocket.setSoTimeout(2000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (client.getState()){
            Arrays.fill(receiveBuffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            try {
                mulSocket.receive(receivePacket);
            } catch (SocketTimeoutException e){
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            String data = new String(receivePacket.getData());
            int clientId = Integer.parseInt(data.substring(1, data.indexOf('>')));
            if(this.clientId == clientId)
                continue;
            String msg = data.substring(data.indexOf('>') + 1);

            String timeStamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String finalMsg = "[<" + clientId + "> - " + timeStamp + "] " + msg;
            System.out.println(finalMsg);
        }
    }
}
