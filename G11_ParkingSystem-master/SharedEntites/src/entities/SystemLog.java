package entities;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an action log entry in the system.
 */
public class SystemLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Unique ID for the log entry */
    private int logId;

    /** Action performed */
    private String action;

    /** Target entity the action was performed on */
    private String target;

    /** ID of the user who performed the action */
    private Integer byUser;

    /** Time the action was logged */
    private LocalDateTime logTime;

    /** Additional notes */
    private String note;

    // Getters and setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getByUser() {
        return byUser;
    }

    public void setByUser(Integer byUser) {
        this.byUser = byUser;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
