import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;

/**
 * Menu and file-management code are provided. Students implement methods
 * in AppointmentScheduler.java only.
 */
public class SchedulerMain {
    private static final Path SEED_PATH = Paths.get("data", "seed_schedule.txt");
    private static final Path SAVE_PATH = Paths.get("data", "saved_schedule.txt");

    public static void main(String[] args) {
        AppointmentScheduler scheduler = new AppointmentScheduler();
        loadAtStartup(scheduler);
        Scanner console = new Scanner(System.in);

        while (true) {
            System.out.println("\nSMART APPOINTMENT SCHEDULER (" + scheduler.size() + " appointments)");
            System.out.println("1 - Display today's schedule");
            System.out.println("2 - Book an appointment");
            System.out.println("3 - Cancel an appointment by start time");
            System.out.println("4 - Find next appointment at/after a time");
            System.out.println("5 - Save schedule");
            System.out.println("6 - Save and quit");
            System.out.print("Choice: ");
            String choice = console.nextLine().trim();

            try {
                switch (choice) {
                    case "1": scheduler.printSchedule(); break;
                    case "2": book(console, scheduler); break;
                    case "3": cancel(console, scheduler); break;
                    case "4": next(console, scheduler); break;
                    case "5": save(scheduler); break;
                    case "6":
                        if (save(scheduler)) System.out.println("Goodbye!");
                        else System.out.println("Goodbye! Save did not complete.");
                        return;
                    default: System.out.println("Choose a number from 1 to 6.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid input: " + ex.getMessage());
            }
        }
    }

    private static void book(Scanner console, AppointmentScheduler scheduler) {
        System.out.print("Title: ");
        String title = console.nextLine();
        int start = askTime(console, "Start time (HH:MM, 24-hour): ");
        int duration = askPositiveInt(console, "Duration (minutes): ");
        Appointment appointment = new Appointment(start, duration, title);
        if (scheduler.book(appointment)) {
            System.out.println("Booked: " + appointment);
        } else {
            System.out.println("Booking rejected (conflict or outside 08:00-17:00).");
        }
    }

    private static void cancel(Scanner console, AppointmentScheduler scheduler) {
        int start = askTime(console, "Start time to cancel (HH:MM): ");
        System.out.println(scheduler.cancel(start) ? "Canceled." : "No appointment starts then.");
    }

    private static void next(Scanner console, AppointmentScheduler scheduler) {
        int time = askTime(console, "Find first appointment starting at/after (HH:MM): ");
        Appointment result = scheduler.nextAtOrAfter(time);
        System.out.println(result == null ? "No later appointment." : "Next: " + result);
    }

    private static int askTime(Scanner console, String prompt) {
        System.out.print(prompt);
        return Appointment.parseTime(console.nextLine());
    }

    private static int askPositiveInt(Scanner console, String prompt) {
        System.out.print(prompt);
        try {
            int value = Integer.parseInt(console.nextLine().trim());
            if (value <= 0) throw new IllegalArgumentException("Duration must be positive.");
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Duration must be a whole number.");
        }
    }

    /** A saved file takes priority. Missing, blank, or corrupt saves fall back to the seed. */
    private static void loadAtStartup(AppointmentScheduler scheduler) {
        if (Files.isRegularFile(SAVE_PATH)) {
            try (Scanner in = new Scanner(SAVE_PATH, StandardCharsets.UTF_8)) {
                scheduler.load(in);
                System.out.println("Loaded " + scheduler.size() + " appointments from " + SAVE_PATH);
                return;
            } catch (IOException | IllegalArgumentException ex) {
                System.out.println("Saved schedule unavailable/invalid; trying starter schedule.");
            }
        }
        if (Files.isRegularFile(SEED_PATH)) {
            try (Scanner in = new Scanner(SEED_PATH, StandardCharsets.UTF_8)) {
                scheduler.load(in);
                System.out.println("Loaded " + scheduler.size() + " appointments from " + SEED_PATH);
                return;
            } catch (IOException | IllegalArgumentException ex) {
                System.out.println("Could not read starter schedule: " + ex.getMessage());
            }
        }
        System.out.println("No schedule loaded. Working directory: " +
                Paths.get("").toAbsolutePath().normalize());
        System.out.println("Open the folder containing the Java files AND data folder directly in VS Code.");
    }

    /** Buffer the file BEFORE writing to disk: unfinished student code cannot erase a saved file. */
    private static boolean save(AppointmentScheduler scheduler) {
        try {
            StringWriter buffer = new StringWriter();
            try (PrintWriter writer = new PrintWriter(buffer)) {
                scheduler.write(writer);
            }
            Files.createDirectories(SAVE_PATH.getParent());
            Files.writeString(SAVE_PATH, buffer.toString(), StandardCharsets.UTF_8);
            System.out.println("Saved " + scheduler.size() + " appointments to " + SAVE_PATH);
            return true;
        } catch (UnsupportedOperationException ex) {
            System.out.println("Not saved: finish the write() method first.");
        } catch (IOException ex) {
            System.out.println("Could not save: " + ex.getMessage());
        }
        return false;
    }
}
