import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * INTERACTIVE MULTITHREADING & CONCURRENCY LAB (SYLLABUS 9 - 16)
 * ============================================================================
 * Provides an interactive testing ground directly mapped to university syllabus:
 *   1. Thread & Runnable: Transit Simulation (start, sleep, join, trace)
 *   2. Thread Interruption: Emergency Trip Diversion (interrupt)
 *   3. Thread Lifecycle: Trace all 6 states (NEW, RUNNABLE, TIMED_WAITING, etc.)
 *   4. Sequential vs Concurrent Benchmark (Concurrency vs Parallelism)
 *   5. Shared Data & Race Condition vs Synchronized (Critical Sections & Locks)
 *   6. Producer-Consumer Coordination (wait, notifyAll, guarded blocks)
 *   7. Background Daemon Monitor & Priorities (setDaemon, MIN_PRIORITY)
 *   8. Quick Reference & Viva Questions Guide
 */
public class ThreadingMenu {

    public static void show() {
        new Menu("MULTITHREADING & CONCURRENCY LAB")
            .add("Transit Simulation: Thread, start(), sleep(), join()", ThreadingMenu::demoTransit)
            .add("Thread Interruption: interrupt() and graceful halt",   ThreadingMenu::demoInterrupt)
            .add("Thread Lifecycle: Trace all 6 states in real time",    ThreadingMenu::demoLifecycle)
            .add("Sequential vs Concurrent Execution Benchmark",         ThreadingMenu::demoBenchmark)
            .add("Race Condition vs Synchronized Critical Sections",     ThreadingMenu::demoRaceCondition)
            .add("Producer-Consumer Hub: wait(), notifyAll() coordination", ThreadingMenu::demoProducerConsumer)
            .add("Background Daemon Monitor & Priority Status",          ThreadingMenu::demoDaemonStatus)
            .add("Threading Syllabus Viva & Theory Guide",               ThreadingMenu::showTheoryGuide)
            .show();
    }

    // =========================================================================
    // 1. THREAD CLASS, START, SLEEP, JOIN, TRACING
    // =========================================================================
    static void demoTransit() throws Exception {
        System.out.println("  Demonstrating subclassing Thread, start(), sleep(), join(), and execution tracing.");
        System.out.println("  Simulating transit for Vehicle PB10AB1234 from Chandigarh Hub to Delhi Hub...\n");

        TransitSimulationThread transit = new TransitSimulationThread(
                101, "PB10AB1234", "Chandigarh Hub -> Delhi Hub (250 km)", 5, 400);

        // Demonstrating priority
        transit.setPriority(Thread.NORM_PRIORITY);

        System.out.println("  [MAIN THREAD]: Before calling start(): Thread State = " + transit.getState());
        System.out.println("  [MAIN THREAD]: Calling transit.start() to spawn a new concurrent worker thread...");

        transit.start();

        System.out.println("  [MAIN THREAD]: Calling transit.join() -> main will wait until worker reaches TERMINATED...");
        transit.join(); // [SYLLABUS]: join() blocks until thread finishes

        System.out.println("\n  [MAIN THREAD]: transit.join() returned! Worker State = " + transit.getState());
        System.out.println("  Simulation completed with result = " + (transit.isCompletedSuccessfully() ? "SUCCESS" : "HALTED"));
    }

    // =========================================================================
    // 2. THREAD INTERRUPTION
    // =========================================================================
    static void demoInterrupt() throws Exception {
        System.out.println("  Demonstrating thread interruption via interrupt() and InterruptedException handling.");
        System.out.println("  Simulating long-distance trip PB02EF9012 (Ludhiana to Ahmedabad, 1200 km)...");

        TransitSimulationThread transit = new TransitSimulationThread(
                202, "PB02EF9012", "Ludhiana -> Ahmedabad (Express)", 10, 500);

        transit.start();

        // Main thread waits 1.2 seconds, then issues an emergency interrupt
        System.out.println("  [MAIN THREAD]: Simulating emergency weather alert after 1.2 seconds...");
        Thread.sleep(1200);

        System.out.println("  [MAIN THREAD]: Calling transit.interrupt()...\n");
        transit.interrupt(); // [SYLLABUS]: Signal thread to abort

        transit.join();
        System.out.println("\n  [MAIN THREAD]: Worker thread successfully handled interrupt and stopped safely.");
    }

    // =========================================================================
    // 3. THREAD LIFECYCLE (ALL 6 STATES)
    // =========================================================================
    static void demoLifecycle() {
        ThreadLifecycleDemo.demonstrateAllStates();
    }

