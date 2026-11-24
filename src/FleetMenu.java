import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Vehicle and Trip management.
 * Concepts: LinkedHashMap, ArrayList, exception handling, date/time parsing.
 */
public class FleetMenu {

    public static void show() {
        new Menu("VEHICLES & TRIPS")
            .add("Add vehicle",                        FleetMenu::addVehicle)
            .add("View vehicles",                      FleetMenu::listVehicles)
            .add("Change vehicle maintenance status",  FleetMenu::maintenance)
            .add("Schedule a trip",                    FleetMenu::scheduleTrip)
            .add("View trips",                         FleetMenu::listTrips)
            .add("Load parcel onto trip",              FleetMenu::loadParcel)
            .add("Dispatch trip (depart)",             FleetMenu::dispatch)
            .add("Complete trip (arrive & unload)",    FleetMenu::arrive)
            .add("Assign driver to trip",              FleetMenu::assignDriver)
            .show();
    }

    // ============================= vehicles =============================

    static void addVehicle() throws Exception {
        String reg  = In.str("Registration number: ").toUpperCase();
        if (DataStore.getVehicle(reg) != null) throw new Exception("Vehicle " + reg + " already exists.");
        String model = In.str("Model        : ");
        double cap   = In.decimal("Capacity (kg): ");
        if (cap <= 0) throw new Exception("Capacity must be > 0.");
        DataStore.addVehicle(new Vehicle(reg, model, cap));
        System.out.println("  OK Vehicle added.");
    }

    static void listVehicles() {
        String[] h = {"Reg Number","Model","Capacity (kg)","Status"};
        List<String[]> rows = new ArrayList<>();
        for (Vehicle v : DataStore.getAllVehicles())
            rows.add(new String[]{v.getRegNumber(), v.getModel(),
                String.format("%.0f",v.getCapacityKg()), v.getMaintenanceStatus()});
        TablePrinter.print(h, rows);
    }

    static void maintenance() throws Exception {
        String reg = In.str("Registration number: ").toUpperCase();
        Vehicle v = DataStore.getVehicle(reg);
        if (v == null) throw new Exception("Vehicle not found.");
        System.out.println("    -> " + v);
        System.out.println("New status:");
        String st = In.choice("Choose: ", "Operational", "Under Maintenance");
        v.setMaintenanceStatus(st);
        System.out.println("  OK Vehicle " + reg + " is now " + st + ".");
    }

    // ============================= trips =============================

    static void scheduleTrip() throws Exception {
        listVehicles();
        String reg = In.str("Vehicle registration: ").toUpperCase();
        Vehicle v = DataStore.getVehicle(reg);
        if (v == null) throw new Exception("Vehicle not found.");
        if (!"Operational".equals(v.getMaintenanceStatus()))
            throw new Exception("Vehicle " + reg + " is Under Maintenance.");

        FacilityMenu.listFacilities();
        int src = In.integer("Source facility ID     : ");
        if (DataStore.getFacility(src) == null) throw new Exception("Facility not found.");
        int dst = In.integer("Destination facility ID: ");
        if (DataStore.getFacility(dst) == null) throw new Exception("Facility not found.");
        if (src == dst) throw new Exception("Source and destination must be different.");

        LocalDateTime dep = In.dateTime("Departure time");
        LocalDateTime arr = In.dateTime("Arrival time  ");
        if (!arr.isAfter(dep)) throw new Exception("Arrival must be after departure.");
        double km = In.decimal("Distance (km) : ");
        if (km <= 0) throw new Exception("Distance must be > 0.");

        // optional driver
        List<Employee> drivers = new ArrayList<>();
        for (Employee e : DataStore.getAllEmployees())
            if ("Driver".equals(e.getRole())) drivers.add(e);
        Integer driverId = null;
        if (!drivers.isEmpty()) {
            System.out.println("Available drivers:");
            for (Employee d : drivers) System.out.println("  " + d.getId() + ". " + d.getName());
            String d = In.opt("Driver ID (blank = assign later): ");
            if (d != null) {
                try { driverId = Integer.parseInt(d.trim()); }
                catch (NumberFormatException e) { throw new Exception("Driver ID must be a number."); }
                if (DataStore.getEmployee(driverId) == null) throw new Exception("Employee not found.");
            }
        }

        int id = DataStore.getNextTripId();
        DataStore.addTrip(new Trip(id, reg, src, dst, dep, arr, km, driverId));
        System.out.println("  OK Trip scheduled with Trip_ID = " + id);
    }

