import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class ActorGetErrors extends AbstractBehavior<StatusCommand> {
    private final Executor ec;

    public ActorGetErrors(ActorContext<StatusCommand> context) {
        super(context);
        ec = context
                .getSystem()
                .dispatchers()
                .lookup(DispatcherSelector.fromConfig("my-dispatcher"));
    }

    public static Behavior<StatusCommand> create(){
        return Behaviors.setup(ActorGetErrors::new);
    }

    @Override
    public Receive<StatusCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ErrorsRequest.class, this::onRequest)
                .build();
    }

    public Behavior<StatusCommand> onRequest(ErrorsRequest request) {
        CompletableFuture<Integer> f =
                CompletableFuture.supplyAsync(
                        () -> MongoService.getInstance().get_errors(request.sat_id),
                        this.ec);
        try {
            request.replyTo.tell(new ErrorsResponse(request.sat_id, f.get()));
        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
        }
        return this;
    }
}
