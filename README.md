# Courier & Parcel Logistics Management System
**Course:** 25CSE0202 – Programming in Java  
**Student Name:** Vishavjeet Singh Chauhan  
**Roll No:** 2510992871 | **Section:** G8  
**Institution:** Chitkara University  
**Evaluation:** Continuous Evaluation (CE-1 & Multithreading Modules)

---

## 📌 Project Overview
A terminal-based **Courier & Parcel Logistics Management System** implemented in **Pure Core Java** (no database required, zero external dependencies). 

All records are managed using in-memory Java **Collections Framework** structures (`HashMap`, `TreeMap`, `LinkedHashMap`, `ArrayList`) with sample data loaded on startup.
The application includes a fully thread-safe core (`DataStore`) and a dedicated **Multithreading & Concurrency Lab** designed specifically to demonstrate every topic in the Java Multithreading syllabus.

---

## 🚀 How to Compile and Run

### Option 1: Using the provided scripts
- **To compile:**
  Double-click `scripts\compile.bat` or run in terminal:
  ```cmd
  scripts\compile.bat
  ```
- **To run:**
  Double-click `scripts\run.bat` or run in terminal:
  ```cmd
  scripts\run.bat
  ```
- **PowerShell:**
  ```powershell
  .\scripts\run.ps1
  ```

### Option 2: Using standard `javac` and `java` directly
From the project directory:
```cmd
javac -d out src\*.java
java -cp out Main
```

---

## 📂 Project Architecture & File Inventory (27 Files)

All source files reside cleanly in `src/` under the default package for seamless compilation.

### 1. Entity / Model Layer (8 Classes)
| Class | Purpose | Concepts Demonstrated |
|---|---|---|
| `Customer.java` | Sender/receiver with multi-valued phone numbers | Encapsulation, constructors, `this`, `ArrayList<String>`, `toString()` |
| `Facility.java` | Hub or Branch facility with regional categorization | OOP, encapsulation, String methods |
| `Employee.java` | Staff member (Manager, Branch Staff, Delivery Agent, Driver) | OOP, wrapper class usage (`int` / `Integer`) |
| `Vehicle.java` | Fleet vehicle with registration and payload capacity | Encapsulation, getters/setters, state tracking |
| `Parcel.java` | Central parcel entity (dimensions, weight, status, promised date) | Complex OOP entity, volumetric weight derivation, `java.time` |
| `Trip.java` | Inter-facility transit trips carrying multiple waybills | OOP, `ArrayList<String>` for loaded waybills, state lifecycle |
| `Payment.java` | 1-to-1 payment tracking for parcels | String formatting, timestamps, status management |
| `TrackingEvent.java` | Immutable audit log of parcel movement | **`Comparable<TrackingEvent>`** interface for timeline sorting |

### 2. Core & Utility Layer (6 Classes)
| Class | Purpose | Concepts Demonstrated |
|---|---|---|
| `Main.java` | Application entry point with banner, daemon startup & navigation | `static main`, String blocks, daemon thread initialization |
| `DataStore.java` | Central in-memory data store with thread synchronization | **`HashMap`**, **`TreeMap`**, **`LinkedHashMap`**, **`ArrayList`**, **`synchronized` methods/blocks**, intrinsic locks, race condition demo |
| `Menu.java` | Generic console menu renderer | `@FunctionalInterface Action`, lambda expressions, ArrayList |
| `In.java` | Safe keyboard input helpers with validation | Robust `try-catch` exception handling, `Scanner`, `NumberFormatException` |
| `TablePrinter.java` | Renders clean ASCII tables with column auto-sizing | `StringBuilder`, 2D string formatting, arrays |
| `WaybillGenerator.java`| Generates verified waybill tracking numbers | Luhn mod-10 check digit algorithm, `String.format` |

### 3. Menu & Interaction Layer (7 Classes)
| Class | Purpose | Concepts Demonstrated |
|---|---|---|
| `CustomerMenu.java` | Customer onboarding, multi-phone management, history | HashMap lookup, search string filters |
| `ParcelMenu.java` | Parcel booking with dynamic slab & volumetric pricing | Volumetric weight computation, validation |
| `FacilityMenu.java` | Facility listing and employee staff assignments | TreeMap iteration, filtered sub-lists |
| `FleetMenu.java` | Vehicle management, trip scheduling, parcel loading | Payload capacity checks, trip status transitions |
| `TrackingMenu.java` | Real-time status lookup, check-ins, delivery updates | `Collections.sort()` on `Comparable` events |
| `PaymentMenu.java` | Payment processing, receipt viewing, pending clearances | Iteration, status updates, invoice generation |
| `ReportMenu.java` | Statistical summaries and business analytics | **`Comparator`** sorting, `TreeMap` revenue aggregation |

