import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ClientWriter extends Thread{
    private final PrintWriter tcpWriter;
    private Client client;
    private DatagramSocket udpSocket;
    private InetAddress hostAddress;
    private int port;
    private InetAddress groupAddress;
    private int mulPort;

    public ClientWriter(PrintWriter tcpWriter, Client client, DatagramSocket udpSocket, InetAddress hostAddress, int port, InetAddress groupAddress, int mulPort){
        this.tcpWriter = tcpWriter;
        this.client = client;
        this.udpSocket = udpSocket;
        this.hostAddress = hostAddress;
        this.port = port;
        this.groupAddress = groupAddress;
        this.mulPort = mulPort;
    }

    public void run(){
        Scanner consoleScanner = new Scanner(System.in);
        String msg;

        while (true){
            if((msg = consoleScanner.nextLine()) == null)
                break;
            if(msg.equals("\\exit")){
                tcpWriter.println("\\exit");
                break;
            }
            if(!this.client.getState())
                break;
            if(msg.length() == 0)
                continue;
            if(msg.length() > 3 && msg.startsWith("-A ")){
                msg = msg.substring(3);
                try{
                    int idx = Integer.parseInt(msg);
                    msg = getAscii(idx);
                    sendUdp(msg, this.hostAddress, this.port);
                }catch (NumberFormatException e){
                    continue;
                }
            }
            else if(msg.length() > 3 && msg.startsWith("-U ")){
                msg = msg.substring(3);
                sendUdp(msg, this.hostAddress, this.port);
            }
            else if(msg.length() > 3 && msg.startsWith("-M ")){
                msg = msg.substring(3);
                sendUdp(msg, this.groupAddress, this.mulPort);
            }
            else
                tcpWriter.println(msg);

            String timeStamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String finalMsg = "[" + timeStamp + "] " + msg;
            System.out.println("\u001B[32m" + finalMsg + "\u001B[0m");
        }
    }

    private void sendUdp(String msg, InetAddress address, int portNumber){
        try {
            DatagramPacket sendPacket;
            while(true){
                if(msg.length() > 1024){
                    byte[] sendBuffer = ("<" + client.getClientId() + ">" + msg.substring(0, 1024)).getBytes();
                    msg = msg.substring(1024);
                    sendPacket  = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
                    udpSocket.send(sendPacket);
                }
                else {
                    byte[] sendBuffer = ("<" + client.getClientId() + ">" + msg).getBytes();
                    sendPacket  = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
                    udpSocket.send(sendPacket);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAscii(int idx){
        String[] asciiArt = {"\n    ,__    _,            ___\n" +
                "     '.`\\ /`|     _.-\"```   `'.\n" +
                "       ; |  /   .'             `} \n" +
                "       _\\|\\/_.-'                 }\n" +
                "   _.-\"a                 {        }\n" +
                ".-`  __    /._          {         }\\\n" +
                "'--\"`  `\"\"`   `\\   ;    {         } \\\n" +
                "               |   } __ _\\       }\\  \\\n" +
                "               |  /;`   / :.   }`  \\  \\\n" +
                "               | | | .-' /  / /     '. '._\n" +
                "      jgs    .'__/-' ````.-'.'        '-._'-._\n" +
                "             ```        ````              `\"\"\"`\n",

                "\n                    _,,......_\n" +
                        "                 ,-'          `'--.\n" +
                        "              ,-'  _              '-.\n" +
                        "     (`.    ,'   ,  `-.              `.\n" +
                        "      \\ \\  -    / )    \\               \\\n" +
                        "       `\\`-^^^, )/      |     /         :\n" +
                        "         )^ ^ ^V/            /          '.\n" +
                        "         |      )            |           `.\n" +
                        "         9   9 /,--,\\    |._:`         .._`.\n" +
                        "         |    /   /  `.  \\    `.      (   `.`.\n" +
                        "         |   / \\  \\    \\  \\     `--\\   )    `.`.___\n" +
                        "-hrr-   .;;./  '   )   '   )       ///'       `-\"'\n" +
                        "        `--'   7//\\    ///\\\n",

                "\n             ,\n" +
                        "       (`.  : \\               __..----..__\n" +
                        "        `.`.| |:          _,-':::''' '  `:`-._\n" +
                        "          `.:\\||       _,':::::'         `::::`-.\n" +
                        "            \\\\`|    _,':::::::'     `:.     `':::`.\n" +
                        "             ;` `-''  `::::::.                  `::\\\n" +
                        "          ,-'      .::'  `:::::.         `::..    `:\\\n" +
                        "        ,' /_) -.            `::.           `:.     |\n" +
                        "      ,'.:     `    `:.        `:.     .::.          \\\n" +
                        " __,-'   ___,..-''-.  `:.        `.   /::::.         |\n" +
                        "|):'_,--'           `.    `::..       |::::::.      ::\\\n" +
                        " `-'                 |`--.:_::::|_____\\::::::::.__  ::|\n" +
                        "                     |   _/|::::|      \\::::::|::/\\  :|\n" +
                        "                     /:./  |:::/        \\__:::):/  \\  :\\\n" +
                        "                   ,'::'  /:::|        ,'::::/_/    `. ``-.__\n" +
                        "     jrei         ''''   (//|/\\      ,';':,-'         `-.__  `'--..__\n" +
                        "                                                           `''---::::'\n"};
        return asciiArt[idx % asciiArt.length];
    }
}
