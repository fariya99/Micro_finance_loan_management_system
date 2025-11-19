public class Validator {


    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }

        String trimmedName = name.trim();

        // 1. Check for non-empty string
        if (trimmedName.isEmpty()) {
            return false;
        }

        // 2. Check length constraints
        if (trimmedName.length() < 2 || trimmedName.length() > 50) {
            return false;
        }

        // 3. Check character set using a regular expression
        // Pattern allows letters, spaces, hyphens, and apostrophes.
        if (!trimmedName.matches("^[a-zA-Z\\s'-]+$")) {
            return false;
        }

        // Prevents names that are just spaces or invalid characters
        if (trimmedName.matches("[\\s'-]*")) {
            return false;
        }

        return true;
    }

    // ---------------- CNIC ----------------


    public static boolean isValidCNIC(String cnic) {
        if (cnic == null) return false;
        String clean = cnic.replace("-", "").trim();
        return clean.matches("^[0-9]{13}$");   // Must be 13 digits
    }


    public static String formatCNIC(String cnic) {
        if (cnic == null) return null;
        String clean = cnic.replace("-", "").trim();

        if (!clean.matches("^[0-9]{13}$")) return null;  // Must be 13 digits

        return clean.substring(0, 5) + "-" +
                clean.substring(5, 12) + "-" +
                clean.substring(12);
    }

    // ---------------- PHONE ----------------

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        // Remove spaces for validation
        phone = phone.replace(" ", "").trim();
        // Check for 11 digits starting with '03'
        return phone.matches("^03[0-9]{9}$");
    }

    // ---------------- EMAIL ----------------


    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        // Basic pattern: word characters, plus, underscore, dot, hyphen, @, domain, dot, TLD
        return email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}