### 4. Multithreading & Concurrency Layer (6 Classes)
| Class | Purpose | Multithreading Syllabus Topics Demonstrated |
|---|---|---|
| `ThreadingMenu.java` | Interactive Concurrency Lab for teacher evaluation | Interactive menu hosting live simulations for all syllabus points |
| `TransitSimulationThread.java` | Vehicle movement simulator extending `Thread` | **`Thread` subclass**, **`start()` vs `run()`**, **`sleep()`**, **`join()`**, **`interrupt()`**, execution tracing |
| `BatchProcessingTask.java` | Bulk facility parcel auditor implementing `Runnable` | **`Runnable` interface**, **Sequential vs Concurrent benchmark**, Concurrency vs Parallelism |
| `ThreadLifecycleDemo.java` | Live state inspector walking through all 6 states | **`NEW`**, **`RUNNABLE`**, **`TIMED_WAITING`**, **`WAITING`**, **`BLOCKED`**, **`TERMINATED`** (`Thread.State`) |
| `ParcelDispatchQueue.java` | Bounded conveyor buffer for Hub dispatch | **`wait()`**, **`notifyAll()`**, **Guarded blocks** (`while` loops), Producer-Consumer coordination |
| `TransitMonitorDaemon.java` | Background fleet health & heartbeat auditor | **`setDaemon(true)`**, **`Thread.MIN_PRIORITY`**, background execution tracing |

---

## 🎯 Direct Mapping to Full Syllabus Topics

### A. Core Java & Collections Topics
| Syllabus Topic | How & Where It Is Implemented |
|---|---|
| **Classes & Objects, Constructors, `this`, `static`** | All model classes (`Customer`, `Parcel`, `Facility`, `Vehicle`, etc.) use parameterized constructors, encapsulation, and `this`. `DataStore` uses `static` storage and methods. |
| **Interfaces & Polymorphism** | `TrackingEvent` implements `Comparable<TrackingEvent>`. `BatchProcessingTask` implements `Runnable`. `Menu` defines custom `@FunctionalInterface Action`. |
| **Strings & Formatting** | `TablePrinter` dynamically constructs borders with `StringBuilder`. `WaybillGenerator` and `In` perform string transformations, slicing, and regex/character parsing. |
| **Wrapper Classes** | `Integer` and `Double` used for parsing strings (`Integer.parseInt`, `Double.parseDouble`) and handling nullability. |
| **Exception Handling (`try-catch`, `throw`, `throws`)** | `In.java` catches `NumberFormatException` and `DateTimeParseException`. All menu actions declare `throws Exception`, caught centrally in `Menu.java`. Interruption catches `InterruptedException`. |
| **Collections Framework** | `ArrayList` (ordered lists), `HashMap` (O(1) lookups), `LinkedHashMap` (insertion order), `TreeMap` (natural sorting). |
| **Sorting (`Comparable` & `Comparator`)** | `TrackingEvent` implements `Comparable<TrackingEvent>` (by timestamp). `ReportMenu` uses `Comparator` with lambdas. |

