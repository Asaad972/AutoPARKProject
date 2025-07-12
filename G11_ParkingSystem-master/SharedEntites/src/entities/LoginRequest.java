package entities;


import java.io.Serializable;

/**
 * Represents a login request sent from the client to the server.
 */
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Username or subscriber ID */
    private String username;

    /** Password for the account */
    private String password;

    /** Access mode (home/shop) */
    private String accessMode;

    /**
     * Constructs a new LoginRequest with the given credentials and access mode.
     *
     * @param username   the username or subscriber ID
     * @param password   the password for the account
     * @param accessMode the access mode (home/shop)
     */
    public LoginRequest(String username, String password, String accessMode) {
        this.username = username;
        this.password = password;
        this.accessMode = accessMode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccessMode() {
        return accessMode;
    }
}
