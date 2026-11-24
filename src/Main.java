/**
 * Courier & Parcel Logistics Management System
 * Terminal application – Pure Java (no database required)
 *
 * Roll No: 2510992871 | Vishavjeet Singh Chauhan | Section G8
 * Course: 25CSE0202 – Programming in Java
 *
 * Java Concepts Used:
 *   OOP              – Classes, Objects, Inheritance, Polymorphism, Interfaces
 *   Collections      – ArrayList, HashMap, LinkedHashMap, TreeMap
 *   Strings          – String manipulation, StringBuilder, formatting
 *   Exceptions       – try-catch, custom exceptions, throw/throws
 *   Wrappers         – Integer, Double
 *   Comparable       – TrackingEvent sorting
 *   Multithreading   – Thread & Runnable, start()/run(), sleep(), join(), interrupt()
 *                      Thread states (NEW, RUNNABLE, TIMED_WAITING, WAITING, BLOCKED, TERMINATED)
 *                      Daemon threads, priorities, execution tracing
 *                      Shared data & synchronization, race conditions, intrinsic locks
 *                      wait(), notifyAll(), guarded blocks, Producer-Consumer pattern
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("""

            ########################################################
            #     COURIER & PARCEL LOGISTICS MANAGEMENT SYSTEM     #
            #       Pure Java  (Collections & Multithreading)      #
            ########################################################
            """);

        // [SYLLABUS TOPIC 11-12]: Start background Daemon thread on system boot
        TransitMonitorDaemon.startDaemon();

        System.out.println("System ready.  Sample data loaded.");
        System.out.println("Background fleet monitor daemon initialized.\n");

        new Menu("MAIN MENU", "Exit")
            .sub("Customers",                       CustomerMenu::show)
            .sub("Parcels (Booking)",               ParcelMenu::show)
            .sub("Facilities & Employees",          FacilityMenu::show)
            .sub("Vehicles & Trips",                FleetMenu::show)
            .sub("Tracking & Delivery",             TrackingMenu::show)
            .sub("Payments",                        PaymentMenu::show)
            .sub("Reports & Queries",               ReportMenu::show)
            .sub("Multithreading & Concurrency Lab", ThreadingMenu::show)
            .show();

        System.out.println("Goodbye!");
    }
}
