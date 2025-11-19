import java.time.LocalDate;

public class BusinessLoan extends Loan {
    private static final double DEFAULT_RATE = 8.0;
    private boolean installment;
    private double emi;

    public BusinessLoan(String loanId, String customerId, double principal, int durationMonths, LocalDate issueDate, boolean installment) {
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
        double monthlyRate = interestRate / 12 / 100.0;
        double amount = principal * Math.pow(1 + monthlyRate, durationMonths);
        double interest = amount - principal;
        return round(interest);
    }

    @Override
    public String getLoanType() {
        return "BUSINESS";
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
