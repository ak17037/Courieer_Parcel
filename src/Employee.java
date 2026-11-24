/**
 * Employee entity.
 * Concepts: OOP (encapsulation, constructors), wrapper class (Integer for nullable facilityId).
 */
public class Employee {
    private int id;
    private String name;
    private String role;            // "Manager", "Branch Staff", "Delivery Agent", "Driver"
    private String licenseNumber;   // required for Drivers, optional for others
    private int facilityId;

    public Employee(int id, String name, String role, String licenseNumber, int facilityId) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.licenseNumber = licenseNumber;
        this.facilityId = facilityId;
    }

    public int getId()                 { return id; }
    public String getName()            { return name; }
    public String getRole()            { return role; }
    public String getLicenseNumber()   { return licenseNumber; }
    public int getFacilityId()         { return facilityId; }
    public void setFacilityId(int fid) { this.facilityId = fid; }

    @Override
    public String toString() { return name + " - " + role; }
}
