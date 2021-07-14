import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.concurrent.*;

public class ActorApiService extends AbstractBehavior<StatusCommand> {
    private final Executor ec;

    public ActorApiService(ActorContext<StatusCommand> context) {
        super(context);
        ec = context
                .getSystem()
                .dispatchers()
                .lookup(DispatcherSelector.fromConfig("my-dispatcher"));
    }

    public static Behavior<StatusCommand> create(){
        return Behaviors.setup(ActorApiService::new);
    }

    @Override
    public Receive<StatusCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(APIRequest.class, this::onRequest)
                .build();
    }

    public Behavior<StatusCommand> onRequest(APIRequest request) throws InterruptedException{
        CompletableFuture<SatelliteAPI.Status> f =
                CompletableFuture.supplyAsync(
                        () -> SatelliteAPI.getStatus(request.sat_id),
                        this.ec);
        try {
            SatelliteAPI.Status status = f.get(300, TimeUnit.MILLISECONDS);
            request.replyTo.tell(new APIResponse(request.sat_id, status, false));
        } catch (ExecutionException | TimeoutException e) {
            request.replyTo.tell(new APIResponse(request.sat_id, null, true));
        }
        return this;
    }
}
