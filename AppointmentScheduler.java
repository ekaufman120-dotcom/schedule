import java.io.PrintWriter;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * STUDENT WORK: complete five marked methods below.
 * TreeSet is backed by a balanced BST. It orders appointments by start time.
 */
public class AppointmentScheduler {
    public static final int OPEN_MINUTES = 8 * 60;
    public static final int CLOSE_MINUTES = 17 * 60;
    public static final String FILE_HEADER = "BST-SCHEDULE-V1";
    private final TreeSet<Appointment> schedule = new TreeSet<>();

    public int size() { return schedule.size(); }

    /**
     * TODO 1: Book the appointment if it is within 08:00-17:00 AND does not
     * overlap any existing appointment. Back-to-back bookings are allowed.
     * Use TreeSet.floor(requested) and TreeSet.ceiling(requested) to inspect
     * just the adjacent appointments. Return true only if added.
     */
    public boolean book(Appointment requested) {
        if(requested.getStartMinutes()<480||requested.getStartMinutes()+requested.getDurationMinutes()>1020){
            System.out.println(requested.getStartMinutes()<480);
            System.out.println(requested.getStartMinutes()+requested.getDurationMinutes()>1020);
            return false;
        }
        Appointment before = schedule.floor(Appointment.keyAt(requested.getStartMinutes()));
        Appointment after = schedule.ceiling(Appointment.keyAt(requested.getStartMinutes()));
        if((before!=null&&before.getStartMinutes()+before.getDurationMinutes()>requested.getStartMinutes())||(after!=null&&requested.getStartMinutes()+requested.getDurationMinutes()>after.getStartMinutes()))
            return false;
        schedule.add(requested);
        return true;
    }

    /** 2: Print appointments chronologically, or an empty-schedule message. */
    public void printSchedule() {
        // Use the TreeSet's sorted iteration order.
        for(Appointment i: schedule)
        {
            System.out.println(i);
        }

    }

    /** 3: Cancel the appointment that STARTS at the given minute, if any. */
    public boolean cancel(int startMinutes) {
        for(Appointment i: schedule)
        {
            if(i.getStartMinutes()==startMinutes)
            {
                schedule.remove(i);
                return true;
            }
        }
        return false;
    }

    /** 4: Return the earliest appointment beginning at or after time. */
    public Appointment nextAtOrAfter(int minutes) {
        // TreeSet.ceiling(Appointment.keyAt(minutes)) is useful.
        //return Math.min(schedule.floor(Appointment.keyAt(minutes)),schedule.ceiling(Appointment.keyAt(minutes)));
        if(Math.abs(minutes-schedule.floor(Appointment.keyAt(minutes)).getStartMinutes())<Math.abs(minutes-schedule.ceiling(Appointment.keyAt(minutes)).getStartMinutes()))
            return schedule.floor(Appointment.keyAt(minutes));
        else
            return schedule.ceiling(Appointment.keyAt(minutes));
    }

    /**
     * 5: Save a header followed by each appointment in chronological order.
     * Begin with: out.println(FILE_HEADER);
     * Then print each appointment.toFileLine().
     */
    public void write(PrintWriter out) {
        // Implement writing. Keep this exception until your code is ready.
        out.println(FILE_HEADER);
        for(Appointment i: schedule)
        {
            out.println(i.toFileLine());
        }
        //throw new UnsupportedOperationException("write() not implemented");
    }

    /** PROVIDED: loading is deliberately independent of unfinished student methods. */
    public void load(Scanner input) {
        if (!input.hasNextLine() || !FILE_HEADER.equals(input.nextLine().trim())) {
            throw new IllegalArgumentException("Missing or incorrect schedule header.");
        }
        TreeSet<Appointment> incoming = new TreeSet<>();
        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (line.isEmpty()) continue;
            Appointment item = Appointment.fromFileLine(line);
            if (item.getStartMinutes() < OPEN_MINUTES || item.getEndMinutes() > CLOSE_MINUTES ||
                    !incoming.add(item)) {
                throw new IllegalArgumentException("Invalid saved appointment: " + line);
            }
        }
        Appointment previous = null;
        for (Appointment item : incoming) {
            if (previous != null && previous.getEndMinutes() > item.getStartMinutes()) {
                throw new IllegalArgumentException("Overlapping appointments in saved file.");
            }
            previous = item;
        }
        schedule.clear();
        schedule.addAll(incoming);
    }
}