    // =========================================================================
    // 4. SEQUENTIAL VS CONCURRENT BENCHMARK
    // =========================================================================
    static void demoBenchmark() throws Exception {
        System.out.println("  Benchmarking Sequential Execution vs Concurrent Multi-threading.");
        System.out.println("  Task: Verifying and auditing 3 regional facility batches (20 items each).\n");

        BatchProcessingTask task1 = new BatchProcessingTask("Batch-North", 20, 30);
        BatchProcessingTask task2 = new BatchProcessingTask("Batch-West",  20, 30);
        BatchProcessingTask task3 = new BatchProcessingTask("Batch-South", 20, 30);

        // 1. Sequential execution (Single thread)
        System.out.println("  --- 1. SEQUENTIAL EXECUTION (Single Thread: main) ---");
        long startSeq = System.currentTimeMillis();
        task1.run();
        task2.run();
        task3.run();
        long timeSeq = System.currentTimeMillis() - startSeq;
        System.out.printf("  >> Sequential Elapsed Time: %d ms%n%n", timeSeq);

        // 2. Concurrent execution (3 worker threads)
        System.out.println("  --- 2. CONCURRENT EXECUTION (3 Worker Threads in Parallel) ---");
        BatchProcessingTask cTask1 = new BatchProcessingTask("Batch-North", 20, 30);
        BatchProcessingTask cTask2 = new BatchProcessingTask("Batch-West",  20, 30);
        BatchProcessingTask cTask3 = new BatchProcessingTask("Batch-South", 20, 30);

        Thread th1 = new Thread(cTask1, "Worker-North");
        Thread th2 = new Thread(cTask2, "Worker-West");
        Thread th3 = new Thread(cTask3, "Worker-South");

        long startCon = System.currentTimeMillis();
        th1.start();
        th2.start();
        th3.start();

        // Main thread waits for all 3 concurrent workers to finish via join()
        th1.join();
        th2.join();
        th3.join();
        long timeCon = System.currentTimeMillis() - startCon;
        System.out.printf("  >> Concurrent Elapsed Time: %d ms%n%n", timeCon);

        // Summary
        double speedup = (double) timeSeq / Math.max(timeCon, 1);
        System.out.println("  ================ BENCHMARK RESULT ================");
        System.out.printf("  Sequential Time : %4d ms (tasks executed back-to-back)%n", timeSeq);
        System.out.printf("  Concurrent Time : %4d ms (tasks executed simultaneously)%n", timeCon);
        System.out.printf("  Speedup Factor  : %.2fx faster!%n", speedup);
        System.out.println("  Demonstrates Concurrency vs Parallelism on modern multi-core CPUs.");
    }

    // =========================================================================
    // 5. RACE CONDITION VS SYNCHRONIZATION
    // =========================================================================
    static void demoRaceCondition() throws Exception {
        System.out.println("  Demonstrating Race Conditions on Shared Data vs Synchronized Critical Sections.");
        System.out.println("  Scenario: 2 Concurrent Booking Desks updating parcel count 10,000 times each.");
        System.out.println("  Expected total = 20,000 increments.\n");

        DataStore.resetDemoCounters();

        // --- TEST A: UNSYNCHRONIZED (Race Condition) ---
        Thread desk1Unsafe = new Thread(() -> {
            for (int i = 0; i < 10000; i++) DataStore.unsafeIncrement();
        }, "Desk-1-Unsafe");

        Thread desk2Unsafe = new Thread(() -> {
            for (int i = 0; i < 10000; i++) DataStore.unsafeIncrement();
        }, "Desk-2-Unsafe");

        desk1Unsafe.start();
        desk2Unsafe.start();
        desk1Unsafe.join();
        desk2Unsafe.join();

        int unsafeResult = DataStore.getUnsafeCounter();
        int lostUpdates = 20000 - unsafeResult;

        System.out.printf("  [TEST A: UNSYNCHRONIZED] Final Count: %d / 20000 (Lost %d updates due to Race Condition!)%n",
                unsafeResult, lostUpdates);

        // --- TEST B: SYNCHRONIZED (Thread-Safe) ---
        Thread desk1Safe = new Thread(() -> {
            for (int i = 0; i < 10000; i++) DataStore.safeIncrementMethod();
        }, "Desk-1-Safe");

        Thread desk2Safe = new Thread(() -> {
            for (int i = 0; i < 10000; i++) DataStore.safeIncrementBlock();
        }, "Desk-2-Safe");

        desk1Safe.start();
        desk2Safe.start();
        desk1Safe.join();
        desk2Safe.join();

        int safeResult = DataStore.getSafeCounter();
        System.out.printf("  [TEST B: SYNCHRONIZED]   Final Count: %d / 20000 (100%% EXACT - Mutual Exclusion & Atomicity guaranteed!)%n%n",
                safeResult);

        System.out.println("  Key Takeaway: 'synchronized' creates an intrinsic lock (monitor) ensuring only");
        System.out.println("  one thread executes the critical section at a time, preventing race conditions.");
    }

