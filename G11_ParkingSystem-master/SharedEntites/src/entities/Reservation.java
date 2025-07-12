package entities;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a parking reservation made by a subscriber.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Unique reservation ID */
    private int reservationId;

    /** Subscriber's code associated with this reservation */
    private String subscriberCode;

    /** ID of the reserved parking space */
    private int parkingSpaceId;

    /** Date and time of the reservation */
    private LocalDateTime reservationDate;

    /** Optional confirmation code */
    private Integer confirmationCode;

    /** Reservation status: active, cancelled, expired */
    private String status;

    /**
     * Constructs a new Reservation object with all fields specified.
     *
     * @param reservationId      The unique reservation ID (set to 0 if not yet assigned).
     * @param subscriberCode     The subscriber's code making the reservation.
     * @param parkingSpaceId     The parking spot ID being reserved.
     * @param reservationDate    The date and time of the reservation.
     * @param confirmationCode   The optional confirmation code (may be null).
     * @param status             The reservation status ("active", "cancelled", "expired").
     */
    public Reservation(int reservationId, String subscriberCode, int parkingSpaceId,
                       LocalDateTime reservationDate, Integer confirmationCode, String status) {
        this.reservationId = reservationId;
        this.subscriberCode = subscriberCode;
        this.parkingSpaceId = parkingSpaceId;
        this.reservationDate = reservationDate;
        this.confirmationCode = confirmationCode;
        this.status = status;
    }


	public int getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public String getSubscriberCode() {
		return subscriberCode;
	}

	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}

	public int getParkingSpaceId() {
		return parkingSpaceId;
	}

	public void setParkingSpaceId(int parkingSpaceId) {
		this.parkingSpaceId = parkingSpaceId;
	}

	public LocalDateTime getReservationDate() {
		return reservationDate;
	}
	
	public LocalDateTime getEndTime() {
	    return reservationDate.plusHours(4);
	}

	public void setReservationDate(LocalDateTime reservationDate) {
		this.reservationDate = reservationDate;
	}

	public Integer getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(Integer confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    // Getters and setters can be added here
}
