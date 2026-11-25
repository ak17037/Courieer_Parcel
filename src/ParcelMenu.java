import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Parcel booking and management.
 * Concepts: pricing logic, wrapper classes, String formatting, collections.
 */
public class ParcelMenu {

    public static void show() {
        new Menu("PARCELS")
            .add("Book a new parcel",                     ParcelMenu::book)
            .add("View all parcels",                      ParcelMenu::list)
            .add("View parcel details",                   ParcelMenu::details)
            .add("Cancel a parcel (BOOKED only)",         ParcelMenu::cancel)
            .show();
    }

    static void book() throws Exception {
        System.out.println("Sender & receiver must be registered customers.");

        int senderId = In.integer("Sender   Customer ID: ");
        Customer sender = DataStore.getCustomer(senderId);
        if (sender == null) throw new Exception("Customer " + senderId + " not found.");
        System.out.println("    -> " + sender);

        int receiverId = In.integer("Receiver Customer ID: ");
        Customer receiver = DataStore.getCustomer(receiverId);
        if (receiver == null) throw new Exception("Customer " + receiverId + " not found.");
        if (senderId == receiverId) throw new Exception("Sender and receiver must be different.");
        System.out.println("    -> " + receiver);

        showFacilities();
        int originId = In.integer("Origin facility ID     : ");
        if (DataStore.getFacility(originId) == null) throw new Exception("Facility not found.");
        int destId   = In.integer("Destination facility ID: ");
        if (DataStore.getFacility(destId) == null) throw new Exception("Facility not found.");

        double weight = In.decimal("Weight (kg)  : ");
        if (weight <= 0) throw new Exception("Weight must be > 0.");
        double length = In.decimal("Length (cm)  : ");
        double width  = In.decimal("Width  (cm)  : ");
        double height = In.decimal("Height (cm)  : ");
        if (length <= 0 || width <= 0 || height <= 0) throw new Exception("All dimensions must be > 0.");
        String content = In.opt("Content description : ");

        System.out.println("Service tier:");
        String tier = In.choice("Choose: ", "Express", "Standard", "Economy");
        double declared = In.decimal("Declared value Rs (0 = no insurance) : ");

        // ---- pricing ----
        String zone       = DataStore.determineZone(originId, destId);
        double volumetric = (length * width * height) / 5000.0;
        double chargeable = Math.ceil(Math.max(weight, volumetric) * 2) / 2.0;
        double baseRate   = DataStore.calculateRate(zone, chargeable, tier);
        double fuel       = baseRate * DataStore.FUEL_SURCHARGE_PCT / 100.0;
        double subtotal   = baseRate + fuel;
        double gst        = subtotal * DataStore.GST_PCT / 100.0;
        double total      = Math.round((subtotal + gst) * 100.0) / 100.0;
        LocalDate promised = DataStore.getPromisedDate(tier, zone);

        System.out.printf("%n  QUOTE: zone %s, volumetric %.2f kg, chargeable %.1f kg%n",
                zone, volumetric, chargeable);
        System.out.printf("  %-18s Rs %10.2f%n", "Base freight",  baseRate);
        System.out.printf("  %-18s Rs %10.2f%n", "Fuel surcharge", fuel);
        System.out.printf("  %-18s Rs %10.2f%n", "GST ("+DataStore.GST_PCT+"%)", gst);
        System.out.printf("  %-18s Rs %10.2f%n", "TOTAL", total);
        System.out.println("  Promised delivery   " + promised);

        if (!In.yes("Book at Rs " + String.format("%.2f", total) + "?")) {
            System.out.println("  (not booked)"); return;
        }

        System.out.println("Payment method:");
        String method  = In.choice("Choose: ", "Cash", "Card", "UPI", "Net Banking");
        boolean paidNow = In.yes("Has the customer paid now?");

        // ---- save ----
        String wb = WaybillGenerator.next();
        Parcel p = new Parcel(wb, senderId, receiverId, originId, destId,
                              weight, length, width, height, content, tier, declared);
        p.setPromisedDate(promised);
        p.setCurrentFacilityId(originId);
        DataStore.addParcel(p);

        Payment pay = new Payment(DataStore.getNextReceiptNo(), wb, total, method,
                                   paidNow ? "Completed" : "Pending");
        DataStore.addPayment(pay);

        Facility origin = DataStore.getFacility(originId);
        DataStore.addTrackingEvent(new TrackingEvent(wb, origin.getName(),
                "BOOKED", "Parcel booked at counter (" + tier + ")"));

        System.out.println("\n  OK PARCEL BOOKED   Waybill No: " + wb);
        System.out.println("  Payment           : Rs " + String.format("%.2f", total)
                + "  " + method + " - " + (paidNow ? "Completed" : "Pending"));
        System.out.println("  Promised delivery : " + promised);
    }

