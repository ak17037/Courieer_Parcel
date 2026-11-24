# Courier & Parcel Logistics Management System

**Course:** 25CSE0202 – Programming in Java
**Institution:** Chitkara University
**Evaluation:** CE-1 + Multithreading Module

---

## What is this project?

So basically we made a courier and parcel management system — the kind of thing companies like DTDC or Blue Dart use to track packages. Ours runs fully in the terminal, no database, no fancy frameworks. Everything is stored in Java collections in memory and the program comes with some pre-loaded sample data so you can start using it right away.

The whole point was to cover every Java topic from our syllabus in one working project. Not just write classes for the sake of it — actually use them in a real workflow. A parcel gets booked, loaded onto a vehicle, dispatched, tracked, delivered. Each step touches different parts of the code.

---

## How to run it

Make sure you have Java installed (JDK 11 or above works fine).

**Easiest way — just double click:**
```
scripts\compile.bat    (compiles everything)
scripts\run.bat        (runs the app)
```

**Or from terminal manually:**
```cmd
javac -d out src\*.java
java -cp out Main
```

**PowerShell users:**
```powershell
.\scripts\run.ps1
```

---

## Project structure

```
src/          all .java source files (27 files)
out/          compiled .class files go here after compile.bat
scripts/      compile.bat, run.bat, run.ps1
```

All source files are in the default package under `src/` — no subfolders needed, one `javac` command compiles everything.

---

## The files and what they do

### Model classes (the data)

| File | What it represents |
|---|---|
| `Customer.java` | A person who sends or receives parcels. Stores name, address, and multiple phone numbers using `ArrayList<String>` |
| `Parcel.java` | The actual package — dimensions, weight, status, waybill number, sender, receiver |
| `Facility.java` | A Hub or Branch location. Hubs connect regions, Branches handle local pickups/deliveries |
| `Employee.java` | Staff — Manager, Branch Staff, Delivery Agent, or Driver. Drivers need a valid license number |
| `Vehicle.java` | Trucks or vans with a payload capacity in kg and a maintenance status |
| `Trip.java` | One vehicle journey between two facilities, carrying a list of parcel waybill numbers |
| `Payment.java` | Payment record for a parcel — mode (Cash, Card, UPI, Net Banking), amount, timestamp |
| `TrackingEvent.java` | A single checkpoint in a parcel's journey. Implements `Comparable` so events sort by time automatically |

### Core / utility (the engine)

| File | What it does |
|---|---|
| `Main.java` | Entry point. Prints the banner, starts the background daemon thread, opens the main menu |
| `DataStore.java` | Stores everything in memory using `HashMap`, `TreeMap`, `LinkedHashMap`, `ArrayList`. All write methods are `synchronized` so threads don't corrupt data |
| `Menu.java` | A reusable menu builder. Uses a `@FunctionalInterface` called `Action` so each menu option is just a lambda |
| `In.java` | Handles all keyboard input safely. Wraps `Scanner` with `try-catch` so if you type letters where a number is expected, it asks again instead of crashing |
| `TablePrinter.java` | Prints nice ASCII tables in the terminal with auto-sized columns using `StringBuilder` |
| `WaybillGenerator.java` | Generates unique tracking numbers using the Luhn Mod-10 checksum algorithm (same one credit cards use) |

### Menu screens (the interface)

| File | What you can do there |
|---|---|
| `CustomerMenu.java` | Register customers, add phone numbers, search by name or phone, view parcel history |
| `ParcelMenu.java` | Book a new parcel — enters dimensions, calculates volumetric weight, applies pricing slabs, fuel surcharge (15%), GST (18%) |
| `FacilityMenu.java` | View all hubs and branches, see employees assigned to each location |
| `FleetMenu.java` | Add vehicles, create trips, assign drivers, load parcels, dispatch and complete trips |
| `TrackingMenu.java` | Look up any parcel's full movement history, mark check-ins, mark delivery |
| `PaymentMenu.java` | Process payments, view receipts, see pending payments |
| `ReportMenu.java` | Business reports — revenue by payment method, top customers by spend, parcel counts |
| `ThreadingMenu.java` | The multithreading lab — interactive demos for every concurrency topic from the syllabus |

### Multithreading classes (the concurrency lab)

| File | What it demonstrates |
|---|---|
| `TransitSimulationThread.java` | A vehicle journey as a thread. Shows `Thread` subclassing, `start()` vs `run()`, `sleep()`, `join()`, `interrupt()` |
| `ThreadLifecycleDemo.java` | Walks a thread through all 6 states live: NEW → RUNNABLE → TIMED_WAITING → WAITING → BLOCKED → TERMINATED |
| `BatchProcessingTask.java` | Implements `Runnable`. Runs the same 3 tasks sequentially then concurrently and shows the time difference |
| `ParcelDispatchQueue.java` | A bounded buffer (capacity 3). Two producer threads load parcels, one consumer thread dispatches them. Uses `wait()` and `notifyAll()` |
| `TransitMonitorDaemon.java` | A background daemon thread that monitors fleet health. Set with `setDaemon(true)` and `Thread.MIN_PRIORITY` — JVM doesn't wait for it on exit |

