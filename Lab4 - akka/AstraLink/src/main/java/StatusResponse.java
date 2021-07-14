import java.util.HashMap;

public class StatusResponse implements StatusCommand{
    public final int query_id;
    public final HashMap<Integer, SatelliteAPI.Status> results;
    public final float valid_responses_ratio;

    public StatusResponse(int query_id, HashMap<Integer, SatelliteAPI.Status> results, float valid_responses_ratio) {
        this.query_id = query_id;
        this.results = results;
        this.valid_responses_ratio = valid_responses_ratio;
    }
}
