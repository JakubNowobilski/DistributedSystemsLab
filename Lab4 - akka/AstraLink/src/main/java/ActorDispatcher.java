import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ActorDispatcher extends AbstractBehavior<StatusCommand> {
    private int actors_count;

    public ActorDispatcher(ActorContext<StatusCommand> context) {
        super(context);
        this.actors_count = 0;
        System.out.println("Created: " + getContext().getSelf().path());
    }

    public static Behavior<StatusCommand> create(){
        return Behaviors.setup(ActorDispatcher::new);
    }

    @Override
    public Receive<StatusCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(StatusRequest.class, this::onRequest)
                .build();
    }

    public Behavior<StatusCommand> onRequest(StatusRequest request){
        this.actors_count++;
        getContext().spawn(ActorStatusService.create(), "ActorStatusService_" + this.actors_count).tell(request);
        return this;
    }
}
