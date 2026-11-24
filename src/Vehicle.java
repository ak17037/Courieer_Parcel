/**
 * Vehicle entity.
 * Concepts: OOP, wrapper class usage (Double for capacity).
 */
public class Vehicle {
    private String regNumber;
    private String model;
    private double capacityKg;
    private String maintenanceStatus;   // "Operational" or "Under Maintenance"

    public Vehicle(String regNumber, String model, double capacityKg) {
        this.regNumber = regNumber;
        this.model = model;
        this.capacityKg = capacityKg;
        this.maintenanceStatus = "Operational";
    }

    public String getRegNumber()          { return regNumber; }
    public String getModel()              { return model; }
    public double getCapacityKg()         { return capacityKg; }
    public String getMaintenanceStatus()  { return maintenanceStatus; }
    public void setMaintenanceStatus(String s) { this.maintenanceStatus = s; }

    @Override
    public String toString() { return model + ", " + capacityKg + " kg, " + maintenanceStatus; }
}
