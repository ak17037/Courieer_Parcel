import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Trip entity – a vehicle journey between two facilities.
 * Concepts: OOP, ArrayList for loaded parcels, wrapper class (Integer for nullable driver).
 */
public class Trip {
    private int id;
    private String regNumber;
    private int sourceFacilityId;
    private int destFacilityId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double distanceKm;
    private Integer driverId;                       // null = no driver yet
    private ArrayList<String> loadedParcels;         // waybill numbers
    private String status;                           // "Scheduled", "In Transit", "Completed"

    public Trip(int id, String regNumber, int sourceFacilityId, int destFacilityId,
                LocalDateTime departureTime, LocalDateTime arrivalTime,
                double distanceKm, Integer driverId) {
        this.id = id;
        this.regNumber = regNumber;
        this.sourceFacilityId = sourceFacilityId;
        this.destFacilityId = destFacilityId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.distanceKm = distanceKm;
        this.driverId = driverId;
        this.loadedParcels = new ArrayList<>();
        this.status = "Scheduled";
    }

    // ---- getters ----
    public int getId()                        { return id; }
    public String getRegNumber()              { return regNumber; }
    public int getSourceFacilityId()          { return sourceFacilityId; }
    public int getDestFacilityId()            { return destFacilityId; }
    public LocalDateTime getDepartureTime()   { return departureTime; }
    public LocalDateTime getArrivalTime()     { return arrivalTime; }
    public double getDistanceKm()             { return distanceKm; }
    public Integer getDriverId()              { return driverId; }
    public ArrayList<String> getLoadedParcels() { return loadedParcels; }
    public String getStatus()                 { return status; }

    // ---- setters ----
    public void setDriverId(Integer d)  { this.driverId = d; }
    public void setStatus(String s)     { this.status = s; }

    public void loadParcel(String wb)   { loadedParcels.add(wb); }

    @Override
    public String toString() { return "Trip " + id + " [" + status + "]"; }
}
