import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Admin {
    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        admin.start();
    }

    private void start() throws Exception{
        // Connection
        String host = "localhost";
        String exchangeName = "GearSupplyExchange";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);

        // Queues and binds
        String adminQueue = "adminQueue";
        channel.queueDeclare(adminQueue, false, false, false, null);
        channel.queueBind(adminQueue, exchangeName, "#");

        // Consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println(timeStamp() + " " + msg);
            }
        };

        // Start listening
        System.out.println("\u001B[33m" + "\nGear Supply Admin");
        System.out.println("Basic usage: <\\s msg>, <\\c msg>, <\\e msg>\n" + "\u001B[0m");
        channel.basicConsume(adminQueue, false, consumer);

        // Main loop - send messages
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        while ((msg = consoleReader.readLine()) != null) {
            if(msg.length() < 4)
                continue;

            String mode = msg.substring(0, 2);
            msg = msg.substring(3);
            switch(mode){
                case "\\s":
                    channel.basicPublish(exchangeName, "suppliers", null, msg.getBytes("UTF-8"));
                    System.out.println(timeStamp() + " Message to [suppliers] sent.");
                    break;
                case "\\c":
                    channel.basicPublish(exchangeName, "crews", null, msg.getBytes("UTF-8"));
                    System.out.println(timeStamp() + " Message to [crews] sent.");
                    break;
                case "\\e":
                    channel.basicPublish(exchangeName, "everyone", null, msg.getBytes("UTF-8"));
                    System.out.println(timeStamp() + " Message to [everyone] sent.");
                    break;
                default:
                    System.out.println(timeStamp() + " Invalid option");
                    break;
            }
        }
    }

    private String timeStamp(){
        return "(" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")";
    }
}
