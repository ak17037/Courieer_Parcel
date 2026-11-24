# Courier & Parcel Logistics Management System
**Course:** 25CSE0202 – Programming in Java  
**Evaluation:** Continuous Evaluation (CE) & Multithreading Viva  
**Institution:** Chitkara University  
**Lead Member:** Vishavjeet Singh Chauhan (Roll No: 2510992871 | Section: G8)  

---

## 🏛️ Part 1: The "Union" Files (Must-Know by ALL 3 Members)

Because the evaluation tests knowledge individually, any member can be asked about the foundation of the project. These **6 core files** form the shared backbone of the entire application that every team member must understand:

```
                          ┌─────────────┐
                          │  Main.java  │ (Starts app, boots Daemon & displays Main Menu)
                          └──────┬──────┘
                                 │
                 ┌───────────────┴───────────────┐
                 ▼                               ▼
        ┌─────────────────┐             ┌─────────────────┐
        │    Menu.java    │             │  DataStore.java │
        │ (Menu Navigation│             │  (Synchronized  │
        │  & Action Loop) │             │   Collections)  │
        └────────┬────────┘             └────────┬────────┘
                 │                               │
                 ▼                               ▼
        ┌─────────────────┐             ┌─────────────────┐
        │     In.java     │             │   Parcel.java   │
        │ (Input Scanner  │             │ (Central Domain │
        │  & Validation)  │             │     Entity)     │
        └─────────────────┘             └────────┬────────┘
                                                 │
                                                 ▼
                                        ┌─────────────────┐
                                        │ThreadingMenu.java│
                                        │(Multithreading  │
                                        │ Concurrency Lab)│
                                        └─────────────────┘
```

| Union File | What It Does | Why Every Member Must Know It |
|---|---|---|
| `Main.java` | Application Entry Point (`public static void main`) | Shows how the program boots up, starts `TransitMonitorDaemon`, prints banner, and launches interactive `Menu`. |
| `DataStore.java` | Central Thread-Safe Storage Engine | Replaces MySQL/JDBC. Uses `synchronized` methods/blocks on intrinsic locks to protect collections from race conditions. |
| `Menu.java` | Generic Console Menu Engine | Every menu screen uses this. Uses `@FunctionalInterface Action { void run() throws Exception; }` and catches exceptions centrally. |
| `In.java` | Defensive Input Helper | Wraps `Scanner` in `try-catch` loops (`NumberFormatException`) so invalid letters or numbers don't break the application. |
| `Parcel.java` | The Central Domain Entity | The core business model representing parcels (Sender, Receiver, Dimensions, Weight, Status, and Waybill). |
| `ThreadingMenu.java` | Concurrency & Multithreading Lab | Central interactive hub running all live demonstrations for the university evaluator. |

---

## 📦 Part 2: The 60-Second "Parcel Journey" (Every Member Must Memorize)

If the evaluator asks: *"Trace what happens when a parcel moves through your system"*, any team member should be able to walk through this exact sequence:

1. **Booking & Dynamic Pricing (`ParcelMenu.java`):**
   - User inputs dimensions ($L, W, H$ in cm) and actual weight ($kg$).
   - Volumetric Weight $= \frac{L \times W \times H}{5000}$.
   - Chargeable Weight $= \max(\text{Actual Weight}, \text{Volumetric Weight})$.
   - Rate slab applied + $15\%$ Fuel Surcharge + $18\%$ GST.
   - Waybill generated using `WaybillGenerator` (Luhn Mod-10 algorithm).
   - Initial status logged as **`"BOOKED"`**.

2. **Loading & Vehicle Dispatch (`FleetMenu.java`):**
   - Assigned to a `Trip` whose source matches parcel's current facility.
   - Capacity check: $\text{remaining} = \text{capacity} - \text{loadedWeight}$.
   - When dispatched, parcel status transitions to **`"DEPARTED_FACILITY"`**.

3. **Arrival at Destination (`FleetMenu.java`):**
   - When trip arrives, parcel status transitions to **`"ARRIVED_AT_FACILITY"`**.

4. **Final Delivery & Auditing (`TrackingMenu.java`):**
   - Delivery agent assigned: status becomes **`"OUT_FOR_DELIVERY"`**.
   - Upon handover: status becomes **`"DELIVERED"`**.
   - Timestamped `TrackingEvent` stored in synchronized `DataStore`.

---

## 🎯 Part 3: Individual Team Division Matrix

