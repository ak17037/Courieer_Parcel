import java.time.LocalDate;
import java.util.*;

/**
 * In-memory data store – replaces the MySQL database.
 *
 * Collections used (from CE-1 syllabus):
 *   HashMap        – fast lookup by key (customers, payments)
 *   TreeMap        – keys kept in sorted order (facilities)
 *   ArrayList      – ordered list (employees, trips, tracking events)
 *   LinkedHashMap  – insertion-order preserved (parcels, vehicles)
 *
 * Multithreading Concepts Used (Threading Syllabus 13-14):
 *   Shared Data & Synchronization:
 *     - Multiple threads (main UI thread, background daemon, concurrent workers)
 *       access this central DataStore concurrently.
 *     - Methods are marked 'synchronized' to acquire the intrinsic lock of DataStore.class.
 *     - Prevents Race Conditions, guarantees Atomicity of compound operations (e.g. ID generation),
 *       and ensures Visibility across CPU caches.
 */
public class DataStore {

    // ========================= COLLECTIONS (SHARED DATA) =========================
    private static HashMap<Integer, Customer>      customers      = new HashMap<>();
    private static TreeMap<Integer, Facility>       facilities     = new TreeMap<>();
    private static ArrayList<Employee>              employees      = new ArrayList<>();
    private static LinkedHashMap<String, Vehicle>   vehicles       = new LinkedHashMap<>();
    private static LinkedHashMap<String, Parcel>    parcels        = new LinkedHashMap<>();
    private static ArrayList<Trip>                  trips          = new ArrayList<>();
    private static HashMap<String, Payment>         payments       = new HashMap<>();   // key = waybillNo
    private static ArrayList<TrackingEvent>          trackingEvents = new ArrayList<>();

    // ========================= ID COUNTERS (CRITICAL SECTIONS) =========================
    private static int nextCustomerId = 6;
    private static int nextFacilityId = 5;
    private static int nextEmployeeId = 9;
    private static int nextTripId     = 1;
    private static int nextReceiptNo  = 1;

    // ========================= PRICING CONSTANTS =========================
    public static final double GST_PCT             = 18.0;
    public static final double FUEL_SURCHARGE_PCT  = 15.0;

    // Rate cards: zone -> array of {maxKg, priceRs}
    private static HashMap<String, double[][]> rateCards       = new HashMap<>();
    // Tier multiplier
    private static HashMap<String, Double>     tierMultipliers = new HashMap<>();
    // Tier transit days: tier -> {local, regional, national}
    private static HashMap<String, int[]>      tierDays        = new HashMap<>();

    // ========================= SAMPLE DATA =========================
    static { loadSampleData(); }

    private static synchronized void loadSampleData() {

        // --- Customers ---
        addCustomer(new Customer(1, "Arun",   "Sharma", "arun@gmail.com",   "123 MG Road, Chandigarh, Punjab"));
        customers.get(1).addPhone("9876543210");
        addCustomer(new Customer(2, "Priya",  "Verma",  "priya@gmail.com",  "456 Mall Road, Ludhiana, Punjab"));
        customers.get(2).addPhone("9876543211");
        addCustomer(new Customer(3, "Rahul",  "Gupta",  "rahul@gmail.com",  "789 Civil Lines, Delhi, Delhi"));
        customers.get(3).addPhone("9876543212");
        customers.get(3).addPhone("9876543213");
        addCustomer(new Customer(4, "Sneha",  "Patel",  "sneha@gmail.com",  "101 SG Highway, Ahmedabad, Gujarat"));
        customers.get(4).addPhone("9876543214");
        addCustomer(new Customer(5, "Vikram", "Singh",  "vikram@gmail.com", "202 Sector 17, Chandigarh, Punjab"));
        customers.get(5).addPhone("9876543215");

        // --- Facilities ---
        addFacility(new Facility(1, "Chandigarh Hub",     "Sector 26, Chandigarh, Punjab",    "Hub",    "North"));
        addFacility(new Facility(2, "Delhi Hub",          "Connaught Place, Delhi, Delhi",     "Hub",    "North"));
        addFacility(new Facility(3, "Ludhiana Branch",    "Ferozepur Road, Ludhiana, Punjab",  "Branch", "North"));
        addFacility(new Facility(4, "Ahmedabad Branch",   "SG Highway, Ahmedabad, Gujarat",    "Branch", "West"));

        // --- Employees ---
        addEmployee(new Employee(1, "Rajesh Kumar",    "Manager",        null,             1));
        addEmployee(new Employee(2, "Amit Thakur",     "Branch Staff",   null,             1));
        addEmployee(new Employee(3, "Sunil Verma",     "Delivery Agent", null,             1));
        addEmployee(new Employee(4, "Deepak Chauhan",  "Driver",         "DL1234567890",   1));
        addEmployee(new Employee(5, "Pooja Sharma",    "Manager",        null,             2));
        addEmployee(new Employee(6, "Rohit Jain",      "Delivery Agent", null,             2));
        addEmployee(new Employee(7, "Naveen Singh",    "Driver",         "DL0987654321",   2));
        addEmployee(new Employee(8, "Meera Devi",      "Branch Staff",   null,             3));

        // --- Vehicles ---
        addVehicle(new Vehicle("PB10AB1234", "Tata Ace",             750));
        addVehicle(new Vehicle("DL01CD5678", "Mahindra Bolero",     1200));
        addVehicle(new Vehicle("PB02EF9012", "Ashok Leyland Dost",  1500));
        addVehicle(new Vehicle("GJ01GH3456", "Eicher Pro",          3000));

        // --- Rate cards ---
        rateCards.put("LOCAL",    new double[][]{{1,50},{5,100},{10,180},{25,350},{50,600}});
        rateCards.put("REGIONAL", new double[][]{{1,80},{5,160},{10,280},{25,500},{50,900}});
        rateCards.put("NATIONAL", new double[][]{{1,120},{5,250},{10,450},{25,800},{50,1400}});

        // --- Service tier config ---
        tierMultipliers.put("Express",  2.0);
        tierMultipliers.put("Standard", 1.0);
        tierMultipliers.put("Economy",  0.75);

        tierDays.put("Express",  new int[]{1, 2, 3});
        tierDays.put("Standard", new int[]{2, 4, 7});
        tierDays.put("Economy",  new int[]{3, 7, 10});
    }

