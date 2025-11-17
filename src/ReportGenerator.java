// ReportGenerator.java
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReportGenerator {
    private DataStore store;

    public ReportGenerator(DataStore store) { this.store = store; }

    public String loanSummaryText() {
        List<Loan> loans = store.getLoans();
        double totalLoaned = loans.stream().mapToDouble(Loan::getPrincipal).sum();
        double totalOutstanding = loans.stream().mapToDouble(Loan::getBalance).sum();
        double totalInterest = loans.stream().mapToDouble(Loan::calculateInterest).sum();
        StringBuilder sb = new StringBuilder();
        sb.append("Loan Summary\n");
        sb.append("-----------------------\n");
        sb.append(String.format("Total principal issued: %.2f\n", totalLoaned));
        sb.append(String.format("Total outstanding balance: %.2f\n", totalOutstanding));
        sb.append(String.format("Total interest across loans: %.2f\n", totalInterest));
        sb.append(String.format("Number of loans: %d\n", loans.size()));
        return sb.toString();
    }

    public String overdueText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Overdue Loans\n");
        sb.append("-----------------------\n");
        List<Loan> overdue = store.getLoans().stream().peek(l -> l.checkOverdue(LocalDate.now()))
                .filter(l -> "OVERDUE".equals(l.getStatus()))
                .collect(Collectors.toList());
        if (overdue.isEmpty()) {
            sb.append("No overdue loans.\n");
            return sb.toString();
        }
        for (Loan l : overdue) sb.append(l.toString()).append("\n");
        return sb.toString();
    }

    public String customerReportText(String customerId) {
        Customer c = store.findCustomerById(customerId);
        if (c == null) return "Customer not found: " + customerId;
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Report\n");
        sb.append("-----------------------\n");
        sb.append(c.toString()).append("\n");
        List<Loan> loans = store.getLoans().stream().filter(l -> l.getCustomerId().equals(customerId)).collect(Collectors.toList());
        if (loans.isEmpty()) { sb.append("No loans.\n"); return sb.toString(); }
        for (Loan l : loans) sb.append(l.toString()).append("\n");
        double outstanding = loans.stream().mapToDouble(Loan::getBalance).sum();
        sb.append(String.format("Total outstanding: %.2f\n", outstanding));
        return sb.toString();
    }
}