### Member 1: Vishavjeet Singh Chauhan (Roll No: 2510992871)
* **Role:** System Architecture, Master Data & Shared Data Synchronization
* **Shared Union Files:** `Main.java`, `DataStore.java`, `Menu.java`, `In.java`, `Parcel.java`, `ThreadingMenu.java`
* **Specialized Deep-Dive Files:**
  1. `Customer.java` — Customer POJO with multi-valued phone numbers (`ArrayList<String>`), encapsulation, `this`, `toString()`.
  2. `CustomerMenu.java` — Customer registration, phone appending, search filters, parcel history.
  3. `Facility.java` — Hub vs. Branch facility hierarchy, regional zone distribution.
  4. `FacilityMenu.java` — Facility overview and staff assignments per location.
  5. `Employee.java` — Staff roles (Manager, Branch Staff, Delivery Agent, Driver with driving license check).
  6. **`TransitMonitorDaemon.java`** — Background Daemon Thread (`setDaemon(true)`), `Thread.MIN_PRIORITY`, heartbeat monitor, execution tracing.
* **Key Syllabus Topics to Defend:** OOP Fundamentals, Constructors, Encapsulation, Functional Interfaces (`@FunctionalInterface`), Input Validation (`try-catch`), `HashMap` for $O(1)$ lookups, `TreeMap` for sorted facilities, **Shared Data Synchronization, Race Conditions, Critical Sections, Intrinsic Locks (`synchronized` in `DataStore.java`), and Daemon Threads**.

---

### Member 2: Teammate 2
* **Role:** Logistics Operations, Fleet Management & Thread Lifecycle
* **Shared Union Files:** `Main.java`, `DataStore.java`, `Menu.java`, `In.java`, `Parcel.java`, `ThreadingMenu.java`
* **Specialized Deep-Dive Files:**
  1. `Vehicle.java` — Fleet assets, payload capacity in kg, maintenance status tracking.
  2. `Trip.java` — Inter-facility transport carrying multiple parcel waybills in an `ArrayList<String>`.
  3. `FleetMenu.java` — Vehicle scheduling, driver assignment, payload capacity checks, dispatch & arrival transitions.
  4. `TrackingEvent.java` — Milestone audit trail implementing **`Comparable<TrackingEvent>`**.
  5. `TrackingMenu.java` — Real-time tracking history display, facility check-in/out, and delivery completion.
  6. **`TransitSimulationThread.java` & `ThreadLifecycleDemo.java`** — Subclassing `Thread`, `start()` vs `run()`, `sleep()`, `join()`, `interrupt()`, and real-time tracing of all 6 thread states (`NEW`, `RUNNABLE`, `TIMED_WAITING`, `WAITING`, `BLOCKED`, `TERMINATED`).
* **Key Syllabus Topics to Defend:** `Comparable` Interface & `compareTo()`, Wrapper classes, State Lifecycle Management, **Thread Class Subclassing, start() vs run(), Thread.sleep(), join(), interrupt() & InterruptedException handling, and the 6 Thread Lifecycle States**.

---

### Member 3: Teammate 3
* **Role:** Commercial Pricing Engine, Concurrency Benchmarking & Inter-Thread Communication
* **Shared Union Files:** `Main.java`, `DataStore.java`, `Menu.java`, `In.java`, `Parcel.java`, `ThreadingMenu.java`
* **Specialized Deep-Dive Files:**
  1. `WaybillGenerator.java` — Generates barcode tracking numbers using the **Luhn Mod-10 algorithm**.  
  2. `TablePrinter.java` — Formats dynamic ASCII console tables with auto-sized columns using **`StringBuilder`**.
  3. `ParcelMenu.java` — Complete booking workflow, volumetric math, rate slab pricing, fuel surcharge, and GST.
  4. `Payment.java` — 1-to-1 payment records, payment modes (Cash, Card, UPI, Net Banking), receipt generation.
  5. `ReportMenu.java` — Business analytics, revenue aggregation by method, top customer rankings using **`Comparator`**.
  6. **`BatchProcessingTask.java` & `ParcelDispatchQueue.java`** — Implementing `Runnable`, Sequential vs Concurrent benchmark, Concurrency vs Parallelism, and Inter-thread Communication (`wait()`, `notifyAll()`, guarded `while` blocks, Producer-Consumer bounded buffer).
* **Key Syllabus Topics to Defend:** `Comparator` Interface with lambdas, `StringBuilder`, Luhn Algorithm, **Runnable Interface, Sequential vs Concurrent Execution, Concurrency vs Parallelism, Inter-Thread Communication (`wait()`, `notifyAll()`), Guarded Blocks, and Producer-Consumer Coordination**.
