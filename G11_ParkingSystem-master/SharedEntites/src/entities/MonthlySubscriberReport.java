package entities;

import java.io.Serializable;
import java.util.List;

public class MonthlySubscriberReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String month;
    private List<Integer> dailySubscriberCounts;

    public MonthlySubscriberReport(String month, List<Integer> dailySubscriberCounts) {
        this.month = month;
        this.dailySubscriberCounts = dailySubscriberCounts;
    }

    public String getMonth() { return month; }
    public List<Integer> getDailySubscriberCounts() { return dailySubscriberCounts; }
}
