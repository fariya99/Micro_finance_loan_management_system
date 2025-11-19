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
    protected boolean installment; // new field
    protected double emi;          // new field

    public Loan(String loanId, String customerId, double principal, double interestRate, int durationMonths, LocalDate issueDate, boolean installment) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.principal = principal;
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusMonths(durationMonths);
        this.status = "ACTIVE";
        this.installment = installment;
        this.balance = calculateTotalPayable();
        if (installment) this.emi = calculateEMI();
        else this.emi = 0.0;
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
    public boolean isInstallment() { return installment; }
    public double getEmi() { return emi; }

    public abstract double calculateInterest();

    public double calculateTotalPayable() { return round(principal + calculateInterest()); }

    public double calculateEMI() {
        if (!installment) return 0.0;
        double r = interestRate / 12 / 100.0;
        double n = durationMonths;
        return round(principal * r * Math.pow(1 + r, n) / (Math.pow(1 + r, n) - 1));
    }

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
                status,
                String.valueOf(installment),
                String.valueOf(emi)
        );
    }

    public static Loan fromCSV(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 12) return null;
        String type = p[0];
        String id = p[1];
        String custId = p[2];
        double principal = Double.parseDouble(p[3]);
        double rate = Double.parseDouble(p[4]);
        int duration = Integer.parseInt(p[5]);
        double balance = Double.parseDouble(p[6]);
        LocalDate issue = LocalDate.parse(p[7]);
        String status = p[9];
        boolean installment = Boolean.parseBoolean(p[10]);
        // emi is optional because it can be recalculated
        // double emi = Double.parseDouble(p[11]);

        Loan loan;
        switch (type) {
            case "PERSONAL":
                loan = new PersonalLoan(id, custId, principal, duration, issue, installment);
                break;
            case "BUSINESS":
                loan = new BusinessLoan(id, custId, principal, duration, issue, installment);
                break;
            case "EDUCATION":
                loan = new EducationLoan(id, custId, principal, duration, issue, installment);
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
        return String.format("%s | %s | P:%.2f | R:%.2f%% | D:%d mo | Bal:%.2f | Issued:%s | Due:%s | %s | Installment:%b | EMI:%.2f",
                getLoanType(), loanId, principal, interestRate, durationMonths, balance, issueDate, dueDate, status, installment, emi);
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
