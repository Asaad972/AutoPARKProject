package entities;

import java.io.Serializable;

/**
 * Represents a system user.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Unique user ID */
    private int id;

    /** Username used for login */
    private String username;

    /** Encrypted or plain text password */
    private String password;

    /** First name of the user */
    private String firstName;

    /** Last name of the user */
    private String lastName;

    /** Role of the user: admin, supervisor, subscriber */
    private String role;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    // Getters and setters can be added here
}
