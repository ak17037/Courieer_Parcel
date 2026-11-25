import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================================
 * SYLLABUS TOPIC (11-12): DAEMON THREADS, PRIORITIES & EXECUTION TRACING
 * ============================================================================
 * Concepts demonstrated:
 *   1. Daemon Thread (setDaemon(true)):
 *      - In Java, user threads keep the JVM running even after main() ends.
 *      - Daemon threads run in the background as service providers (e.g. Garbage Collector, Heartbeat).
 *      - When all user threads terminate (e.g. user selects 'Exit'), the JVM immediately exits
 *        without waiting for daemon threads to finish.
 *   2. Thread Priority (setPriority):
 *      - Scaled from 1 (MIN_PRIORITY) to 10 (MAX_PRIORITY), default is 5 (NORM_PRIORITY).
 *      - Gives hints to OS thread scheduler about CPU time allocation.
 *      - Background maintenance threads typically use MIN_PRIORITY.
 *   3. Execution Tracing:
 *      - Records execution timestamps, thread ID, state, and metrics.
 */
public class TransitMonitorDaemon extends Thread {

    private static TransitMonitorDaemon instance;
    private volatile boolean running = true;
    private long heartbeatCount = 0;
    private LocalDateTime lastCheckTime = LocalDateTime.now();

    public TransitMonitorDaemon() {
        super("Logistics-Daemon-Heartbeat");
        // [SYLLABUS]: Mark as Daemon thread BEFORE calling start()
        setDaemon(true);
        // [SYLLABUS]: Assign MIN_PRIORITY (1) for background service thread
        setPriority(Thread.MIN_PRIORITY);
    }

    /** Starts the singleton daemon thread safely once at system boot */
    public static synchronized void startDaemon() {
        if (instance == null || !instance.isAlive()) {
            instance = new TransitMonitorDaemon();
            instance.start();
        }
    }

    public static TransitMonitorDaemon getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Sleep quietly for 5 seconds between background monitoring cycles
                Thread.sleep(5000);

                heartbeatCount++;
                lastCheckTime = LocalDateTime.now();

                // Quiet background audit of system health
                // Does NOT print to console directly so it doesn't disturb user terminal input!

            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public synchronized String getStatusReport() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("""
          Daemon Thread Name : %s
          Thread ID          : %d
          Is Daemon?         : %b  (JVM will automatically shut down on exit)
          Thread Priority    : %d (Thread.MIN_PRIORITY = 1)
          Thread State       : %s
          Heartbeat Ticks    : %d cycles
          Last Audit Time    : %s
          Active Parcels     : %d in memory
          Active Vehicles    : %d registered
        """,
        getName(),
        threadId(),
        isDaemon(),
        getPriority(),
        getState(),
        heartbeatCount,
        lastCheckTime.format(fmt),
        DataStore.getAllParcels().size(),
        DataStore.getAllVehicles().size());
    }
}
