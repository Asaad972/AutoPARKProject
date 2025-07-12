package entities;

import java.io.Serializable;

/**
 * Represents a subscriber user in the system.
 * Stores identification and contact details.
 */
public class Subscriber implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Subscriber's unique ID */
    private int id;

    /** Full name of the subscriber */
    private String fullName;

    /** Username used for login */
    private String username; 

    /** Email address of the subscriber */
    private String email;

    /** Phone number of the subscriber */
    private String phone;

    /** Unique subscriber code */
    private String subscriberCode;

    /**
     * Constructs a Subscriber object with all details.
     *
     * @param id              Subscriber ID
     * @param fullName        Full name of the subscriber
     * @param username        Username used to login
     * @param email           Current email address
     * @param phone           Current phone number
     * @param subscriberCode  Unique subscriber code
     */
    public Subscriber(int id, String fullName, String username, String email, String phone, String subscriberCode) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone; 
        this.subscriberCode = subscriberCode;
    }

	/**
     * Constructs a Subscriber object.
     *
     * @param subscriberCode unique subscriber code (primary key)
     * @param subscriberId   ID referencing the users table
     * @param email          email address
     * @param phoneNumber    phone number
     */
    /** @return the subscriber's ID */
    public int getId() {
        return id;
    }

    /** @return the full name of the subscriber */
    public String getFullName() {
        return fullName;
    }

    /** @return the username of the subscriber */
    public String getUsername() {
        return username;
    }

    /** @return the email address of the subscriber */
    public String getEmail() {
        return email;
    }

    /** @return the phone number of the subscriber */
    public String getPhone() {
        return phone;
    }

    /** @return the unique subscriber code */
    public String getSubscriberCode() {
        return subscriberCode;
    }
}
