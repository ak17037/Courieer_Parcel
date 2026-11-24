import java.util.ArrayList;
import java.util.List;

/**
 * A numbered terminal menu.  Option 0 always goes back / exits.
 * Concepts: functional interface (Action), ArrayList, polymorphism, exception handling.
 */
public class Menu {

    /** Functional interface for menu actions */
    @FunctionalInterface
    public interface Action { void run() throws Exception; }

    private final String title;
    private final String backLabel;
    private final List<String>  labels     = new ArrayList<>();
    private final List<Action>  actions    = new ArrayList<>();
    private final List<Boolean> pauseAfter = new ArrayList<>();

    public Menu(String title)                    { this(title, "Back"); }
    public Menu(String title, String backLabel)  { this.title = title; this.backLabel = backLabel; }

    /** Normal action: waits for ENTER after it runs. */
    public Menu add(String label, Action action) { return add(label, action, true); }
    /** Sub-menu: no pause after returning from it. */
    public Menu sub(String label, Action action)  { return add(label, action, false); }

    private Menu add(String label, Action action, boolean pause) {
        labels.add(label); actions.add(action); pauseAfter.add(pause);
        return this;
    }

    public void show() {
        while (true) {
            System.out.println();
            System.out.println("=".repeat(56));
            System.out.println("  " + title);
            System.out.println("=".repeat(56));
            for (int i = 0; i < labels.size(); i++)
                System.out.printf("  %2d. %s%n", i + 1, labels.get(i));
            System.out.printf("  %2d. %s%n", 0, backLabel);
            System.out.println("-".repeat(56));

            int n = In.integer("Enter choice: ");
            if (n == 0) return;
            if (n < 1 || n > labels.size()) { System.out.println("  ! Invalid choice."); continue; }
            System.out.println("\n--- " + labels.get(n - 1) + " ---");
            try {
                actions.get(n - 1).run();
            } catch (Exception e) {
                if ("Cancelled.".equals(e.getMessage())) System.out.println("  (cancelled)");
                else System.out.println("\n  X ERROR: " + e.getMessage());
            }
            if (pauseAfter.get(n - 1)) In.pause();
        }
    }
}