---

## Java syllabus topics and where they show up

### Core OOP and language topics

**Classes, Objects, Constructors, `this`**
Every model class has a parameterized constructor and uses `this` to distinguish instance variables from parameters. Example — `Customer.java` has `this.name = name` throughout.

**Encapsulation**
All fields in model classes are `private`. Access is only through getters and setters. `Customer`, `Parcel`, `Vehicle`, `Employee`, `Facility` all follow this.

**Interfaces**
- `TrackingEvent` implements `Comparable<TrackingEvent>` — `compareTo()` sorts by timestamp
- `BatchProcessingTask` implements `Runnable` — task given to a thread
- `Menu.java` defines its own `@FunctionalInterface Action` — used as a lambda target

**Inheritance and Polymorphism**
`TransitSimulationThread` extends `Thread`. Overrides `run()`. The `start()` call creates a new thread stack, calling `run()` directly doesn't.

**`static` keyword**
`DataStore` stores all collections as `static` so every class shares the same in-memory data without needing to pass objects around.

**Wrapper Classes**
`Integer.parseInt()`, `Double.parseDouble()` used in `In.java`. `NumberFormatException` caught when parsing fails.

**Exception Handling**
`In.java` catches `NumberFormatException` and `DateTimeParseException` in loops. `Menu.java` wraps every action in `try-catch` and prints the error without crashing the whole app.

**String and StringBuilder**
`TablePrinter.java` builds entire formatted tables using `StringBuilder` — appending borders, headers, and rows without creating intermediate String objects. `WaybillGenerator` uses `String.format` and character-level operations.

**Collections Framework**

| Collection | Used where | Why that one |
|---|---|---|
| `HashMap<String, Customer>` | Customer, Parcel, Vehicle lookups | O(1) average lookup by ID or waybill |
| `TreeMap<String, Facility>` | Facility storage | Keeps facilities sorted by name automatically |
| `LinkedHashMap` | Payment records | Preserves insertion order for receipts |
| `ArrayList<String>` | Phone numbers, waybill lists in trips | Ordered, allows duplicates, easy to iterate |

**Sorting — `Comparable` and `Comparator`**
- `TrackingEvent` implements `Comparable<TrackingEvent>` — `Collections.sort()` just works on a list of events
- `ReportMenu` uses `Comparator` via lambda `(a, b) -> Double.compare(b.getValue(), a.getValue())` to rank customers by spend

**Functional Interface and Lambdas**
`Menu.java` defines `@FunctionalInterface Action { void run() throws Exception; }`. Every menu option is registered as a lambda:
```java
menu.add("Book a parcel", () -> ParcelMenu.bookParcel());
```

---

### Multithreading topics — full syllabus coverage

**What is a Thread / Process / Program**
A `.class` file sitting on disk is a passive program. When JVM loads it, that's a process. Inside that process, multiple threads run concurrently — sharing heap memory (DataStore) but with their own stack.

**`Thread` class — subclassing**
`TransitSimulationThread extends Thread`. Override `run()` with the actual work. Call `start()` to let JVM create a new thread stack and call `run()` on it.

**`Runnable` interface**
`BatchProcessingTask implements Runnable`. Pass to `new Thread(task)`. Preferred when your class already extends something else. Cleaner separation of task from thread.

**`start()` vs `run()`**
Calling `thread.run()` is just a regular method call — same thread, blocks everything. Calling `thread.start()` tells JVM to create a new OS-level thread and call `run()` there. Demo in `TransitSimulationThread.java` and `ThreadingMenu`.

**`Thread.sleep(ms)`**
Used in `TransitSimulationThread` to simulate travel time between facilities. Thread enters `TIMED_WAITING` state. Throws `InterruptedException` if interrupted while sleeping.

**`join()`**
Main thread calls `thread.join()` to wait for the simulation thread to finish before printing the final result. Without `join()`, the main thread would race ahead.

**`interrupt()` and `InterruptedException`**
`TransitSimulationThread` checks `Thread.currentThread().isInterrupted()` and handles `InterruptedException` in its `sleep()` calls — simulates an emergency halt mid-journey.

**Thread Lifecycle — all 6 states**
`ThreadLifecycleDemo.java` moves a thread through all states and prints `thread.getState()` at each point:

```
NEW → RUNNABLE → TIMED_WAITING → WAITING → BLOCKED → TERMINATED
```

| State | When |
|---|---|
| `NEW` | `new Thread(...)` called, `start()` not yet |
| `RUNNABLE` | `start()` called, running or ready to run |
| `TIMED_WAITING` | Inside `sleep(ms)` or `join(timeout)` |
| `WAITING` | Inside `wait()` with no timeout |
| `BLOCKED` | Waiting to acquire a `synchronized` lock another thread holds |
| `TERMINATED` | `run()` method returned or threw uncaught exception |

