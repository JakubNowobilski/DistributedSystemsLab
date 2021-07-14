import java.io.*;

public class ClientTcpReader extends Thread {
    private final BufferedReader tcpReader;
    private Client client;

    public ClientTcpReader(BufferedReader tcpReader, Client client){
        this.tcpReader = tcpReader;
        this.client = client;
    }

    public void run(){
        String msg;

        try {
            while ((msg = tcpReader.readLine()) != null && !msg.equals("\\exit")){
                System.out.println(msg);
            }
        } catch (IOException e) {
            if(!e.getMessage().equals("Stream closed"))
                e.printStackTrace();
        }
        this.client.setState(false);
    }
}
