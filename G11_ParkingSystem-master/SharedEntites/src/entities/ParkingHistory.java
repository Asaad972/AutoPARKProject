package entities;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a record in a subscriber's parking history.
 */
public class ParkingHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int historyId;
	private String subscriberCode;
	private int parkingSpaceId;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	private boolean extended;
	private boolean wasLate;
	private boolean pickedUp;
	private int extended_hours;

	public ParkingHistory(int historyId, String subscriberCode, int parkingSpaceId, LocalDateTime entryTime,
			LocalDateTime exitTime, boolean extended, int extended_hours, boolean wasLate, boolean pickedUp) {
		this.historyId = historyId;
		this.subscriberCode = subscriberCode;
		this.parkingSpaceId = parkingSpaceId;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.extended_hours = extended_hours;

		this.extended = extended;
		this.wasLate = wasLate;
		this.pickedUp = pickedUp;

	}

	// Getters

	public int getHistoryId() {
		return historyId;
	}

	public int getExtendedHours() {
		return this.extended_hours;
	}
	public void setExtendedHours(int hours) {
		this.extended_hours = hours;
	}

	public String getSubscriberCode() {
		return subscriberCode;
	}

	public int getParkingSpaceId() {
		return parkingSpaceId;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public boolean isExtended() {
		return extended;
	}

	public boolean isWasLate() {
		return wasLate;
	}

	public boolean isPickedUp() {
		return pickedUp;
	}

	public void setPickedUp(boolean pickedUp) {
		this.pickedUp = pickedUp;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}
}
