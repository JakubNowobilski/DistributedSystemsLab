import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Crew {
    public static void main(String[] args) throws Exception {
        Crew crew = new Crew();
        crew.start();
    }

    private void start() throws Exception{
        // Crew data
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter crew name:");
        String crewName = consoleReader.readLine();

        // Connection
        String host = "localhost";
        String exchangeName = "GearSupplyExchange";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);

        // Queues and binds
        String adminQueue = crewName + "AdminQueue";
        channel.queueDeclare(adminQueue, false, false, false, null);
        channel.queueBind(adminQueue, exchangeName, "crews");
        channel.queueBind(adminQueue, exchangeName, "everyone");
        String crewQueue = crewName;
        channel.queueDeclare(crewQueue, false, false, false, null);
        channel.queueBind(crewQueue, exchangeName, crewQueue);

        // Consumers
        Consumer adminConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println("\u001B[33m" + timeStamp() + " Admin: " + msg + "\u001B[0m");
            }
        };

        Consumer ackConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                String supplierName = msg.substring(0, msg.indexOf("|"));
                String orderStamp = msg.substring(msg.indexOf("|") + 1);
                String order = orderStamp.substring(orderStamp.indexOf("|") + 1);
                orderStamp = orderStamp.substring(0, orderStamp.indexOf("|"));
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println(timeStamp() + " Order " + orderStamp + " for [" + order + "] acknowledged by [" + supplierName + "]");
            }
        };

        // Start listening
        System.out.println("\u001B[33m" + "\nCrew " + crewName);
        System.out.println("Enter item to order:\n" + "\u001B[0m");
        channel.basicConsume(adminQueue, false, adminConsumer);
        channel.basicConsume(crewQueue, false, ackConsumer);

        // Main loop - send order
        String order;
        while ((order = consoleReader.readLine()) != null) {
            if(order.length() == 0)
                continue;

            String msg = crewName + "|" + order;
            channel.basicPublish(exchangeName, order, null, msg.getBytes("UTF-8"));
            System.out.println(timeStamp() + " Order for [" + order + "] sent.");
        }
    }

    private String timeStamp(){
        return "(" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")";
    }
}
