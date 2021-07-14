import akka.actor.typed.ActorRef;

public class APIRequest implements StatusCommand{
    public final int sat_id;
    public final int timeout;
    public final ActorRef<StatusCommand> replyTo;

    public APIRequest(int sat_id, int timeout, ActorRef<StatusCommand> replyTo) {
        this.sat_id = sat_id;
        this.timeout = timeout;
        this.replyTo = replyTo;
    }
}
