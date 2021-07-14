import com.zeroc.Ice.*;

import java.lang.Exception;

public class Server {
    public static void main(String[] args) {
        int status = 0;
        try{
            Communicator communicator = Util.initialize(args, "src/main/resources/config.server");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Server shutting down.");
                communicator.destroy();
            }));

            ObjectAdapter adapter = communicator.createObjectAdapter("Server.Adapter");

            final CalcI calc = new CalcI();

            adapter.add(calc, Util.stringToIdentity("calc"));
            adapter.activate();

            System.out.println("Server: " + adapter.getName());
            System.out.println("Server endpoints: ");
            for(Endpoint e: adapter.getEndpoints())
                System.out.println(e.toString());

            communicator.waitForShutdown();
        }catch (Exception e){
            e.printStackTrace();
            status = 1;
        }
        System.exit(status);
    }
}
