import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Parcel entity – the central entity of the system.
 * Concepts: OOP, wrapper classes (Integer for nullable fields), java.time API.
 */
public class Parcel {
    private String waybillNo;
    private int senderId;
    private int receiverId;
    private int originFacilityId;
    private int destFacilityId;
    private double weightKg;
    private double lengthCm, widthCm, heightCm;
    private String contentDesc;
    private String serviceTier;         // "Express", "Standard", "Economy"
    private double declaredValue;
    private LocalDate promisedDate;
    private LocalDateTime bookingDate;
    private String currentStatus;       // "BOOKED", "IN_TRANSIT", "DELIVERED", etc.
    private boolean isReturn;
    private Integer currentFacilityId;  // wrapper: null = not inside any facility
    private Integer assignedAgentId;    // wrapper: null = no agent assigned

    public Parcel(String waybillNo, int senderId, int receiverId,
                  int originFacilityId, int destFacilityId,
                  double weightKg, double lengthCm, double widthCm, double heightCm,
                  String contentDesc, String serviceTier, double declaredValue) {
        this.waybillNo = waybillNo;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.originFacilityId = originFacilityId;
        this.destFacilityId = destFacilityId;
        this.weightKg = weightKg;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.heightCm = heightCm;
        this.contentDesc = contentDesc;
        this.serviceTier = serviceTier;
        this.declaredValue = declaredValue;
        this.bookingDate = LocalDateTime.now();
        this.currentStatus = "BOOKED";
        this.isReturn = false;
    }

    // ---- getters ----
    public String getWaybillNo()           { return waybillNo; }
    public int getSenderId()               { return senderId; }
    public int getReceiverId()             { return receiverId; }
    public int getOriginFacilityId()       { return originFacilityId; }
    public int getDestFacilityId()         { return destFacilityId; }
    public double getWeightKg()            { return weightKg; }
    public double getLengthCm()            { return lengthCm; }
    public double getWidthCm()             { return widthCm; }
    public double getHeightCm()            { return heightCm; }
    public String getContentDesc()         { return contentDesc; }
    public String getServiceTier()         { return serviceTier; }
    public double getDeclaredValue()       { return declaredValue; }
    public LocalDate getPromisedDate()     { return promisedDate; }
    public LocalDateTime getBookingDate()  { return bookingDate; }
    public String getCurrentStatus()       { return currentStatus; }
    public boolean isReturn()              { return isReturn; }
    public Integer getCurrentFacilityId()  { return currentFacilityId; }
    public Integer getAssignedAgentId()    { return assignedAgentId; }

    // ---- setters ----
    public void setPromisedDate(LocalDate d)        { this.promisedDate = d; }
    public void setCurrentStatus(String s)          { this.currentStatus = s; }
    public void setIsReturn(boolean r)              { this.isReturn = r; }
    public void setCurrentFacilityId(Integer fid)   { this.currentFacilityId = fid; }
    public void setAssignedAgentId(Integer aid)      { this.assignedAgentId = aid; }

    /** Volumetric weight = (L × W × H) / 5000 */
    public double getVolumetricWeight() {
        return (lengthCm * widthCm * heightCm) / 5000.0;
    }

    /** Chargeable weight = max(actual, volumetric), rounded up to next 0.5 kg */
    public double getChargeableWeight() {
        double max = Math.max(weightKg, getVolumetricWeight());
        return Math.ceil(max * 2) / 2.0;
    }

    @Override
    public String toString() { return waybillNo + " [" + currentStatus + "]"; }
}
