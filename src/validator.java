public class Validator {

    // ---------------- CNIC ----------------
    public static boolean isValidCNIC(String cnic) {
        String clean = cnic.replace("-", "").trim();
        return clean.matches("^[0-9]{13}$");   // 13 digits
    }

    public static String formatCNIC(String cnic) {
        String clean = cnic.replace("-", "").trim();

        if (!clean.matches("^[0-9]{13}$")) return null;  // invalid

        return clean.substring(0, 5) + "-" +
               clean.substring(5, 12) + "-" +
               clean.substring(12);
    }

    // ---------------- PHONE ----------------
    public static boolean isValidPhone(String phone) {
        phone = phone.replace(" ", "").trim();
        return phone.matches("^03[0-9]{9}$");   // Pakistani 03xxxxxxxxx
    }

    // ---------------- EMAIL ----------------
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}

