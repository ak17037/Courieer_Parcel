import java.util.ArrayList;
import java.util.List;

/**
 * Facility & Employee management menu.
 * Concepts: TreeMap (sorted facilities), ArrayList, exception handling.
 */
public class FacilityMenu {

    public static void show() {
        new Menu("FACILITIES & EMPLOYEES")
            .add("Add new facility",                        FacilityMenu::addFacility)
            .add("View all facilities",                     FacilityMenu::listFacilities)
            .add("Parcels currently at a facility",         FacilityMenu::parcelsAt)
            .add("Add employee",                            FacilityMenu::addEmployee)
            .add("View employees",                          FacilityMenu::listEmployees)
            .add("Transfer employee to another facility",   FacilityMenu::transfer)
            .add("Remove employee",                         FacilityMenu::removeEmployee)
            .show();
    }

    static void addFacility() throws Exception {
        String name   = In.str("Facility name : ");
        String addr   = In.str("Address       : ");
        System.out.println("Type:");
        String type   = In.choice("Choose: ", "Hub", "Branch");
        String region = In.str("Region        : ");

        System.out.println("\nRule: a facility must have at least one employee.");
        String empName = In.str("Employee name   : ");
        System.out.println("Role:");
        String role = In.choice("Choose: ", "Manager", "Branch Staff", "Delivery Agent", "Driver");
        String lic  = In.opt("License number  : ");

        int fId = DataStore.getNextFacilityId();
        DataStore.addFacility(new Facility(fId, name, addr, type, region));
        int eId = DataStore.getNextEmployeeId();
        DataStore.addEmployee(new Employee(eId, empName, role, lic, fId));
        System.out.println("\n  OK Facility added with ID = " + fId + " (with 1 employee)");
    }

    public static void listFacilities() {
        String[] h = {"ID","Facility","Type","Region","Address","Employees","Parcels"};
        List<String[]> rows = new ArrayList<>();
        for (Facility f : DataStore.getAllFacilities()) {
            int emp = DataStore.getEmployeesByFacility(f.getId()).size();
            int par = DataStore.getParcelsAtFacility(f.getId()).size();
            rows.add(new String[]{String.valueOf(f.getId()), f.getName(), f.getType(),
                f.getRegion(), f.getAddress(), String.valueOf(emp), String.valueOf(par)});
        }
        TablePrinter.print(h, rows);
    }

    static void parcelsAt() throws Exception {
        int fId = In.integer("Facility ID: ");
        Facility f = DataStore.getFacility(fId);
        if (f == null) throw new Exception("Facility " + fId + " not found.");
        System.out.println("    -> " + f);
        List<Parcel> list = DataStore.getParcelsAtFacility(fId);
        if (list.isEmpty()) { System.out.println("  No parcels at this facility."); return; }
        String[] h = {"Waybill","Sender","Receiver","Status","Weight"};
        List<String[]> rows = new ArrayList<>();
        for (Parcel p : list) {
            Customer s = DataStore.getCustomer(p.getSenderId());
            Customer r = DataStore.getCustomer(p.getReceiverId());
            rows.add(new String[]{p.getWaybillNo(), s!=null?s.getFullName():"-",
                r!=null?r.getFullName():"-", p.getCurrentStatus(),
                String.format("%.1f kg", p.getWeightKg())});
        }
        TablePrinter.print(h, rows);
    }

    static void addEmployee() throws Exception {
        int fId = In.integer("Facility ID: ");
        if (DataStore.getFacility(fId) == null) throw new Exception("Facility not found.");
        String name = In.str("Employee name   : ");
        System.out.println("Role:");
        String role = In.choice("Choose: ", "Manager", "Branch Staff", "Delivery Agent", "Driver");
        String lic  = In.opt("License number" + (role.equals("Driver") ? " (REQUIRED)" : " (optional)") + " : ");
        if (role.equals("Driver") && (lic == null || lic.isEmpty()))
            throw new Exception("A Driver must have a license number.");
        int id = DataStore.getNextEmployeeId();
        DataStore.addEmployee(new Employee(id, name, role, lic, fId));
        System.out.println("  OK Employee added with ID = " + id);
    }

    static void listEmployees() {
        String[] h = {"ID","Name","Role","License","Facility"};
        List<String[]> rows = new ArrayList<>();
        for (Employee e : DataStore.getAllEmployees()) {
            Facility f = DataStore.getFacility(e.getFacilityId());
            rows.add(new String[]{String.valueOf(e.getId()), e.getName(), e.getRole(),
                e.getLicenseNumber()==null?"-":e.getLicenseNumber(), f!=null?f.getName():"-"});
        }
        TablePrinter.print(h, rows);
    }

    static void transfer() throws Exception {
        int eId = In.integer("Employee ID: ");
        Employee e = DataStore.getEmployee(eId);
        if (e == null) throw new Exception("Employee not found.");
        System.out.println("    -> " + e);
        int fId = In.integer("New facility ID: ");
        if (DataStore.getFacility(fId) == null) throw new Exception("Facility not found.");
        e.setFacilityId(fId);
        System.out.println("  OK Employee transferred.");
    }

    static void removeEmployee() throws Exception {
        int id = In.integer("Employee ID: ");
        Employee e = DataStore.getEmployee(id);
        if (e == null) throw new Exception("Employee not found.");
        System.out.println("    -> " + e);
        if (!In.yes("Remove this employee?")) return;
        DataStore.removeEmployee(id);
        System.out.println("  OK Employee removed.");
    }
}
