// ReportGenerator.java
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReportGenerator {

    private DataStore store;

    public ReportGenerator(DataStore store) {
        this.store = store;
    }

    // LOAN SUMMARY
    public String loanSummaryText() {
        List<Loan> loans = store.getLoans();

        double totalLoaned = loans.stream().mapToDouble(Loan::getPrincipal).sum();
        double totalOutstanding = loans.stream().mapToDouble(Loan::getBalance).sum();
        double totalInterest = loans.stream().mapToDouble(Loan::calculateInterest).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("=========== LOAN SUMMARY REPORT ===========\n\n");
        sb.append(String.format("Total Principal Issued:       %.2f\n", totalLoaned));
        sb.append(String.format("Total Outstanding Balance:    %.2f\n", totalOutstanding));
        sb.append(String.format("Total Interest Accrued:       %.2f\n", totalInterest));
        sb.append(String.format("Number of Loans:              %d\n", loans.size()));
        sb.append("\n===========================================\n");
        return sb.toString();
    }

    // OVERDUE
    public String overdueText() {
        List<Loan> overdue = store.getLoans().stream()
                .peek(l -> l.checkOverdue(LocalDate.now()))
                .filter(l -> "OVERDUE".equals(l.getStatus()))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("============== OVERDUE LOANS ==============\n\n");
        if (overdue.isEmpty()) {
            sb.append("No overdue loans found.\n");
            sb.append("\n===========================================\n");
            return sb.toString();
        }
        for (Loan l : overdue) {
            sb.append("Loan ID: ").append(l.getLoanId()).append("\n");
            sb.append("Customer: ").append(l.getCustomerId()).append("\n");
            sb.append("Amount: ").append(l.getPrincipal()).append("\n");
            sb.append("Remaining Balance: ").append(l.getBalance()).append("\n");
            sb.append("Due Date: ").append(l.getDueDate()).append("\n");
            sb.append("Status: ").append(l.getStatus()).append("\n");
            sb.append("-------------------------------------------\n");
        }
        sb.append("\n===========================================\n");
        return sb.toString();
    }

    // CUSTOMER REPORT
    public String customerReportText(String customerId) {
        Customer c = store.findCustomerById(customerId);
        if (c == null) return "Customer not found: " + customerId;

        StringBuilder sb = new StringBuilder();
        sb.append("============== CUSTOMER REPORT ==============\n\n");
        sb.append("Customer Details:\n");
        sb.append(c.toString()).append("\n\n");

        List<Loan> loans = store.getLoans().stream()
                .filter(l -> l.getCustomerId().equals(customerId))
                .collect(Collectors.toList());

        if (loans.isEmpty()) {
            sb.append("This customer has no loans.\n");
            sb.append("\n=============================================\n");
            return sb.toString();
        }

        double totalOutstanding = loans.stream().mapToDouble(Loan::getBalance).sum();

        sb.append("Loans:\n");
        sb.append("---------------------------------------------\n");

        for (Loan l : loans) {
            sb.append("Loan ID: ").append(l.getLoanId()).append("\n");
            sb.append("Principal: ").append(l.getPrincipal()).append("\n");
            sb.append("Balance: ").append(l.getBalance()).append("\n");
            sb.append("Interest: ").append(l.calculateInterest()).append("\n");
            sb.append("Status: ").append(l.getStatus()).append("\n");
            sb.append("---------------------------------------------\n");
        }

        sb.append(String.format("\nTotal Outstanding: %.2f\n", totalOutstanding));
        sb.append("\n=============================================\n");
        return sb.toString();
    }

    // CSV EXPORTS
    public boolean exportLoanSummaryCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Loan> loans = store.getLoans();
            writer.write("LoanID,CustomerID,Principal,Balance,Interest,Status,DueDate\n");
            for (Loan l : loans) {
                writer.write(String.join(",",
                        l.getLoanId(),
                        l.getCustomerId(),
                        String.valueOf(l.getPrincipal()),
                        String.valueOf(l.getBalance()),
                        String.valueOf(l.calculateInterest()),
                        l.getStatus(),
                        l.getDueDate().toString()
                ));
                writer.write("\n");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportOverdueCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Loan> overdue = store.getLoans().stream()
                    .peek(l -> l.checkOverdue(LocalDate.now()))
                    .filter(l -> "OVERDUE".equals(l.getStatus()))
                    .collect(Collectors.toList());

            writer.write("LoanID,CustomerID,Principal,Balance,Interest,Status,DueDate\n");
            for (Loan l : overdue) {
                writer.write(String.join(",",
                        l.getLoanId(),
                        l.getCustomerId(),
                        String.valueOf(l.getPrincipal()),
                        String.valueOf(l.getBalance()),
                        String.valueOf(l.calculateInterest()),
                        l.getStatus(),
                        l.getDueDate().toString()
                ));
                writer.write("\n");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportCustomerCSV(String customerId, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            Customer c = store.findCustomerById(customerId);
            if (c == null) return false;
            List<Loan> loans = store.getLoans().stream()
                    .filter(l -> l.getCustomerId().equals(customerId))
                    .collect(Collectors.toList());

            writer.write("LoanID,Principal,Balance,Interest,Status,DueDate\n");
            for (Loan l : loans) {
                writer.write(String.join(",",
                        l.getLoanId(),
                        String.valueOf(l.getPrincipal()),
                        String.valueOf(l.getBalance()),
                        String.valueOf(l.calculateInterest()),
                        l.getStatus(),
                        l.getDueDate().toString()
                ));
                writer.write("\n");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
