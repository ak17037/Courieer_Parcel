/**
 * ============================================================================
 * SYLLABUS TOPIC (9-10): RUNNABLE INTERFACE & CONCURRENT VS SEQUENTIAL BENCHMARK
 * ============================================================================
 * Concepts demonstrated:
 *   1. Implementing java.lang.Runnable:
 *      Separates the task/job (Runnable) from the execution vehicle (Thread).
 *      Favored when a class already extends another superclass.
 *   2. Sequential vs Concurrent Execution:
 *      - Sequential: Tasks run one after another on a single thread (T_total = Sum of times).
 *      - Concurrent: Tasks run concurrently on multiple threads, overlapping execution.
 *   3. Concurrency vs Parallelism:
 *      - Concurrency: Multiple tasks making progress in overlapping time frames.
 *      - Parallelism: Multiple tasks executing simultaneously on multiple physical CPU cores.
 *   4. Coordinating multiple threads with join() to aggregate results.
 */
public class BatchProcessingTask implements Runnable {

    private final String batchName;
    private final int itemsToProcess;
    private final long simulatedWorkloadMs;
    private int processedCount = 0;

    public BatchProcessingTask(String batchName, int itemsToProcess, long simulatedWorkloadMs) {
        this.batchName = batchName;
        this.itemsToProcess = itemsToProcess;
        this.simulatedWorkloadMs = simulatedWorkloadMs;
    }

    @Override
    public void run() {
        Thread current = Thread.currentThread();
        System.out.printf("  [Task: %-15s | Thread: %-20s | ID: %2d] Started processing %d items...%n",
                batchName, current.getName(), current.threadId(), itemsToProcess);

        for (int i = 1; i <= itemsToProcess; i++) {
            try {
                // Simulate I/O or algorithmic work (e.g. barcode generation or checksum calculation)
                Thread.sleep(simulatedWorkloadMs);
                processedCount++;
            } catch (InterruptedException e) {
                System.out.printf("  [Task: %s] Processing interrupted at item %d.%n", batchName, i);
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("  [Task: %-15s | Thread: %-20s] FINISHED. Total items processed: %d%n",
                batchName, current.getName(), processedCount);
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public String getBatchName() {
        return batchName;
    }
}
