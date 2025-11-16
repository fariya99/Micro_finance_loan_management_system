// BusinessLoan.java
import java.time.LocalDate;

public class BusinessLoan extends Loan {
    private static final double DEFAULT_RATE = 8.0;

    public BusinessLoan(String loanId, String customerId, double principal, int durationMonths, LocalDate issueDate) {
        super(loanId, customerId, principal, DEFAULT_RATE, durationMonths, issueDate);
    }

    @Override
    public double calculateInterest() {
        double r = interestRate / 100.0;
        double factor = Math.pow(1.0 + r / 12.0, durationMonths);
        double amount = principal * factor;
        double interest = amount - principal;
        return round(interest);
    }

    @Override
    public String getLoanType() { return "BUSINESS"; }
}
