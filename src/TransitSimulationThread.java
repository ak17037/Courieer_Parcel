/**
 * ============================================================================
 * SYLLABUS TOPIC (9-10 & 11-12): THREAD CLASS, SLEEP, JOIN, INTERRUPT & PRIORITY
 * ============================================================================
 * Concepts demonstrated:
 *   1. Creating a Thread by subclassing java.lang.Thread.
 *   2. Overriding the run() method for concurrent execution logic.
 *   3. start() vs run():
 *        - Calling run() is synchronous (executes on the calling thread).
 *        - Calling start() spawns a new thread of execution with its own call stack.
 *   4. Thread.sleep(millis): Puts the thread into TIMED_WAITING state.
 *   5. thread.join(): Blocks caller until this thread reaches TERMINATED state.
 *   6. thread.interrupt(): Sets the interrupt flag; wakes up sleeping thread with InterruptedException.
 *   7. Thread Priority: setPriority(Thread.MIN_PRIORITY .. Thread.MAX_PRIORITY).
 *   8. Execution Tracing: Logging thread name, ID, priority, and state.
 */
public class TransitSimulationThread extends Thread {

    private final int tripId;
    private final String regNumber;
    private final String routeDescription;
    private final int totalMilestones;
    private final long delayPerMilestoneMs;
    private volatile boolean completedSuccessfully = false;

    public TransitSimulationThread(int tripId, String regNumber, String routeDescription,
                                   int totalMilestones, long delayPerMilestoneMs) {
        // Name the thread for clear execution tracing
        super("TransitWorker-Trip-" + tripId);
        this.tripId = tripId;
        this.regNumber = regNumber;
        this.routeDescription = routeDescription;
        this.totalMilestones = totalMilestones;
        this.delayPerMilestoneMs = delayPerMilestoneMs;
    }

    @Override
    public void run() {
        printTrace("RUNNING: Started transit simulation along route " + routeDescription);

        try {
            for (int milestone = 1; milestone <= totalMilestones; milestone++) {
                // Check if someone interrupted this thread before sleeping
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Interruption signal detected before milestone " + milestone);
                }

                // Simulate travel between logistics checkpoints
                // [SYLLABUS]: Thread.sleep() puts thread into TIMED_WAITING state
                Thread.sleep(delayPerMilestoneMs);

                int progressPct = (milestone * 100) / totalMilestones;
                printTrace(String.format("PROGRESS: Vehicle %s reached checkpoint %d/%d (%d%% complete)",
                        regNumber, milestone, totalMilestones, progressPct));
            }

            completedSuccessfully = true;
            printTrace("COMPLETED: Vehicle " + regNumber + " successfully arrived at destination.");

        } catch (InterruptedException e) {
            // [SYLLABUS]: Handling interrupt() and InterruptedException gracefully
            completedSuccessfully = false;
            printTrace("INTERRUPTED: Emergency transit halt / diversion triggered! Safe stopping sequence initiated.");
            // Restore interrupted flag if needed by callers
            Thread.currentThread().interrupt();
        }
    }

    /** Helper for execution tracing showing thread metadata */
    private void printTrace(String message) {
        Thread curr = Thread.currentThread();
        System.out.printf("  [Thread: %-22s | ID: %2d | Priority: %2d | State: %-13s] %s%n",
                curr.getName(), curr.threadId(), curr.getPriority(), curr.getState(), message);
    }

    public boolean isCompletedSuccessfully() {
        return completedSuccessfully;
    }

    public int getTripId() {
        return tripId;
    }
}