    // ========================= CUSTOMER OPS (SYNCHRONIZED) =========================
    public static synchronized void    addCustomer(Customer c)        { customers.put(c.getId(), c); }
    public static synchronized Customer getCustomer(int id)           { return customers.get(id); }
    public static synchronized Collection<Customer> getAllCustomers()  { return new ArrayList<>(customers.values()); }
    public static synchronized int     getNextCustomerId()            { return nextCustomerId++; }
    public static synchronized boolean removeCustomer(int id)         { return customers.remove(id) != null; }

    public static synchronized List<Customer> searchCustomers(String query) {
        String q = query.toLowerCase();
        List<Customer> out = new ArrayList<>();
        for (Customer c : customers.values()) {
            if (c.getFullName().toLowerCase().contains(q)) { out.add(c); continue; }
            for (String ph : c.getPhones())
                if (ph.contains(query)) { out.add(c); break; }
        }
        return out;
    }

    // ========================= FACILITY OPS (SYNCHRONIZED) =========================
    public static synchronized void     addFacility(Facility f)         { facilities.put(f.getId(), f); }
    public static synchronized Facility  getFacility(int id)            { return facilities.get(id); }
    public static synchronized Collection<Facility> getAllFacilities()   { return new ArrayList<>(facilities.values()); }
    public static synchronized int      getNextFacilityId()             { return nextFacilityId++; }

    // ========================= EMPLOYEE OPS (SYNCHRONIZED) =========================
    public static synchronized void addEmployee(Employee e)             { employees.add(e); }
    public static synchronized Employee getEmployee(int id) {
        for (Employee e : employees) if (e.getId() == id) return e;
        return null;
    }
    public static synchronized List<Employee> getAllEmployees()          { return new ArrayList<>(employees); }
    public static synchronized int  getNextEmployeeId()                 { return nextEmployeeId++; }

    public static synchronized List<Employee> getEmployeesByFacility(int fid) {
        List<Employee> out = new ArrayList<>();
        for (Employee e : employees) if (e.getFacilityId() == fid) out.add(e);
        return out;
    }
    public static synchronized boolean removeEmployee(int id) { return employees.removeIf(e -> e.getId() == id); }

    // ========================= VEHICLE OPS (SYNCHRONIZED) =========================
    public static synchronized void     addVehicle(Vehicle v)           { vehicles.put(v.getRegNumber(), v); }
    public static synchronized Vehicle   getVehicle(String reg)         { return vehicles.get(reg); }
    public static synchronized Collection<Vehicle> getAllVehicles()      { return new ArrayList<>(vehicles.values()); }

    // ========================= PARCEL OPS (SYNCHRONIZED) =========================
    public static synchronized void    addParcel(Parcel p)              { parcels.put(p.getWaybillNo(), p); }
    public static synchronized Parcel   getParcel(String wb)            { return parcels.get(wb); }
    public static synchronized Collection<Parcel> getAllParcels()        { return new ArrayList<>(parcels.values()); }

    // ========================= TRIP OPS (SYNCHRONIZED) =========================
    public static synchronized void  addTrip(Trip t)                    { trips.add(t); }
    public static synchronized Trip   getTrip(int id) {
        for (Trip t : trips) if (t.getId() == id) return t;
        return null;
    }
    public static synchronized List<Trip> getAllTrips()                  { return new ArrayList<>(trips); }
    public static synchronized int   getNextTripId()                    { return nextTripId++; }

