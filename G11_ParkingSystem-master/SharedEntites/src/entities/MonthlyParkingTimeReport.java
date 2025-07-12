package entities;

import java.io.Serializable;

public class MonthlyParkingTimeReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String month;
    private int normalHours;
    private int extendedHours;
    private int delayedHours;

    public MonthlyParkingTimeReport(String month, int normalHours, int extendedHours, int delayedHours) {
        this.month = month;
        this.normalHours = normalHours;
        this.extendedHours = extendedHours;
        this.delayedHours = delayedHours;
    }

    public String getMonth() { return month; }
    public int getNormalHours() { return normalHours; }
    public int getExtendedHours() { return extendedHours; }
    public int getDelayedHours() { return delayedHours; }
}
