import java.util.*;

/**
 * Reports and analytics using Java Collections.
 * Concepts: HashMap aggregation, Comparator sorting, TreeMap for sorted keys.
 */
public class ReportMenu {

    public static void show() {
        new Menu("REPORTS & QUERIES")
            .add("Parcel status summary",                ReportMenu::statusSummary)
            .add("Revenue by payment method",            ReportMenu::revenue)
            .add("Facility workload (parcels at each)",  ReportMenu::facilityLoad)
            .add("Top customers by parcels sent",        ReportMenu::topCustomers)
            .add("Parcels with volumetric > actual",     ReportMenu::volumetric)
            .add("Data summary (record counts)",         ReportMenu::counts)
            .show();
    }

    /** Groups parcels by status using HashMap – demonstrates aggregation */
    static void statusSummary() {
        HashMap<String, Integer> map = new HashMap<>();
        for (Parcel p : DataStore.getAllParcels())
            map.merge(p.getCurrentStatus(), 1, Integer::sum);

        // sort descending by count using Comparator
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(map.entrySet());
        sorted.sort(Comparator.<Map.Entry<String, Integer>, Integer>comparing(Map.Entry::getValue).reversed());

        String[] h = {"Status","Count"};
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : sorted)
            rows.add(new String[]{e.getKey(), String.valueOf(e.getValue())});
        TablePrinter.print(h, rows);
    }

    /** Revenue breakdown – uses TreeMap for alphabetically sorted methods */
    static void revenue() {
        TreeMap<String, double[]> byMethod = new TreeMap<>();
        for (Payment pay : DataStore.getAllPayments()) {
            double[] t = byMethod.computeIfAbsent(pay.getMethod(), k -> new double[3]);
            switch (pay.getStatus()) {
                case "Completed" -> t[0] += pay.getAmount();
                case "Pending"   -> t[1] += pay.getAmount();
                case "Failed"    -> t[2] += pay.getAmount();
            }
        }
        String[] h = {"Method","Collected Rs","Pending Rs","Failed Rs"};
        List<String[]> rows = new ArrayList<>();
        double[] grand = new double[3];
        for (Map.Entry<String, double[]> e : byMethod.entrySet()) {
            double[] t = e.getValue();
            rows.add(new String[]{e.getKey(), fmt(t[0]), fmt(t[1]), fmt(t[2])});
            grand[0]+=t[0]; grand[1]+=t[1]; grand[2]+=t[2];
        }
        rows.add(new String[]{"TOTAL", fmt(grand[0]), fmt(grand[1]), fmt(grand[2])});
        TablePrinter.print(h, rows);
    }

    static void facilityLoad() {
        String[] h = {"Facility","Type","Parcels Inside"};
        List<String[]> rows = new ArrayList<>();
        for (Facility f : DataStore.getAllFacilities())
            rows.add(new String[]{f.getName(), f.getType(),
                String.valueOf(DataStore.getParcelsAtFacility(f.getId()).size())});
        TablePrinter.print(h, rows);
    }

    /** Top customers sorted by parcels sent – demonstrates Comparator */
    static void topCustomers() {
        HashMap<Integer, Integer> count = new HashMap<>();
        HashMap<Integer, Double>  value = new HashMap<>();
        for (Parcel p : DataStore.getAllParcels()) {
            int sid = p.getSenderId();
            count.merge(sid, 1, Integer::sum);
            Payment pay = DataStore.getPayment(p.getWaybillNo());
            if (pay != null) value.merge(sid, pay.getAmount(), Double::sum);
        }
        List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(count.entrySet());
        sorted.sort(Comparator.<Map.Entry<Integer, Integer>, Integer>comparing(Map.Entry::getValue).reversed());

        String[] h = {"Customer","Parcels Sent","Total Billed Rs"};
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : sorted) {
            Customer c = DataStore.getCustomer(e.getKey());
            rows.add(new String[]{c!=null?c.getFullName():"ID "+e.getKey(),
                String.valueOf(e.getValue()), fmt(value.getOrDefault(e.getKey(), 0.0))});
        }
        TablePrinter.print(h, rows);
    }

    static void volumetric() {
        String[] h = {"Waybill","Actual kg","Volumetric kg","Chargeable kg"};
        List<String[]> rows = new ArrayList<>();
        for (Parcel p : DataStore.getAllParcels())
            if (p.getVolumetricWeight() > p.getWeightKg())
                rows.add(new String[]{p.getWaybillNo(), String.format("%.1f",p.getWeightKg()),
                    String.format("%.2f",p.getVolumetricWeight()),
                    String.format("%.1f",p.getChargeableWeight())});
        if (rows.isEmpty()) System.out.println("  No parcels have volumetric > actual weight.");
        else TablePrinter.print(h, rows);
    }

    static void counts() {
        String[] h = {"Entity","Count"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Customers",  String.valueOf(DataStore.getAllCustomers().size())});
        rows.add(new String[]{"Facilities", String.valueOf(DataStore.getAllFacilities().size())});
        rows.add(new String[]{"Employees",  String.valueOf(DataStore.getAllEmployees().size())});
        rows.add(new String[]{"Vehicles",   String.valueOf(DataStore.getAllVehicles().size())});
        rows.add(new String[]{"Parcels",    String.valueOf(DataStore.getAllParcels().size())});
        rows.add(new String[]{"Trips",      String.valueOf(DataStore.getAllTrips().size())});
        rows.add(new String[]{"Payments",   String.valueOf(DataStore.getAllPayments().size())});
        TablePrinter.print(h, rows);
    }

    private static String fmt(double v) { return String.format("%.2f", v); }
}
