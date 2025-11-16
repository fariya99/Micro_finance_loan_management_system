// Loan.java
import java.time.LocalDate;
import java.util.Objects;

public abstract class Loan {
    protected String loanId;
    protected String customerId;
    protected double principal;
    protected double interestRate;
    protected int durationMonths;
    protected double balance;
    protected LocalDate issueDate;
    protected LocalDate dueDate;
    protected String status; // ACTIVE, CLOSED, OVERDUE

    public Loan(String loanId, String customerId, double principal, double interestRate, int durationMonths, LocalDate issueDate) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.principal = principal;
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusMonths(durationMonths);
        this.status = "ACTIVE";
        this.balance = calculateTotalPayable();
    }

    public String getLoanId() { return loanId; }
    public String getCustomerId() { return customerId; }
    public double getPrincipal() { return principal; }
    public double getInterestRate() { return interestRate; }
    public int getDurationMonths() { return durationMonths; }
    public double getBalance() { return balance; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public String getStatus() { return status; }

    public abstract double calculateInterest();
    public double calculateTotalPayable() { return round(principal + calculateInterest()); }

    public void makePayment(double amount) {
        if (amount <= 0) return;
        balance = round(balance - amount);
        if (balance <= 0.0) {
            balance = 0.0;
            status = "CLOSED";
        }
    }

    public void checkOverdue(LocalDate now) {
        if (!"CLOSED".equals(status) && now.isAfter(dueDate) && balance > 0.0) {
            status = "OVERDUE";
        }
    }

    public abstract String getLoanType();

    public String toCSV() {
        return String.join(",",
                getLoanType(),
                loanId,
                customerId,
                String.valueOf(principal),
                String.valueOf(interestRate),
                String.valueOf(durationMonths),
                String.valueOf(balance),
                issueDate.toString(),
                dueDate.toString(),
                status);
    }

    public static Loan fromCSV(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 10) return null;
        String type = p[0];
        String id = p[1];
        String custId = p[2];
        double principal = Double.parseDouble(p[3]);
        double rate = Double.parseDouble(p[4]);
        int duration = Integer.parseInt(p[5]);
        double balance = Double.parseDouble(p[6]);
        LocalDate issue = LocalDate.parse(p[7]);
        String status = p[9];

        Loan loan;
        switch (type) {
            case "PERSONAL":
                loan = new PersonalLoan(id, custId, principal, duration, issue);
                break;
            case "BUSINESS":
                loan = new BusinessLoan(id, custId, principal, duration, issue);
                break;
            case "EDUCATION":
                loan = new EducationLoan(id, custId, principal, duration, issue);
                break;
            default:
                return null;
        }
        loan.balance = balance;
        loan.status = status;
        return loan;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | P:%.2f | R:%.2f%% | D:%d mo | Bal:%.2f | Issued:%s | Due:%s | %s",
                getLoanType(), loanId, principal, interestRate, durationMonths, balance, issueDate, dueDate, status);
    }

    protected double round(double v) { return Math.round(v * 100.0) / 100.0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan)) return false;
        Loan loan = (Loan) o;
        return Objects.equals(loanId, loan.loanId);
    }

    @Override
    public int hashCode() { return Objects.hash(loanId); }
}
