import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import java.util.HashMap;

public class ActorStatusService extends AbstractBehavior<StatusCommand> {
    private StatusRequest request = null;
    private int valid_responses;
    private int responses;
    private HashMap<Integer, SatelliteAPI.Status> results;

    public ActorStatusService(ActorContext<StatusCommand> context) {
        super(context);
        this.results = new HashMap<>();
        this.valid_responses = 0;
        this.responses = 0;
        System.out.println("Created: " + getContext().getSelf().path());
    }

    public static Behavior<StatusCommand> create(){
        return Behaviors.setup(ActorStatusService::new);
    }

    @Override
    public Receive<StatusCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(StatusRequest.class, this::onRequest)
                .onMessage(APIResponse.class, this::onResponse)
                .build();
    }

    public Behavior<StatusCommand> onRequest(StatusRequest request){
        this.request = request;
        for(int i = request.first_sat_id; i < request.first_sat_id + request.range; i++)
            getContext().spawn(ActorApiService.create(), "ActorApiService_" + i).tell(new APIRequest(i, request.timeout, getContext().getSelf()));
        return this;
    }

    public Behavior<StatusCommand> onResponse(APIResponse response){
        this.responses++;
        if(!response.isTimedOut){
            this.valid_responses++;
            if(response.status != SatelliteAPI.Status.OK)
                this.results.put(response.sat_id, response.status);
        }
        if(this.responses == this.request.range)
            sendResponse((float) this.valid_responses / this.responses);
        return this;
    }

    public void sendResponse(float valid_responses_ratio){
        if(this.request.replyTo != null)
            this.request.replyTo.tell(new StatusResponse(this.request.query_id, this.results, valid_responses_ratio));
    }
}
