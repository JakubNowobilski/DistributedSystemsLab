import akka.actor.typed.ActorRef;

public class ErrorsRequest implements StatusCommand{
    public final int sat_id;
    public final ActorRef<StatusCommand> replyTo;

    public ErrorsRequest(int sat_id, ActorRef<StatusCommand> replyTo) {
        this.sat_id = sat_id;
        this.replyTo = replyTo;
    }
}
