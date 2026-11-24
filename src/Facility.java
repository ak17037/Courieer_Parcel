/**
 * Facility entity (Hub or Branch).
 * Concepts: OOP (encapsulation, constructors), String usage.
 */
public class Facility {
    private int id;
    private String name;
    private String address;
    private String type;     // "Hub" or "Branch"
    private String region;

    public Facility(int id, String name, String address, String type, String region) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.region = region;
    }

    public int getId()          { return id; }
    public String getName()     { return name; }
    public String getAddress()  { return address; }
    public String getType()     { return type; }
    public String getRegion()   { return region; }

    @Override
    public String toString() { return name + " [" + type + "]"; }
}
