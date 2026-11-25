import java.util.ArrayList;
import java.util.List;

/**
 * Customer management menu.
 * Concepts: HashMap lookup, ArrayList, String searching, exception handling.
 */
public class CustomerMenu {

    public static void show() {
        new Menu("CUSTOMERS")
            .add("Add new customer",              CustomerMenu::add)
            .add("View all customers",            CustomerMenu::list)
            .add("Search customer (name/phone)",  CustomerMenu::search)
            .add("Add another phone number",      CustomerMenu::addPhone)
            .add("Update email / address",        CustomerMenu::updateInfo)
            .add("View a customer's parcels",     CustomerMenu::viewParcels)
            .add("Delete customer",               CustomerMenu::delete)
            .show();
    }

    static void add() throws Exception {
        String first  = In.str("First name : ");
        String last   = In.str("Last name  : ");
        String email  = In.opt("Email (optional) : ");
        String addr   = In.str("Address    : ");
        String phones = In.str("Phone number(s), comma-separated (10 digits each): ");

        int id = DataStore.getNextCustomerId();
        Customer c = new Customer(id, first, last, email, addr);
        for (String ph : phones.split(",")) {
            String t = ph.trim();
            if (t.length() != 10) throw new Exception("Phone must be 10 digits: " + t);
            c.addPhone(t);
        }
        DataStore.addCustomer(c);
        System.out.println("\n  OK Customer added with Customer_ID = " + id);
    }

    static void list() {
        String[] h = {"ID","First","Last","Phones","Email","Address"};
        List<String[]> rows = new ArrayList<>();
        for (Customer c : DataStore.getAllCustomers())
            rows.add(new String[]{String.valueOf(c.getId()), c.getFirstName(), c.getLastName(),
                String.join(", ", c.getPhones()), c.getEmail()==null?"-":c.getEmail(), c.getAddress()});
        TablePrinter.print(h, rows);
    }

    static void search() {
        String q = In.str("Enter part of name or phone: ");
        List<Customer> res = DataStore.searchCustomers(q);
        if (res.isEmpty()) { System.out.println("  No customers found."); return; }
        String[] h = {"ID","Name","Phones","Address"};
        List<String[]> rows = new ArrayList<>();
        for (Customer c : res)
            rows.add(new String[]{String.valueOf(c.getId()), c.getFullName(),
                String.join(", ", c.getPhones()), c.getAddress()});
        TablePrinter.print(h, rows);
    }

    static void addPhone() throws Exception {
        int id = In.integer("Customer ID: ");
        Customer c = DataStore.getCustomer(id);
        if (c == null) throw new Exception("Customer " + id + " not found.");
        System.out.println("    -> " + c);
        String ph = In.str("New phone (10 digits): ");
        if (ph.length() != 10) throw new Exception("Phone must be 10 digits.");
        c.addPhone(ph);
        System.out.println("  OK Phone added.");
    }

    static void updateInfo() throws Exception {
        int id = In.integer("Customer ID: ");
        Customer c = DataStore.getCustomer(id);
        if (c == null) throw new Exception("Customer " + id + " not found.");
        System.out.println("    -> " + c + " | Email: " + c.getEmail());
        String email = In.opt("New email   (blank = keep): ");
        String addr  = In.opt("New address (blank = keep): ");
        if (email != null) c.setEmail(email);
        if (addr  != null) c.setAddress(addr);
        System.out.println("  OK Customer updated.");
    }

    static void viewParcels() throws Exception {
        int id = In.integer("Customer ID: ");
        Customer c = DataStore.getCustomer(id);
        if (c == null) throw new Exception("Customer " + id + " not found.");
        System.out.println("    -> " + c);

        System.out.println("\nParcels SENT:");
        String[] h = {"Waybill","To","Destination","Status","Amount"};
        List<String[]> rows = new ArrayList<>();
        for (Parcel p : DataStore.getAllParcels()) {
            if (p.getSenderId() != id) continue;
            Customer r = DataStore.getCustomer(p.getReceiverId());
            Facility d = DataStore.getFacility(p.getDestFacilityId());
            Payment pay = DataStore.getPayment(p.getWaybillNo());
            rows.add(new String[]{p.getWaybillNo(), r!=null?r.getFullName():"-",
                d!=null?d.getName():"-", p.getCurrentStatus(),
                pay!=null?String.format("%.2f",pay.getAmount()):"-"});
        }
        TablePrinter.print(h, rows);

        System.out.println("\nParcels RECEIVED:");
        List<String[]> rows2 = new ArrayList<>();
        for (Parcel p : DataStore.getAllParcels()) {
            if (p.getReceiverId() != id) continue;
            Customer s = DataStore.getCustomer(p.getSenderId());
            Facility d = DataStore.getFacility(p.getDestFacilityId());
            rows2.add(new String[]{p.getWaybillNo(), s!=null?s.getFullName():"-",
                d!=null?d.getName():"-", p.getCurrentStatus(), "-"});
        }
        TablePrinter.print(new String[]{"Waybill","From","Destination","Status","Amount"}, rows2);
    }

    static void delete() throws Exception {
        int id = In.integer("Customer ID to delete: ");
        Customer c = DataStore.getCustomer(id);
        if (c == null) throw new Exception("Customer " + id + " not found.");
        System.out.println("    -> " + c);
        if (!In.yes("Really delete customer " + id + "?")) return;
        DataStore.removeCustomer(id);
        System.out.println("  OK Customer deleted.");
    }
}