    static void list() {
        String[] h = {"Waybill","Sender","Receiver","Origin","Destination",
                      "Weight","Amount","Payment","Status"};
        List<String[]> rows = new ArrayList<>();
        for (Parcel p : DataStore.getAllParcels()) {
            Customer s = DataStore.getCustomer(p.getSenderId());
            Customer r = DataStore.getCustomer(p.getReceiverId());
            Facility o = DataStore.getFacility(p.getOriginFacilityId());
            Facility d = DataStore.getFacility(p.getDestFacilityId());
            Payment pay = DataStore.getPayment(p.getWaybillNo());
            rows.add(new String[]{p.getWaybillNo(),
                s!=null?s.getFullName():"-", r!=null?r.getFullName():"-",
                o!=null?o.getName():"-", d!=null?d.getName():"-",
                String.format("%.1f",p.getWeightKg()),
                pay!=null?String.format("%.2f",pay.getAmount()):"-",
                pay!=null?pay.getStatus():"-", p.getCurrentStatus()});
        }
        TablePrinter.print(h, rows);
    }

    private static void showFacilities() {
        String[] h = {"ID","Facility","Type","Region"};
        List<String[]> rows = new ArrayList<>();
        for (Facility f : DataStore.getAllFacilities())
            rows.add(new String[]{String.valueOf(f.getId()),f.getName(),f.getType(),f.getRegion()});
        TablePrinter.print(h, rows);
    }

    static void details() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel " + wb + " not found.");

        Customer s = DataStore.getCustomer(p.getSenderId());
        Customer r = DataStore.getCustomer(p.getReceiverId());
        Facility o = DataStore.getFacility(p.getOriginFacilityId());
        Facility d = DataStore.getFacility(p.getDestFacilityId());

        System.out.println("\nPARCEL");
        System.out.println("  Waybill      : " + wb);
        System.out.println("  Sender       : " + (s!=null?s.getFullName():"-"));
        System.out.println("  Receiver     : " + (r!=null?r.getFullName():"-"));
        System.out.println("  Origin       : " + (o!=null?o.getName():"-"));
        System.out.println("  Destination  : " + (d!=null?d.getName():"-"));
        System.out.printf( "  Weight       : %.1f kg (volumetric %.2f kg)%n",
                p.getWeightKg(), p.getVolumetricWeight());
        System.out.println("  Dimensions   : "+p.getLengthCm()+"x"+p.getWidthCm()+"x"+p.getHeightCm()+" cm");
        System.out.println("  Content      : " + (p.getContentDesc()==null?"-":p.getContentDesc()));
        System.out.println("  Service      : " + p.getServiceTier());
        System.out.println("  Booked       : " + p.getBookingDate().toLocalDate());
        System.out.println("  Promised     : " + p.getPromisedDate());
        System.out.println("  Status       : " + p.getCurrentStatus());

        Payment pay = DataStore.getPayment(wb);
        System.out.println("\nPAYMENT");
        if (pay != null)
            System.out.println("  Receipt #" + pay.getReceiptNo() + " | Rs "
                + String.format("%.2f", pay.getAmount()) + " | " + pay.getMethod() + " | " + pay.getStatus());
        else System.out.println("  (none)");

        System.out.println("\nTRACKING HISTORY");
        TrackingMenu.printHistory(wb);
    }

    static void cancel() throws Exception {
        String wb = In.str("Waybill No to cancel: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel " + wb + " not found.");
        if (!"BOOKED".equals(p.getCurrentStatus()))
            throw new Exception("Only BOOKED parcels can be cancelled (current: " + p.getCurrentStatus() + ").");
        if (!In.yes("Cancel parcel " + wb + "?")) return;
        p.setCurrentStatus("CANCELLED");
        DataStore.addTrackingEvent(new TrackingEvent(wb, "-", "CANCELLED", "Cancelled by user"));
        System.out.println("  OK Parcel " + wb + " cancelled.");
    }
}
