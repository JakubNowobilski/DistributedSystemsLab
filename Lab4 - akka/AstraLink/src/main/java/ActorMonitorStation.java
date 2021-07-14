import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ActorMonitorStation extends AbstractBehavior<StatusCommand> {
    private int actors_count;
    private final ActorRef<StatusCommand> dispatcher;
    private long start;

    public ActorMonitorStation(ActorContext<StatusCommand> context, ActorRef<StatusCommand> dispatcher) {
        super(context);
        this.dispatcher = dispatcher;
        this.start = 0;
        this.actors_count = 0;
        System.out.println("Created: " + getContext().getSelf().path());
    }

    public static Behavior<StatusCommand> create(ActorRef<StatusCommand> dispatcher){
        return Behaviors.setup(context -> new ActorMonitorStation(context, dispatcher));
    }

    @Override
    public Receive<StatusCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(StatusRequest.class, this::onRequest)
                .onMessage(StatusResponse.class, this::onResponse)
                .onMessage(ErrorsRequest.class, this::onErrorsRequest)
                .onMessage(ErrorsResponse.class, this::onErrorsResponse)
                .build();
    }

    public Behavior<StatusCommand> onRequest(StatusRequest request){
        System.out.println(getContext().getSelf().path() + " - [REQUEST]" + "\n\tQuery id: " + request.query_id + "\n");
        this.start = System.currentTimeMillis();
        this.dispatcher.tell(request);
        return this;
    }

    public Behavior<StatusCommand> onResponse(StatusResponse response){
        long finish = System.currentTimeMillis();
        System.out.println(getContext().getSelf().path() + " - [RESPONSE]");
        System.out.println("\tQuery id: " + response.query_id);
        System.out.println("\tElapsed time: " + (finish - this.start) + "ms");
        System.out.println("\tValid responses ratio: " + response.valid_responses_ratio);
        System.out.println("\tNumber of errors: " + response.results.size());
        response.results.forEach((k, v) -> {
            System.out.println("\t\t" + k + ": " + v);
        });
        System.out.println();
        response.results.forEach((k, v) -> {
            MongoService.getInstance().add_error(k);
        });
        return this;
    }

    public Behavior<StatusCommand> onErrorsRequest(ErrorsRequest request){
        this.actors_count++;
        getContext().spawn(ActorGetErrors.create(), "ActorGetErrors_" + actors_count).tell(request);
        return this;
    }

    public Behavior<StatusCommand> onErrorsResponse(ErrorsResponse response){
        if(response.errors_count != 0)
            System.out.println(response.sat_id + "\t\t\t" + response.errors_count);
        return this;
    }
}
