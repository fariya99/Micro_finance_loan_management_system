public class Validator {

    public static boolean isValidPhone(String phone) {
        return phone.matches("^03[0-9]{9}$"); // Pakistani 11-digit starts with 03
    }

    public static boolean isValidCNIC(String cnic) {
        return cnic.matches("^[0-9]{13}$"); // 13 digits
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}

