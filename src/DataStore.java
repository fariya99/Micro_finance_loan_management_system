import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DataStore {
    private static final String CUSTOMERS_FILE = "customers.csv";
    private static final String LOANS_FILE = "loans.csv";
    private static final String PAYMENTS_FILE = "payments.csv";

    private List<Customer> customers = new ArrayList<>();
    private Map<String, Customer> customerMap = new HashMap<>();

    private List<Loan> loans = new ArrayList<>();
    private Map<String, Loan> loanMap = new HashMap<>();

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

    public List<Customer> getCustomers() { return new ArrayList<>(customers); }
    public List<Loan> getLoans() { return new ArrayList<>(loans); }
    public List<Payment> getPayments() { return new ArrayList<>(payments); }

    public boolean addCustomer(Customer c) {
        if (c.getName().isEmpty() || c.getCnic().isEmpty() || c.getPhoneNumber().isEmpty() ||
                !Validator.isValidCNIC(c.getCnic()) || !Validator.isValidPhone(c.getPhoneNumber()) ||
                !Validator.isValidEmail(c.getEmail())) {
            return false;
        }
        customers.add(c);
        customerMap.put(c.getCustomerId(), c);
        appendLine(CUSTOMERS_FILE, c.toCSV());
        return true;
    }

    public boolean deleteCustomer(String id) {
        Customer c = customerMap.remove(id);
        if (c == null) return false;
        customers.remove(c);
        rewriteCustomersFile();
        return true;
    }

    public boolean editCustomer(String id, String newName, String newCnic, String newEmail,
                                String newAddress, String newPhone) {

        Customer c = customerMap.get(id);
        if (c == null) return false;

        if (!Validator.isValidCNIC(newCnic) || !Validator.isValidPhone(newPhone) || !Validator.isValidEmail(newEmail)) {
            return false;
        }

        c.setName(newName);
        c.setCnic(newCnic);
        c.setEmail(newEmail);
        c.setAddress(newAddress);
        c.setPhoneNumber(newPhone);

        rewriteCustomersFile();
        return true;
    }

    public void addLoan(Loan l) {
        loans.add(l);
        loanMap.put(l.getLoanId(), l);
        appendLine(LOANS_FILE, l.toCSV());
    }

    public void addPayment(Payment p) {
        payments.add(p);
        appendLine(PAYMENTS_FILE, p.toCSV());
    }

    public void recordPaymentAndUpdateLoan(Payment p) {
        addPayment(p);
        Loan loan = loanMap.get(p.getLoanId());
        if (loan != null) {
            loan.makePayment(p.getAmountPaid());
            loan.checkOverdue(LocalDate.now());
            rewriteLoansFile();
            rewritePaymentsFile();
        }
    }

    public Customer findCustomerById(String id) { return customerMap.get(id); }
    public Loan findLoanById(String id) { return loanMap.get(id); }

    public List<Payment> getPaymentsForLoan(String loanId) {
        return payments.stream()
                .filter(p -> p.getLoanId().equals(loanId))
                .collect(Collectors.toList());
    }

    public double getTotalPaidForLoan(String loanId) {
        return getPaymentsForLoan(loanId).stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();
    }

    public LocalDate getLastPaymentDateForLoan(String loanId) {
        return getPaymentsForLoan(loanId).stream()
                .map(Payment::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    public boolean isInstallmentLoan(String loanId) {
        Loan l = loanMap.get(loanId);
        return l != null && l.isInstallment();
    }

    public double getEMI(String loanId) {
        Loan l = loanMap.get(loanId);
        return l != null ? l.getEmi() : 0.0;
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
    private void rewriteCustomersFile() { writeAllLines(CUSTOMERS_FILE, customersToCSV()); }

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
        return loans.stream().map(Loan::toCSV).collect(Collectors.toList());
    }

    private List<String> paymentsToCSV() {
        return payments.stream().map(Payment::toCSV).collect(Collectors.toList());
    }

    private List<String> customersToCSV() {
        return customers.stream().map(Customer::toCSV).collect(Collectors.toList());
    }

    private void loadAll() {
        loadCustomers();
        loadLoans();
        loadPayments();
    }

    private void loadCustomers() {
        customers.clear();
        customerMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Customer c = Customer.fromCSV(line);
                if (c != null) {
                    customers.add(c);
                    customerMap.put(c.getCustomerId(), c);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadLoans() {
        loans.clear();
        loanMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(LOANS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Loan l = Loan.fromCSV(line);
                if (l != null) {
                    loans.add(l);
                    loanMap.put(l.getLoanId(), l);
                }
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