    static void listTrips() {
        String[] h = {"ID","Vehicle","From","To","Departure","Arrival","Km","Parcels","Status"};
        List<String[]> rows = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Trip t : DataStore.getAllTrips()) {
            Facility s = DataStore.getFacility(t.getSourceFacilityId());
            Facility d = DataStore.getFacility(t.getDestFacilityId());
            rows.add(new String[]{String.valueOf(t.getId()), t.getRegNumber(),
                s!=null?s.getName():"-", d!=null?d.getName():"-",
                t.getDepartureTime().format(fmt), t.getArrivalTime().format(fmt),
                String.format("%.0f",t.getDistanceKm()),
                String.valueOf(t.getLoadedParcels().size()), t.getStatus()});
        }
        TablePrinter.print(h, rows);
    }

    static void loadParcel() throws Exception {
        int tripId = In.integer("Trip ID: ");
        Trip trip = DataStore.getTrip(tripId);
        if (trip == null) throw new Exception("Trip not found.");
        if (!"Scheduled".equals(trip.getStatus()))
            throw new Exception("Can only load parcels on Scheduled trips (current: " + trip.getStatus() + ").");

        int src = trip.getSourceFacilityId();
        List<Parcel> avail = DataStore.getParcelsAtFacility(src);
        if (avail.isEmpty()) {
            System.out.println("  No parcels at " + DataStore.getFacility(src).getName()); return;
        }
        System.out.println("Parcels at " + DataStore.getFacility(src).getName() + ":");
        for (Parcel p : avail) System.out.println("  " + p.getWaybillNo() + " - " + p.getWeightKg() + " kg");

        String wb = In.str("Waybill No to load: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel not found.");
        if (p.getCurrentFacilityId() == null || p.getCurrentFacilityId() != src)
            throw new Exception("Parcel is not at this trip's source facility.");
        if (trip.getLoadedParcels().contains(wb))
            throw new Exception("Parcel already loaded on this trip.");

        // capacity check
        double loaded = 0;
        for (String w : trip.getLoadedParcels()) {
            Parcel lp = DataStore.getParcel(w);
            if (lp != null) loaded += lp.getWeightKg();
        }
        Vehicle veh = DataStore.getVehicle(trip.getRegNumber());
        double remaining = veh.getCapacityKg() - loaded;
        if (p.getWeightKg() > remaining)
            throw new Exception("Not enough capacity. Remaining: " + String.format("%.1f",remaining)
                + " kg, parcel: " + p.getWeightKg() + " kg.");

        trip.loadParcel(wb);
        remaining -= p.getWeightKg();
        System.out.println("  OK " + wb + " loaded on trip " + tripId
                + ". Remaining capacity: " + String.format("%.1f", remaining) + " kg.");
    }

    static void dispatch() throws Exception {
        int tripId = In.integer("Trip ID: ");
        Trip trip = DataStore.getTrip(tripId);
        if (trip == null) throw new Exception("Trip not found.");
        if (trip.getLoadedParcels().isEmpty()) throw new Exception("No parcels loaded on this trip.");
        if (!"Scheduled".equals(trip.getStatus())) throw new Exception("Trip is already " + trip.getStatus() + ".");

        Facility src = DataStore.getFacility(trip.getSourceFacilityId());
        trip.setStatus("In Transit");
        for (String wb : trip.getLoadedParcels()) {
            Parcel p = DataStore.getParcel(wb);
            if (p == null) continue;
            p.setCurrentFacilityId(null);
            DataStore.addTrackingEvent(new TrackingEvent(wb, src.getName(),
                "DEPARTED_FACILITY", "Departed on trip " + tripId + " (" + trip.getRegNumber() + ")"));
        }
        System.out.println("  OK Trip " + tripId + " dispatched with " + trip.getLoadedParcels().size() + " parcel(s).");
    }

    static void arrive() throws Exception {
        int tripId = In.integer("Trip ID: ");
        Trip trip = DataStore.getTrip(tripId);
        if (trip == null) throw new Exception("Trip not found.");
        if (!"In Transit".equals(trip.getStatus()))
            throw new Exception("Trip must be In Transit (current: " + trip.getStatus() + ").");

        int dst = trip.getDestFacilityId();
        Facility destFac = DataStore.getFacility(dst);
        trip.setStatus("Completed");

        List<String> loaded = new ArrayList<>(trip.getLoadedParcels());
        for (String wb : loaded) {
            Parcel p = DataStore.getParcel(wb);
            if (p == null) continue;
            p.setCurrentFacilityId(dst);
            DataStore.addTrackingEvent(new TrackingEvent(wb, destFac.getName(),
                "ARRIVED_AT_FACILITY", "Unloaded from trip " + tripId));
        }
        System.out.println("  OK Trip " + tripId + " arrived at " + destFac.getName()
                + "; " + loaded.size() + " parcel(s) checked in.");
    }

    static void assignDriver() throws Exception {
        int tripId = In.integer("Trip ID: ");
        Trip trip = DataStore.getTrip(tripId);
        if (trip == null) throw new Exception("Trip not found.");

        System.out.println("Available drivers:");
        for (Employee e : DataStore.getAllEmployees())
            if ("Driver".equals(e.getRole())) System.out.println("  " + e.getId() + ". " + e.getName());
        int empId = In.integer("Driver employee ID: ");
        Employee emp = DataStore.getEmployee(empId);
        if (emp == null) throw new Exception("Employee not found.");
        if (!"Driver".equals(emp.getRole()))
            throw new Exception(emp.getName() + " is not a Driver (role: " + emp.getRole() + ").");
        trip.setDriverId(empId);
        System.out.println("  OK " + emp.getName() + " assigned to trip " + tripId + ".");
    }
}
