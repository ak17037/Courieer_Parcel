import java.util.ArrayList;

/**
 * Customer entity.
 * Concepts: OOP (encapsulation, constructors, this), ArrayList, toString override.
 */
public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private ArrayList<String> phones;   // multivalued: one customer, many phones

    public Customer(int id, String firstName, String lastName, String email, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phones = new ArrayList<>();
    }

    // ---- getters ----
    public int getId()                 { return id; }
    public String getFirstName()       { return firstName; }
    public String getLastName()        { return lastName; }
    public String getFullName()        { return firstName + " " + lastName; }
    public String getEmail()           { return email; }
    public String getAddress()         { return address; }
    public ArrayList<String> getPhones() { return phones; }

    // ---- setters ----
    public void setEmail(String email)     { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void addPhone(String phone)     { phones.add(phone); }

    @Override
    public String toString() { return getFullName() + " (" + address + ")"; }
}
