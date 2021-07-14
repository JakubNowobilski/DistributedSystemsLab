import akka.actor.typed.ActorRef;

public class StatusRequest implements StatusCommand{
    public final int query_id;
    public final int first_sat_id;
    public final int range;
    public final int timeout;
    public final ActorRef<StatusCommand> replyTo;

    public StatusRequest(int query_id, int first_sat_id, int range, int timeout, ActorRef<StatusCommand> replyTo){
        this.query_id = query_id;
        this.first_sat_id = first_sat_id;
        this.range = range;
        this.timeout = timeout;
        this.replyTo = replyTo;
    }
}
