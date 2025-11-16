// EducationLoan.java
import java.time.LocalDate;

public class EducationLoan extends Loan {
    private static final double DEFAULT_RATE = 5.0;

    public EducationLoan(String loanId, String customerId, double principal, int durationMonths, LocalDate issueDate) {
        super(loanId, customerId, principal, DEFAULT_RATE, durationMonths, issueDate);
    }

    @Override
    public double calculateInterest() {
        double interest = principal * (interestRate / 100.0) * (durationMonths / 12.0);
        return round(interest);
    }

    @Override
    public String getLoanType() { return "EDUCATION"; }
}
