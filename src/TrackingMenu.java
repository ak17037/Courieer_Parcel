import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracking and delivery menu.
 * Concepts: Comparable (TrackingEvent sorting), ArrayList, Collections.sort().
 */
public class TrackingMenu {

    public static void show() {
        new Menu("TRACKING & DELIVERY")
            .add("Track a parcel (full history)",  TrackingMenu::track)
            .add("Check parcel IN at facility",    TrackingMenu::checkIn)
            .add("Check parcel OUT of facility",   TrackingMenu::checkOut)
            .add("Send out for delivery",          TrackingMenu::outForDelivery)
            .add("Mark as DELIVERED",              TrackingMenu::delivered)
            .add("Log an exception / remark",      TrackingMenu::exception)
            .show();
    }

    /** Prints tracking history – uses Comparable sorting */
    public static void printHistory(String wb) {
        List<TrackingEvent> history = DataStore.getTrackingHistory(wb);
        if (history.isEmpty()) { System.out.println("  No tracking events."); return; }
        String[] h = {"Time","Status","Location","Remarks"};
        List<String[]> rows = new ArrayList<>();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TrackingEvent e : history)
            rows.add(new String[]{e.getEventTime().format(fmt), e.getStatusCode(),
                e.getLocation(), e.getRemarks()});
        TablePrinter.print(h, rows);
    }

    static void track() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel " + wb + " not found.");

        Facility at   = p.getCurrentFacilityId()!=null ? DataStore.getFacility(p.getCurrentFacilityId()) : null;
        Facility dest = DataStore.getFacility(p.getDestFacilityId());
        System.out.println("\n  Current status   : " + p.getCurrentStatus());
        System.out.println("  Currently at     : " + (at!=null?at.getName():"(in transit / not at facility)"));
        System.out.println("  Destination      : " + (dest!=null?dest.getName():"-"));
        System.out.println();
        printHistory(wb);
    }

    static void checkIn() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel not found.");
        if ("DELIVERED".equals(p.getCurrentStatus())) throw new Exception("Parcel is already delivered.");
        if (p.getCurrentFacilityId() != null)
            throw new Exception("Still checked in at " + DataStore.getFacility(p.getCurrentFacilityId()).getName());
        int fId = In.integer("Facility ID: ");
        Facility f = DataStore.getFacility(fId);
        if (f == null) throw new Exception("Facility not found.");
        p.setCurrentFacilityId(fId);
        DataStore.addTrackingEvent(new TrackingEvent(wb, f.getName(), "ARRIVED_AT_FACILITY", "Checked in manually"));
        System.out.println("  OK Checked in at " + f.getName() + ".");
    }

    static void checkOut() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel not found.");
        if (p.getCurrentFacilityId() == null) throw new Exception("Parcel is not checked in anywhere.");
        Facility f = DataStore.getFacility(p.getCurrentFacilityId());
        p.setCurrentFacilityId(null);
        DataStore.addTrackingEvent(new TrackingEvent(wb, f.getName(), "DEPARTED_FACILITY", "Checked out manually"));
        System.out.println("  OK Checked out of " + f.getName() + ".");
    }

    static void outForDelivery() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel not found.");
        int destId = p.isReturn() ? p.getOriginFacilityId() : p.getDestFacilityId();
        if (p.getCurrentFacilityId() == null || p.getCurrentFacilityId() != destId)
            throw new Exception("Parcel must be at its destination facility first.");

        List<Employee> agents = new ArrayList<>();
        for (Employee e : DataStore.getEmployeesByFacility(destId))
            if ("Delivery Agent".equals(e.getRole())) agents.add(e);

        String agentName = "agent";
        if (!agents.isEmpty()) {
            System.out.println("Delivery agents:");
            for (Employee a : agents) System.out.println("  " + a.getId() + ". " + a.getName());
            int agentId = In.integer("Agent ID: ");
            Employee agent = DataStore.getEmployee(agentId);
            if (agent != null) { agentName = agent.getName(); p.setAssignedAgentId(agentId); }
        }

        Facility dest = DataStore.getFacility(destId);
        p.setCurrentFacilityId(null);
        DataStore.addTrackingEvent(new TrackingEvent(wb, dest.getName(), "OUT_FOR_DELIVERY", "With agent " + agentName));
        System.out.println("  OK " + wb + " is out for delivery with " + agentName + ".");
    }

    static void delivered() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel not found.");
        if (!"OUT_FOR_DELIVERY".equals(p.getCurrentStatus()))
            throw new Exception("Must be OUT_FOR_DELIVERY first (current: " + p.getCurrentStatus() + ").");

        Payment pay = DataStore.getPayment(wb);
        if (pay != null && !"Completed".equals(pay.getStatus())) {
            System.out.println("  Payment is " + pay.getStatus() + " (Rs " + String.format("%.2f",pay.getAmount()) + ").");
            if (In.yes("Collect payment now (cash on delivery)?")) {
                pay.setStatus("Completed"); pay.setMethod("Cash");
            } else throw new Exception("Cannot deliver until payment is completed.");
        }

        String receivedBy = In.str("Received by (name): ");
        Customer recv = DataStore.getCustomer(p.isReturn() ? p.getSenderId() : p.getReceiverId());
        String status = p.isReturn() ? "RETURNED" : "DELIVERED";
        p.setCurrentStatus(status);
        DataStore.addTrackingEvent(new TrackingEvent(wb,
            recv!=null?recv.getAddress():"-", status,
            (p.isReturn()?"Returned to sender, received by ":"Received by ") + receivedBy));
        System.out.println("  OK " + wb + " " + status + ".");
    }

    static void exception() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        if (DataStore.getParcel(wb) == null) throw new Exception("Parcel not found.");
        String loc    = In.str("Location : ");
        String remark = In.str("Remark   : ");
        DataStore.addTrackingEvent(new TrackingEvent(wb, loc, "EXCEPTION", remark));
        System.out.println("  OK Event logged.");
    }
}
