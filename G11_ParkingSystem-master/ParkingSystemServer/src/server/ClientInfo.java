package server;

/**
 * Represents metadata about a connected client, including IP address,
 * hostname, status, and a unique identifier.
 */
public class ClientInfo {
    private String ip;
    private String host;
    private String status;
    private int id;

    /**
     * Constructs a new ClientInfo object.
     *
     * @param ip     the IP address of the client
     * @param host   the host name of the client
     * @param status the current status of the client (e.g., "connected")
     * @param id     the unique ID assigned to the client
     */
    public ClientInfo(String ip, String host, String status, int id) {
        this.ip = ip;
        this.host = host;
        this.status = status;
        this.id = id;
    }

    /**
     * @return the client's IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return the client's host name
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the client's current status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the client's unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the client's current status.
     *
     * @param status the new status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