    // ========================= PAYMENT OPS (SYNCHRONIZED) =========================
    public static synchronized void    addPayment(Payment p)            { payments.put(p.getWaybillNo(), p); }
    public static synchronized Payment  getPayment(String wb)           { return payments.get(wb); }
    public static synchronized Collection<Payment> getAllPayments()      { return new ArrayList<>(payments.values()); }
    public static synchronized int     getNextReceiptNo()               { return nextReceiptNo++; }

    // ========================= TRACKING OPS (SYNCHRONIZED) =========================
    public static synchronized void addTrackingEvent(TrackingEvent e) {
        trackingEvents.add(e);
        Parcel p = getParcel(e.getWaybillNo());
        if (p != null) p.setCurrentStatus(e.getStatusCode());
    }

    public static synchronized List<TrackingEvent> getTrackingHistory(String wb) {
        List<TrackingEvent> h = new ArrayList<>();
        for (TrackingEvent e : trackingEvents)
            if (e.getWaybillNo().equals(wb)) h.add(e);
        Collections.sort(h);           // uses Comparable (TrackingEvent)
        return h;
    }

    public static synchronized String getLatestStatus(String wb) {
        List<TrackingEvent> h = getTrackingHistory(wb);
        return h.isEmpty() ? null : h.get(h.size() - 1).getStatusCode();
    }

    // ========================= PRICING =========================
    /** Same facility = LOCAL, same region = REGIONAL, else NATIONAL */
    public static String determineZone(int originId, int destId) {
        if (originId == destId) return "LOCAL";
        Facility a = getFacility(originId), b = getFacility(destId);
        if (a != null && b != null && a.getRegion().equals(b.getRegion())) return "REGIONAL";
        return "NATIONAL";
    }

    /** Slab-based rate × tier multiplier */
    public static double calculateRate(String zone, double chargeableKg, String tier) {
        double[][] slabs = rateCards.getOrDefault(zone, rateCards.get("NATIONAL"));
        double base = slabs[slabs.length - 1][1];
        for (double[] slab : slabs) if (chargeableKg <= slab[0]) { base = slab[1]; break; }
        return Math.round(base * tierMultipliers.getOrDefault(tier, 1.0) * 100.0) / 100.0;
    }

    /** base + fuel surcharge + GST */
    public static double calculateTotal(double baseRate) {
        double fuel     = baseRate * FUEL_SURCHARGE_PCT / 100.0;
        double subtotal = baseRate + fuel;
        double gst      = subtotal * GST_PCT / 100.0;
        return Math.round((subtotal + gst) * 100.0) / 100.0;
    }

    /** Promised delivery date from tier + zone */
    public static LocalDate getPromisedDate(String tier, String zone) {
        int[] d = tierDays.getOrDefault(tier, tierDays.get("Standard"));
        int days = switch (zone) { case "LOCAL" -> d[0]; case "REGIONAL" -> d[1]; default -> d[2]; };
        return LocalDate.now().plusDays(days);
    }

    /** Parcels currently checked in at a facility */
    public static synchronized List<Parcel> getParcelsAtFacility(int fid) {
        List<Parcel> out = new ArrayList<>();
        for (Parcel p : parcels.values())
            if (p.getCurrentFacilityId() != null && p.getCurrentFacilityId() == fid) out.add(p);
        return out;
    }

    // ========================= SYLLABUS TOPIC 13-14: RACE CONDITION DEMO =========================
    // Demonstrates race condition vs critical section synchronization on shared data.
    private static int unsafeCounter = 0;
    private static int safeCounter   = 0;
    private static final Object lockObject = new Object(); // Intrinsic lock object

    public static void resetDemoCounters() {
        unsafeCounter = 0;
        safeCounter   = 0;
    }

    /**
     * Unsynchronized increment.
     * count++ is NOT atomic; it consists of: 1) Read count, 2) Add 1, 3) Write back.
     * When two threads run this concurrently, updates are lost -> Race Condition!
     */
    public static void unsafeIncrement() {
        unsafeCounter++;
    }

    /**
     * Synchronized method increment.
     * Acquires intrinsic lock of DataStore.class, ensuring mutual exclusion,
     * atomicity, and memory visibility across CPU cores.
     */
    public static synchronized void safeIncrementMethod() {
        safeCounter++;
    }

    /**
     * Synchronized block increment.
     * Also synchronizes on DataStore.class.
     * Rule: To guarantee mutual exclusion, all critical sections accessing
     * the same shared variable must synchronize on the SAME monitor lock!
     */
    public static void safeIncrementBlock() {
        synchronized (DataStore.class) {
            safeCounter++;
        }
    }

    public static int getUnsafeCounter() { return unsafeCounter; }
    public static int getSafeCounter()   { return safeCounter; }
}
