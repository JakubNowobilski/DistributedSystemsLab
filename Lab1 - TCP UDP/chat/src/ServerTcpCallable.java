import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class ServerTcpCallable implements Callable {
    private final int clientId;
    private final Socket clientSocket;
    private final HashMap<Integer, PrintWriter> clientsMap;

    public ServerTcpCallable(int clientId, Socket clientSocket, HashMap<Integer, PrintWriter> clientsMap){
        this.clientId = clientId;
        this.clientSocket = clientSocket;
        this.clientsMap = clientsMap;
    }

    @Override
    public Object call() throws Exception {
        BufferedReader tcpReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter tcpWriter = clientsMap.get(clientId);
        System.out.println("\u001B[33m" + "Client <" + clientId + "> connected.\n" + "\u001B[0m");
        tcpWriter.println(clientId);

        String msg;
        while((msg = tcpReader.readLine()) != null && !msg.equals("\\exit")){
            String timeStamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String finalMsg = "[<" + this.clientId + "> - " + timeStamp + "] " + msg;
            System.out.println("\u001B[32m" + "T " + finalMsg + "\u001B[0m");

            clientsMap.entrySet().stream().filter((c -> c.getKey() != clientId)).
                    forEach(c -> c.getValue().println(finalMsg));

        }
        System.out.println("\u001B[33m" + "Client <" + clientId + "> disconnected.\n" + "\u001B[0m");
        this.clientsMap.remove(this.clientId);
        this.clientSocket.close();
        return null;
    }
}