**Daemon Threads**
`TransitMonitorDaemon` is started in `Main.java` with:
```java
daemon.setDaemon(true);
daemon.setPriority(Thread.MIN_PRIORITY);
daemon.start();
```
Daemon threads are background threads. JVM exits normally even if they're still running — it doesn't wait for them. If all non-daemon threads finish, daemon threads are killed automatically.

**Race Conditions and Shared Data**
Two threads incrementing the same counter without synchronization — some increments get lost (race condition). The demo in `ThreadingMenu` shows the counter ending up at something like 17,842 instead of 20,000. Then with `synchronized`, it always hits exactly 20,000.

**Critical Sections and Intrinsic Locks**
Every method in `DataStore` that modifies a collection is `synchronized`. The JVM gives each object an intrinsic lock — only one thread can hold it at a time. This protects the critical section.

```java
public synchronized void addParcel(Parcel p) {
    parcels.put(p.getWaybill(), p);
}
```

**`wait()`, `notifyAll()`, and Guarded Blocks**
`ParcelDispatchQueue` is a bounded buffer with capacity 3. Two producer threads add parcels, one consumer takes them out.

- When buffer is full → producer calls `wait()` → releases lock → goes to WAITING
- When consumer takes one → calls `notifyAll()` → wakes producers
- Guard condition is always a `while` loop, not `if` — protects against spurious wakeups:

```java
while (buffer.isFull()) wait();
```

**Sequential vs Concurrent Execution**
`BatchProcessingTask` runs the same 3 audit jobs on one thread sequentially — takes ~3x the time. Then spawns 3 threads concurrently — roughly 3x faster because work overlaps. The benchmark prints both times.

**Concurrency vs Parallelism**
Concurrency = multiple tasks making progress, possibly interleaving on one core. Parallelism = multiple tasks running at literally the same instant on multiple CPU cores. Our threads are concurrent — the JVM/OS decides if they run in parallel depending on available cores.

**Execution Tracing**
Every threading demo prints structured logs like:
```
[Thread: Transit-Delhi | ID: 14 | Priority: 5 | State: TIMED_WAITING]
```
So you can see exactly which thread is doing what at each moment.

---

## Team division

### Member 1 — Vishavjeet Singh Chauhan (Roll No: 2510992871)
**Role:** System Architecture, Master Data & Shared Data Synchronization

Owns: `Customer.java`, `CustomerMenu.java`, `Facility.java`, `FacilityMenu.java`, `Employee.java`, `TransitMonitorDaemon.java`

Topics to know: OOP fundamentals, Constructors, Encapsulation, `@FunctionalInterface`, input validation with `try-catch`, `HashMap` for O(1) lookups, `TreeMap` for sorted facilities, Synchronization in `DataStore`, Race Conditions, Intrinsic Locks, Daemon Threads.

---

### Member 2
**Role:** Logistics Operations, Fleet Management & Thread Lifecycle

Owns: `Vehicle.java`, `Trip.java`, `FleetMenu.java`, `TrackingEvent.java`, `TrackingMenu.java`, `TransitSimulationThread.java`, `ThreadLifecycleDemo.java`

Topics to know: `Comparable` and `compareTo()`, Wrapper classes, State lifecycle management, Thread subclassing, `start()` vs `run()`, `sleep()`, `join()`, `interrupt()`, `InterruptedException`, all 6 Thread states.

---

### Member 3
**Role:** Commercial Pricing Engine, Concurrency Benchmarking & Inter-Thread Communication

Owns: `WaybillGenerator.java`, `TablePrinter.java`, `ParcelMenu.java`, `Payment.java`, `PaymentMenu.java`, `ReportMenu.java`, `BatchProcessingTask.java`, `ParcelDispatchQueue.java`

Topics to know: `Comparator` with lambdas, `StringBuilder`, Luhn Mod-10 algorithm, `Runnable` interface, Sequential vs Concurrent execution benchmark, Concurrency vs Parallelism, `wait()`, `notifyAll()`, Guarded Blocks, Producer-Consumer pattern.

---

## Shared union files — all three members must know these

Every team member can be asked about these 6 files in the evaluation. They are the foundation of the whole project.

| File | Why everyone must know it |
|---|---|
| `Main.java` | How the app starts, how daemon is booted, how the main menu is displayed |
| `DataStore.java` | How data is stored in memory, why `synchronized` is on every write method, what collections are used and why |
| `Menu.java` | How `@FunctionalInterface` and lambdas make the menu work, how exceptions are handled centrally |
| `In.java` | How `try-catch` prevents crashes on bad input, what `NumberFormatException` is and when it's thrown |
| `Parcel.java` | The central business entity — fields, constructor, volumetric weight, status transitions |
| `ThreadingMenu.java` | The interactive concurrency lab — entry point for all multithreading demos |
