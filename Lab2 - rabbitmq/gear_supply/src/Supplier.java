import com.rabbitmq.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Supplier {
    public int orderNo = 1;

    public static void main(String[] args) throws Exception {
        Supplier supplier = new Supplier();
        supplier.start();
    }

    private void start() throws Exception {
        // Supplier data
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter supplier name:");
        String supplierName = consoleReader.readLine();
        System.out.println("Enter available items:");
        String[] items = consoleReader.readLine().split(" ");

        // Connection
        String host = "localhost";
        String exchangeName = "GearSupplyExchange";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
        channel.basicQos(1);

        // Queues and binds
        String adminQueue = supplierName + "AdminQueue";
        channel.queueDeclare(adminQueue, false, false, false, null);
        channel.queueBind(adminQueue, exchangeName, "suppliers");
        channel.queueBind(adminQueue, exchangeName, "everyone");
        for(String item: items){
            channel.queueDeclare(item, false, false, false, null);
            channel.queueBind(item, exchangeName, item);
        }

        // Consumers
        Consumer adminConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println("\u001B[33m" + timeStamp() + " Admin: " + msg + "\u001B[0m");
            }
        };

        Consumer itemsConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                String crewName = msg.substring(0, msg.indexOf("|"));
                String order = msg.substring(msg.indexOf("|") + 1);
                channel.basicAck(envelope.getDeliveryTag(), false);
                String orderStamp = "[" + orderNo + " - " + crewName + "]";
                System.out.println(timeStamp() + " Order " + orderStamp + " for [" + order + "] received.");

                String reply = supplierName + "|" + orderStamp + "|" + order;
                channel.basicPublish(exchangeName, crewName, null, reply.getBytes("UTF-8"));
                orderNo++;
            }
        };

        // Start listening
        System.out.print("\u001B[33m" + "\nSupplier " + supplierName + "\n(");
        for(int i = 0; i < items.length; i++){
            if(i == items.length - 1)
                System.out.print(items[i] + ")\n");
            else
                System.out.print(items[i] + ", ");
        }
        System.out.println("Waiting for messages.\n" + "\u001B[0m");
        channel.basicConsume(adminQueue, false, adminConsumer);
        for(String item: items){
            channel.basicConsume(item, false, itemsConsumer);
        }
    }

    private String timeStamp(){
        return "(" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")";
    }
}
