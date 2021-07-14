import java.util.Random;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class AstraLink extends AbstractBehavior<String> {
    private ActorRef<StatusCommand> s4;

    public AstraLink(ActorContext<String> context) {
        super(context);
        s4 = null;
        System.out.println("Created: " + getContext().getSelf().path());
    }

    public static Behavior<String> create(){
        return Behaviors.setup(AstraLink::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessage(String.class, this::onRequest)
                .build();
    }

    public Behavior<String> onRequest(String request){
        if(request.equals("part1")){
            ActorRef<StatusCommand> dispatcher = getContext().spawn(ActorDispatcher.create(), "Dispatcher");
            ActorRef<StatusCommand> s1 = getContext().spawn(ActorMonitorStation.create(dispatcher), "Station_1");
            ActorRef<StatusCommand> s2 = getContext().spawn(ActorMonitorStation.create(dispatcher), "Station_2");
            ActorRef<StatusCommand> s3 = getContext().spawn(ActorMonitorStation.create(dispatcher), "Station_3");
            this.s4 = getContext().spawn(ActorMonitorStation.create(dispatcher), "Station_4");
            s1.tell(new StatusRequest(1, get_sat_id(), 50, 300, s1));
            s2.tell(new StatusRequest(2, get_sat_id(), 50, 300, s2));
            s3.tell(new StatusRequest(3, get_sat_id(), 50, 300, s3));
        }
        else if(request.equals("part2")){
            if(this.s4 != null){
                System.out.println("Sat id:\t\tErrors count:");
                for(int i = 100; i < 200; i++)
                    this.s4.tell(new ErrorsRequest(i, s4));
            }
        }
        return this;
    }

    public static void main(String[] args) throws InterruptedException {
        MongoService mongoService = MongoService.getInstance();
        mongoService.initCollection();

        ActorSystem<String> system = ActorSystem.create(AstraLink.create(), "AstraLink");
        system.tell("part1");
        Thread.sleep(1000);
        system.tell("part2");
    }

    private static int get_sat_id(){
        return 100 + (new Random().nextInt(50));
    }
}
