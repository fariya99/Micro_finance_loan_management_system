import java.time.LocalDate;

public class EducationLoan extends Loan {
    private static final double DEFAULT_RATE = 5.0;
    private boolean installment;
    private double emi;

    public EducationLoan(String loanId, String customerId, double principal, int durationMonths, LocalDate issueDate, boolean installment) {
        super(loanId, customerId, principal, DEFAULT_RATE, durationMonths, issueDate, installment);


        this.installment = installment;
        if (installment) {
            this.emi = calculateEMI();
        } else {
            this.emi = 0.0;
        }
    }

    @Override
    public double calculateInterest() {
        double interest = principal * (interestRate / 100.0) * (durationMonths / 12.0);
        return round(interest);
    }

    @Override
    public String getLoanType() {
        return "EDUCATION";
    }

    public double calculateEMI() {
        if (!installment) return 0.0;
        double r = interestRate / 12 / 100.0;
        double n = durationMonths;
        double emi = (principal * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
        return round(emi);
    }

    public boolean isInstallment() {
        return installment;
    }

    public double getEmi() {
        return emi;
    }
}
