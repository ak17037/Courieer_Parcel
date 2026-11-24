import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Keyboard input helpers.  Every method keeps asking until the input is valid.
 * Concepts: exception handling (try-catch), input validation, Scanner.
 */
public class In {
    private static final Scanner sc = new Scanner(System.in);

    public static String str(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) { System.out.println(); System.exit(0); }
            String s = sc.nextLine().replace("\uFEFF", "").trim();
            if (!s.isEmpty() || !required) return s;
            System.out.println("  ! This field is required.");
        }
    }

    public static String str(String prompt) { return str(prompt, true); }

    /** Optional text: returns null when left blank. */
    public static String opt(String prompt) {
        String s = str(prompt, false);
        return s.isEmpty() ? null : s;
    }

    public static int integer(String prompt) {
        while (true) {
            try { return Integer.parseInt(str(prompt)); }
            catch (NumberFormatException e) { System.out.println("  ! Enter a whole number."); }
        }
    }

    public static double decimal(String prompt) {
        while (true) {
            try { return Double.parseDouble(str(prompt)); }
            catch (NumberFormatException e) { System.out.println("  ! Enter a number (e.g. 12.5)."); }
        }
    }

    public static LocalDateTime dateTime(String prompt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (true) {
            String s = str(prompt + " (yyyy-MM-dd HH:mm or 'now'): ");
            if (s.equalsIgnoreCase("now")) return LocalDateTime.now().withSecond(0).withNano(0);
            try { return LocalDateTime.parse(s, fmt); }
            catch (DateTimeParseException e) { System.out.println("  ! Use format like 2026-09-25 14:30"); }
        }
    }

    /** Shows numbered options and returns the chosen one. */
    public static String choice(String prompt, String... options) {
        for (int i = 0; i < options.length; i++)
            System.out.println("   " + (i + 1) + ". " + options[i]);
        while (true) {
            int n = integer(prompt);
            if (n >= 1 && n <= options.length) return options[n - 1];
            System.out.println("  ! Choose 1-" + options.length + ".");
        }
    }

    public static boolean yes(String prompt) {
        return str(prompt + " (y/n): ").toLowerCase().startsWith("y");
    }

    public static void pause() { str("\nPress ENTER to continue...", false); }
}
