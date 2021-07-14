public class APIResponse implements StatusCommand{
    public final int sat_id;
    public final SatelliteAPI.Status status;
    public final boolean isTimedOut;

    public APIResponse(int sat_id, SatelliteAPI.Status status, boolean isTimedOut) {
        this.sat_id = sat_id;
        this.status = status;
        this.isTimedOut = isTimedOut;
    }
}