### B. Multithreading Syllabus Topics (Weeks 9 - 16)
| Syllabus Topic | Implementation in Project | Key Code References |
|---|---|---|
| **Multiprogramming vs Multithreading & Concurrency** | Documented & demonstrated in `ThreadingMenu.java` (Option 8 Viva Guide). Shows that threads share JVM heap/DataStore memory. | `ThreadingMenu::showTheoryGuide`, `DataStore.java` |
| **Program, Process, and Thread** | Passive program (`.class`) vs active process (JVM) vs lightweight threads of execution. | `ThreadingMenu.java` |
| **Sequential vs Concurrent Execution** | Live benchmark comparing sequential execution on main thread vs concurrent execution on 3 worker threads with speedup calculation. | `BatchProcessingTask.java`, `ThreadingMenu::demoBenchmark` |
| **Concurrency vs Parallelism** | Interleaving on single core vs simultaneous execution across multi-core CPUs. | `BatchProcessingTask.java` |
| **Thread Class & Runnable Interface** | `TransitSimulationThread` extends `Thread`. `BatchProcessingTask` implements `Runnable`. | `TransitSimulationThread.java`, `BatchProcessingTask.java` |
| **`start()` vs `run()`** | Demonstrates that `run()` executes synchronously on caller thread, while `start()` spawns a new OS/JVM thread stack. | `TransitSimulationThread.java`, `ThreadingMenu::demoTransit` |
| **`sleep()`, `join()`, `interrupt()`** | `sleep()` for travel milestones, `join()` for caller coordination, `interrupt()` with `InterruptedException` handling for emergency trip halts. | `TransitSimulationThread.java`, `ThreadingMenu::demoInterrupt` |
| **Thread Lifecycle (6 States)** | Live trace capturing and printing `Thread.State` at every state: `NEW`, `RUNNABLE`, `TIMED_WAITING`, `WAITING`, `BLOCKED`, `TERMINATED`. | `ThreadLifecycleDemo.java`, `ThreadingMenu::demoLifecycle` |
| **Daemon Threads & Priorities** | Background `TransitMonitorDaemon` initialized with `setDaemon(true)` and `setPriority(Thread.MIN_PRIORITY)`. JVM exits without blocking. | `TransitMonitorDaemon.java`, `Main.java` |
| **Execution Tracing** | Formatted execution logs showing `[Thread: <name> \| ID: <id> \| Priority: <pri> \| State: <state>]`. | `TransitSimulationThread.java`, `ThreadingMenu.java` |
| **Shared Data, Race Conditions & Critical Sections** | Side-by-side test comparing 2 concurrent threads incrementing an unsynchronized counter (lost updates, race condition) vs synchronized critical sections. | `DataStore.java`, `ThreadingMenu::demoRaceCondition` |
| **Synchronized Methods & Blocks, Intrinsic Locks** | All mutation methods in `DataStore` use `synchronized` to acquire the intrinsic lock of `DataStore.class`, guaranteeing Atomicity and Visibility. | `DataStore.java` (`addParcel`, `addTrackingEvent`, etc.) |
| **Inter-thread Communication: `wait()`, `notifyAll()`** | Bounded buffer conveyor (`ParcelDispatchQueue`). Producers wait when full; consumers wait when empty; notifyAll on state change. | `ParcelDispatchQueue.java`, `ThreadingMenu::demoProducerConsumer` |
| **Guarded Blocks & Correct Monitor Usage** | Guard conditions wrapped inside `while (buffer.isFull()) wait()` to prevent spurious wakeups, inside synchronized monitor methods. | `ParcelDispatchQueue.java` |

---

## 🧪 Suggested Demo Flow for Teacher Evaluation

### Phase 1: Core Logistics Operations (Unchanged & Rock Solid)
1. **Start System:** Run `scripts\run.bat` or `java -cp out Main`.
2. **View Initial Data:** Go to `7. Reports & Queries` ➔ `6. Data summary (record counts)`.
3. **Book a Parcel:** Go to `2. Parcels (Booking)` ➔ `1. Book a new parcel` (note volumetric calculation and generated waybill).
4. **Track Movement:** Go to `5. Tracking & Delivery` ➔ `1. Track a parcel (full history)`.
5. **Vehicle Dispatch:** Go to `4. Vehicles & Trips` ➔ `4. Load parcel onto trip`, then `5. Dispatch trip` and `6. Complete trip`.

### Phase 2: Multithreading & Concurrency Evaluation (Menu Option 8)
Go to `8. Multithreading & Concurrency Lab` in Main Menu:

1. **Thread, `start()`, `sleep()`, `join()` (Option 1):**
   - Shows `TransitSimulationThread` subclassing `Thread`.
   - Watch the live progress updates as the thread sleeps (`TIMED_WAITING`) between milestones and the main thread waits using `join()`.
2. **Thread Interruption (Option 2):**
   - Shows emergency halt signaling via `thread.interrupt()`.
   - Explains how `InterruptedException` is cleanly caught and handled.
3. **Thread Lifecycle (All 6 States) (Option 3):**
   - Live real-time demonstration walking a thread through **`NEW`**, **`RUNNABLE`**, **`TIMED_WAITING`**, **`WAITING`**, **`BLOCKED`**, and **`TERMINATED`**.
4. **Sequential vs Concurrent Benchmark (Option 4):**
   - Runs 3 batch tasks sequentially, then concurrently on 3 worker threads.
   - Shows execution time dropping by ~3x and explains Concurrency vs Parallelism.
5. **Race Condition vs Synchronization (Option 5):**
   - Shows two threads updating an unsynchronized counter -> lost updates occur!
   - Shows the same operation with `synchronized` methods/blocks -> 100% exact result (20,000 / 20,000).
6. **Producer-Consumer Coordination (Option 6):**
   - Shows 2 Sorter threads (producers) and 1 Delivery Van thread (consumer) coordinating over a bounded conveyor buffer of capacity 3.
   - Highlights `wait()`, `notifyAll()`, and guarded `while` loops.
7. **Background Daemon Monitor (Option 7):**
   - Displays live status of `TransitMonitorDaemon` running quietly in the background with `setDaemon(true)` and `MIN_PRIORITY`.
8. **Viva & Theory Guide (Option 8):**
   - Comprehensive quick reference with textbook answers to common viva questions.
