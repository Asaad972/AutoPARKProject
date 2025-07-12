package serverGui;

import javafx.application.Platform;
import server.DBController;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles scheduling of monthly report generation tasks in the background.
 * 
 * Automatically calculates the last day of the current month and schedules
 * the report generation for that time. Once run, it reschedules itself again.
 */
public class MonthlyReportScheduler {

    private Timer timer;
    private Runnable onReportGenerated; // Optional GUI callback to update GUI status

    /**
     * Constructs a new scheduler.
     *
     * @param onReportGenerated a callback function to run on the JavaFX thread after report is generated
     */
    public MonthlyReportScheduler(Runnable onReportGenerated) {
        this.onReportGenerated = onReportGenerated;
    }

    /**
     * Starts or restarts the background timer to generate monthly reports.
     * 
     * Cancels any previous timer and reschedules the next run.
     * The timer will continue rescheduling itself after every execution.
     */
    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true); // daemon thread

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;

                System.out.printf("Generating monthly reports for %d-%02d...", year, month);
                DBController.generateMonthlyReports(year, month);

                if (onReportGenerated != null) {
                    Platform.runLater(onReportGenerated);
                }

                start(); // reschedule
            }
        };

        timer.schedule(task, getNextRunDate());
        System.out.println("Monthly report generation scheduled for: " + getNextRunDate());
    }

    /**
     * Stops the currently running timer, if any.
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Calculates the next scheduled time for report generation.
     * 
     * Returns a Date set to the last day of the current month at 23:59.
     *
     * @return the next run Date
     */
    private Date getNextRunDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
