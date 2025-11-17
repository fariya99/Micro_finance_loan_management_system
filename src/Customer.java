import java.util.Objects;

public class Customer {
    private String customerId;
    private String name;
    private String cnic;
    private String email;
    private String address;
    private String phoneNumber;

    public Customer(String customerId, String name, String cnic, String email, String address, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.cnic = cnic;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getCnic() { return cnic; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setName(String name) { this.name = name; }
    public void setCnic(String cnic) { this.cnic = cnic; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String toCSV() {
        return String.join(",",
                escapeCSV(customerId),
                escapeCSV(name),
                escapeCSV(cnic),
                escapeCSV(email),
                escapeCSV(address),
                escapeCSV(phoneNumber)
        );
    }

    public static Customer fromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        return new Customer(
                unescapeCSV(parts[0]),
                unescapeCSV(parts[1]),
                unescapeCSV(parts[2]),
                unescapeCSV(parts[3]),
                unescapeCSV(parts[4]),
                unescapeCSV(parts[5])
        );
    }

    private String escapeCSV(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String unescapeCSV(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    @Override
    public String toString() {
        return String.format("%s — %s (%s)", customerId, name, phoneNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer c = (Customer) o;
        return Objects.equals(customerId, c.customerId);
    }

    @Override
    public int hashCode() { return Objects.hash(customerId); }
}
