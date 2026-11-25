/**
 * Generates unique waybill numbers with a Luhn check digit.
 * Concepts: static variables, String formatting, algorithm (Luhn mod-10).
 */
public class WaybillGenerator {
    private static int counter = 100001;

    /** Returns the next waybill, e.g. WB1000014 */
    public static String next() {
        String digits = String.format("%06d", counter++);
        return "WB" + digits + luhnCheckDigit(digits);
    }

    /** Luhn mod-10: catches every single-digit error and most transpositions */
    private static int luhnCheckDigit(String digits) {
        int sum = 0;
        for (int i = 0; i < digits.length(); i++) {
            int d = digits.charAt(digits.length() - 1 - i) - '0';
            if (i % 2 == 0) { d *= 2; if (d > 9) d -= 9; }
            sum += d;
        }
        return (10 - sum % 10) % 10;
    }
}
