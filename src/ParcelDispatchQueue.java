import java.util.LinkedList;
import java.util.Queue;

/**
 * ============================================================================
 * SYLLABUS TOPIC (15-16): INTER-THREAD COMMUNICATION & PRODUCER-CONSUMER
 * ============================================================================
 * Concepts demonstrated:
 *   1. wait()        - Releases the monitor lock and places current thread in WAITING state.
 *   2. notify()      - Wakes up a single thread waiting on this object's monitor.
 *   3. notifyAll()   - Wakes up all threads waiting on this object's monitor.
 *   4. Guarded Block - Loop condition 'while (condition) wait()' prevents spurious wakeups.
 *   5. Correct Monitor Usage:
 *      wait(), notify(), and notifyAll() MUST be invoked inside a synchronized context
 *      owning the intrinsic lock, or Java throws IllegalMonitorStateException.
 *
 * Logistics Real-World Context:
 *   - Producer Thread : Intake / Sorter Clerk books parcels into the Hub conveyor buffer.
 *   - Consumer Thread : Delivery Driver / Dispatch Van retrieves parcels for delivery.
 *   - Bounded Buffer  : Conveyor belt has a fixed physical capacity (e.g., 3 parcels).
 */
public class ParcelDispatchQueue {

    private final int capacity;
    private final Queue<String> buffer = new LinkedList<>();

    public ParcelDispatchQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Producer method: Adds a parcel waybill to the dispatch buffer.
     * Guarded block: If buffer is full, waits until consumer makes space.
     *
     * @param waybillNo The waybill number being produced
     * @param producerName Name of the producing worker/thread
     * @throws InterruptedException if interrupted while waiting
     */
    public synchronized void produce(String waybillNo, String producerName) throws InterruptedException {
        // [GUARDED BLOCK]: Always use while-loop instead of if-statement to handle spurious wakeups
        while (buffer.size() == capacity) {
            System.out.printf("  [PRODUCER: %s] Buffer FULL (%d/%d). Entering WAITING state via wait()...%n",
                    producerName, buffer.size(), capacity);
            // wait() releases the intrinsic lock of 'this' and waits to be notified
            wait();
        }

        // Critical section: modifying shared buffer
        buffer.add(waybillNo);
        System.out.printf("  [PRODUCER: %s] Enqueued %s. Buffer status: [%d/%d]%n",
                producerName, waybillNo, buffer.size(), capacity);

        // Notify all waiting consumers that a parcel is now available
        notifyAll();
    }

    /**
     * Consumer method: Removes and returns a parcel waybill from the dispatch buffer.
     * Guarded block: If buffer is empty, waits until producer enqueues a parcel.
     *
     * @param consumerName Name of the consuming vehicle / agent thread
     * @return The waybill number retrieved
     * @throws InterruptedException if interrupted while waiting
     */
    public synchronized String consume(String consumerName) throws InterruptedException {
        // [GUARDED BLOCK]: If buffer is empty, wait for producer
        while (buffer.isEmpty()) {
            System.out.printf("  [CONSUMER: %s] Buffer EMPTY (0/%d). Entering WAITING state via wait()...%n",
                    consumerName, capacity);
            // wait() releases the intrinsic lock of 'this' and waits to be notified
            wait();
        }

        // Critical section: modifying shared buffer
        String item = buffer.poll();
        System.out.printf("  [CONSUMER: %s] Dispatched %s. Buffer status: [%d/%d]%n",
                consumerName, item, buffer.size(), capacity);

        // Notify all waiting producers that space is now available in the buffer
        notifyAll();
        return item;
    }

    public synchronized int getCount() {
        return buffer.size();
    }

    public int getCapacity() {
        return capacity;
    }
}
