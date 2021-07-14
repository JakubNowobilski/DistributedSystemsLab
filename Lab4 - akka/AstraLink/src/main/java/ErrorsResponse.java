public class ErrorsResponse implements StatusCommand{
    public final int sat_id;
    public final int errors_count;

    public ErrorsResponse(int sat_id, int errors_count) {
        this.sat_id = sat_id;
        this.errors_count = errors_count;
    }
}
