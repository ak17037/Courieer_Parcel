import java.util.ArrayList;
import java.util.List;

/**
 * Payment management menu.
 * Concepts: HashMap operations, String formatting, StringBuilder (invoice).
 */
public class PaymentMenu {

    public static void show() {
        new Menu("PAYMENTS")
            .add("View all payments",       PaymentMenu::listAll)
            .add("View payments by status", PaymentMenu::byStatus)
            .add("Update payment status",   PaymentMenu::updateStatus)
            .add("Print invoice",           PaymentMenu::invoice)
            .show();
    }

    static void listAll() { printPayments(DataStore.getAllPayments()); }

    static void byStatus() {
        String status = In.choice("Status: ", "Pending", "Completed", "Failed");
        List<Payment> filtered = new ArrayList<>();
        for (Payment p : DataStore.getAllPayments())
            if (status.equals(p.getStatus())) filtered.add(p);
        if (filtered.isEmpty()) { System.out.println("  No " + status + " payments."); return; }
        printPayments(filtered);
    }

    static void updateStatus() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Payment pay = DataStore.getPayment(wb);
        if (pay == null) throw new Exception("No payment for " + wb + ".");
        System.out.println("  Current: " + pay);
        System.out.println("New status:");
        String st = In.choice("Choose: ", "Pending", "Completed", "Failed");
        if ("Completed".equals(st) && In.yes("Change payment method?")) {
            String method = In.choice("Method: ", "Cash", "Card", "UPI", "Net Banking");
            pay.setMethod(method);
        }
        pay.setStatus(st);
        System.out.println("  OK Payment for " + wb + " is now " + st + ".");
    }

    static void invoice() throws Exception {
        String wb = In.str("Waybill No: ").toUpperCase();
        Parcel p = DataStore.getParcel(wb);
        if (p == null) throw new Exception("Parcel not found.");
        Payment pay = DataStore.getPayment(wb);
        if (pay == null) throw new Exception("No payment found.");

        Customer sender   = DataStore.getCustomer(p.getSenderId());
        Customer receiver = DataStore.getCustomer(p.getReceiverId());
        Facility origin   = DataStore.getFacility(p.getOriginFacilityId());
        Facility dest     = DataStore.getFacility(p.getDestFacilityId());

        // Build invoice using StringBuilder – demonstrates StringBuilder usage
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(58);
        String thin = "-".repeat(58);

        sb.append("\n").append(line).append("\n");
        sb.append(String.format("  TAX INVOICE  INV-%-20s Date %s%n", wb, p.getBookingDate().toLocalDate()));
        sb.append("  Courier & Parcel Logistics\n");
        sb.append(line).append("\n");
        sb.append("  Bill to : ").append(sender!=null?sender.getFullName()+", "+sender.getAddress():"-").append("\n");
        sb.append("  Ship to : ").append(receiver!=null?receiver.getFullName()+", "+receiver.getAddress():"-").append("\n");
        sb.append("  Route   : ").append(origin!=null?origin.getName():"-").append(" -> ").append(dest!=null?dest.getName():"-").append("\n");
        sb.append("  Service : ").append(p.getServiceTier()).append(", promised ").append(p.getPromisedDate()).append("\n");
        sb.append("  Weight  : ").append(p.getWeightKg()).append(" kg\n");
        sb.append(thin).append("\n");
        sb.append(String.format("  %-40s Rs %11.2f%n", "TOTAL", pay.getAmount()));
        sb.append("  Payment : ").append(pay.getMethod()).append(" - ").append(pay.getStatus()).append("\n");
        sb.append(line);

        System.out.println(sb);
    }

    private static void printPayments(java.util.Collection<Payment> payments) {
        String[] h = {"Receipt","Waybill","Sender","Amount","Method","Status"};
        List<String[]> rows = new ArrayList<>();
        for (Payment pay : payments) {
            Parcel p = DataStore.getParcel(pay.getWaybillNo());
            Customer s = p!=null?DataStore.getCustomer(p.getSenderId()):null;
            rows.add(new String[]{String.valueOf(pay.getReceiptNo()), pay.getWaybillNo(),
                s!=null?s.getFullName():"-", String.format("%.2f",pay.getAmount()),
                pay.getMethod(), pay.getStatus()});
        }
        TablePrinter.print(h, rows);
    }
}
