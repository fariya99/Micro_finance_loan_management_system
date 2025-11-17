import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final String CUSTOMERS_FILE = "customers.csv";
    private static final String LOANS_FILE = "loans.csv";
    private static final String PAYMENTS_FILE = "payments.csv";

    private List<Customer> customers = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();

    public DataStore() {
        ensureFilesExist();
        loadAll();
    }

    private void ensureFilesExist() {
        try {
            if (!Files.exists(Paths.get(CUSTOMERS_FILE))) Files.createFile(Paths.get(CUSTOMERS_FILE));
            if (!Files.exists(Paths.get(LOANS_FILE))) Files.createFile(Paths.get(LOANS_FILE));
            if (!Files.exists(Paths.get(PAYMENTS_FILE))) Files.createFile(Paths.get(PAYMENTS_FILE));
        } catch (IOException e) {
            System.err.println("Error ensuring data files: " + e.getMessage());
        }
    }

    public List<Customer> getCustomers() { return customers; }
    public List<Loan> getLoans() { return loans; }
    public List<Payment> getPayments() { return payments; }

    public void addCustomer(Customer c) {
        customers.add(c);
        appendLine(CUSTOMERS_FILE, c.toCSV());
    }
    public boolean deleteCustomer(String id) {
    Customer c = findCustomerById(id);
    if (c == null) return false;

    customers.remove(c);
    rewriteCustomersFile();
    return true;
    }

    public boolean editCustomer(String id, String newName, String newCnic, String newEmail,
                            String newAddress, String newPhone) {

    Customer c = findCustomerById(id);
    if (c == null) return false;

    // VALIDATION
    if (!Validator.isValidCNIC(newCnic)) {
        System.out.println("Invalid CNIC (must be 13 digits)");
        return false;
    }
    if (!Validator.isValidPhone(newPhone)) {
        System.out.println("Invalid Phone (03xxxxxxxxx)");
        return false;
    }
    if (!Validator.isValidEmail(newEmail)) {
        System.out.println("Invalid Email format");
        return false;
    }

    // UPDATE
    c.setName(newName);
    c.setCnic(newCnic);
    c.setEmail(newEmail);
    c.setAddress(newAddress);
    c.setPhoneNumber(newPhone);

    // REWRITE CSV
    rewriteCustomersFile();

    return true;
    }
    

    public void addLoan(Loan l) {
        loans.add(l);
        appendLine(LOANS_FILE, l.toCSV());
    }

    public void addPayment(Payment p) {
        payments.add(p);
        appendLine(PAYMENTS_FILE, p.toCSV());
    }

    public void recordPaymentAndUpdateLoan(Payment p) {
        addPayment(p);
        Loan loan = findLoanById(p.getLoanId());
        if (loan != null) {
            loan.makePayment(p.getAmountPaid());
            loan.checkOverdue(LocalDate.now());
            rewriteLoansFile();
            rewritePaymentsFile();
        }
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers) if (c.getCustomerId().equals(id)) return c;
        return null;
    }

    public Loan findLoanById(String id) {
        for (Loan l : loans) if (l.getLoanId().equals(id)) return l;
        return null;
    }

    public List<Payment> getPaymentsForLoan(String loanId) {
        List<Payment> list = new ArrayList<>();
        for (Payment p : payments) if (p.getLoanId().equals(loanId)) list.add(p);
        return list;
    }

    private void appendLine(String file, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to " + file + ": " + e.getMessage());
        }
    }

    private void rewriteLoansFile() { writeAllLines(LOANS_FILE, loansToCSV()); }
    private void rewritePaymentsFile() { writeAllLines(PAYMENTS_FILE, paymentsToCSV()); }
    private void rewriteCustomersFile() {
    List<String> list = new ArrayList<>();
    for (Customer c : customers) list.add(c.toCSV());
    writeAllLines("customers.csv", list);
}


    private void writeAllLines(String file, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + file + ": " + e.getMessage());
        }
    }

    private List<String> loansToCSV() {
        List<String> out = new ArrayList<>();
        for (Loan l : loans) out.add(l.toCSV());
        return out;
    }
    private List<String> paymentsToCSV() {
        List<String> out = new ArrayList<>();
        for (Payment p : payments) out.add(p.toCSV());
        return out;
    }

    private void loadAll() {
        loadCustomers();
        loadLoans();
        loadPayments();
    }

    private void loadCustomers() {
        customers.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Customer c = Customer.fromCSV(line);
                if (c != null) customers.add(c);
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadLoans() {
        loans.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(LOANS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Loan l = Loan.fromCSV(line);
                if (l != null) loans.add(l);
            }
        } catch (IOException e) {
            System.err.println("Error loading loans: " + e.getMessage());
        }
    }

    private void loadPayments() {
        payments.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(PAYMENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Payment p = Payment.fromCSV(line);
                if (p != null) payments.add(p);
            }
        } catch (IOException e) {
            System.err.println("Error loading payments: " + e.getMessage());
        }
    }
}
