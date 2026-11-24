import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tracking event – records one status change of a parcel.
 * Concepts: Comparable interface (for sorting events by time), java.time.
 */
public class TrackingEvent implements Comparable<TrackingEvent> {
    private String waybillNo;
    private LocalDateTime eventTime;
    private String location;
    private String statusCode;
    private String remarks;

    public TrackingEvent(String waybillNo, String location,
                         String statusCode, String remarks) {
        this.waybillNo = waybillNo;
        this.eventTime = LocalDateTime.now();
        this.location = location;
        this.statusCode = statusCode;
        this.remarks = remarks;
    }

    public String getWaybillNo()          { return waybillNo; }
    public LocalDateTime getEventTime()   { return eventTime; }
    public String getLocation()           { return location; }
    public String getStatusCode()         { return statusCode; }
    public String getRemarks()            { return remarks; }

    /** Comparable: sorts events by time (oldest first) */
    @Override
    public int compareTo(TrackingEvent other) {
        return this.eventTime.compareTo(other.eventTime);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return eventTime.format(fmt) + " | " + statusCode + " | " + location;
    }
}