    // =========================================================================
    // 6. PRODUCER-CONSUMER COORDINATION (WAIT & NOTIFYALL)
    // =========================================================================
    static void demoProducerConsumer() throws Exception {
        System.out.println("  Demonstrating Inter-thread Communication using wait(), notifyAll(), and Guarded Blocks.");
        System.out.println("  Scenario: Hub Conveyor Belt (Bounded Buffer capacity = 3 parcels).");
        System.out.println("  Producers : 2 Sorter Clerks booking 3 parcels each (total 6 parcels).");
        System.out.println("  Consumer  : 1 Dispatch Van picking up parcels to load.\n");

        ParcelDispatchQueue conveyor = new ParcelDispatchQueue(3);

        // Producer 1
        Thread producer1 = new Thread(() -> {
            String[] items = {"WB-101", "WB-102", "WB-103"};
            for (String wb : items) {
                try {
                    Thread.sleep(250);
                    conveyor.produce(wb, "Sorter-Chandigarh");
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "Sorter-Chandigarh");

        // Producer 2
        Thread producer2 = new Thread(() -> {
            String[] items = {"WB-201", "WB-202", "WB-203"};
            for (String wb : items) {
                try {
                    Thread.sleep(350);
                    conveyor.produce(wb, "Sorter-Delhi");
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "Sorter-Delhi");

        // Consumer
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    Thread.sleep(600); // Consumer is slower -> forces buffer to fill and producers to wait!
                    conveyor.consume("FleetVan-01");
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "FleetVan-01");

        producer1.start();
        producer2.start();
        consumer.start();

        producer1.join();
        producer2.join();
        consumer.join();

        System.out.println("\n  Producer-Consumer coordination finished successfully without deadlocks!");
        System.out.println("  Demonstrates guarded blocks 'while(buffer.isFull()) wait()' and notifyAll().");
    }

    // =========================================================================
    // 7. BACKGROUND DAEMON MONITOR STATUS
    // =========================================================================
    static void demoDaemonStatus() {
        TransitMonitorDaemon daemon = TransitMonitorDaemon.getInstance();
        if (daemon == null) {
            System.out.println("  TransitMonitorDaemon not yet running.");
            return;
        }

        System.out.println("  === LIVE BACKGROUND DAEMON STATUS ===");
        System.out.println(daemon.getStatusReport());
        System.out.println("  [SYLLABUS NOTE]: This thread was configured with daemon.setDaemon(true).");
        System.out.println("  When all user threads exit, the JVM terminates this daemon automatically.");
    }

    // =========================================================================
    // 8. SYLLABUS THEORY & VIVA GUIDE
    // =========================================================================
    static void showTheoryGuide() {
        System.out.println("""
          ============================================================================
                         JAVA MULTITHREADING SYLLABUS VIVA QUICK-REFERENCE
          ============================================================================

          1. Multiprogramming vs Multithreading:
             - Multiprogramming: Multiple distinct programs loaded in memory; CPU switches
               between processes. Each has separate address space.
             - Multithreading: Multiple concurrent execution paths within the SAME process.
               Threads share memory (e.g. heap, DataStore), reducing overhead.

          2. Program vs Process vs Thread:
             - Program: Passive set of instructions stored on disk (e.g. Main.java/Main.class).
             - Process: An active program instance in execution with allocated memory space.
             - Thread: The smallest unit of CPU execution inside a process (lightweight process).

          3. Concurrency vs Parallelism:
             - Concurrency: Managing multiple tasks at once by interleaving execution.
             - Parallelism: Executing multiple tasks simultaneously on multiple physical CPU cores.

          4. Thread Class vs Runnable Interface:
             - Subclassing Thread: Simple, but Java doesn't support multiple class inheritance.
             - Implementing Runnable: Best practice; separates task logic from the execution
               thread, allowing the class to extend another class.

          5. start() vs run():
             - run(): Normal method call. Executes synchronously on the CURRENT thread.
             - start(): Requests JVM/OS to allocate a new call stack and launch a NEW thread.

          6. Thread.sleep() vs Object.wait():
             - sleep(): Belongs to Thread class; pauses thread for specified time; DOES NOT
               release any monitor lock.
             - wait(): Belongs to Object class; pauses thread indefinitely until notified;
               RELEASES the monitor lock. Must be called inside synchronized context!

          7. Thread Lifecycle (6 States):
             NEW -> RUNNABLE -> (TIMED_WAITING / WAITING / BLOCKED) -> TERMINATED

          8. Race Conditions & Critical Sections:
             - Critical Section: Code accessing shared mutable data.
             - Race Condition: Uncoordinated concurrent access producing unpredictable output.
             - Solution: 'synchronized' keyword on methods or blocks using intrinsic locks.

          9. Daemon Threads:
             - Background service threads (setDaemon(true)).
             - Do not prevent the JVM from exiting when main user threads finish.
          ============================================================================
        """);
    }
}
