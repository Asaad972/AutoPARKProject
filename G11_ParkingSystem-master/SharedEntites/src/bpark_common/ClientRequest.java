package bpark_common;

import java.io.Serializable;

/**
 * Represents a request sent from a client to the server in the BPARK system.
 * This object is used to encapsulate a command string and any associated parameters
 * needed to execute that command on the server side.
 * <p>
 * It implements {@link Serializable} to allow transmission over a network.
 */
public class ClientRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The command or action name that the server should process (e.g., "login", "get_data"). */
    private String command;

    /** The parameters associated with the command, if any. */
    private Object[] params;

    /**
     * Constructs a new ClientRequest with the given command and parameters.
     *
     * @param command the name of the command to execute
     * @param params  an array of parameters needed for the command (may be null or empty)
     */
    public ClientRequest(String command, Object[] params) {
        this.command = command;
        this.params = params;
    }

    /**
     * Returns the command string associated with this request.
     *
     * @return the command string
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the parameters passed with this request.
     *
     * @return an array of parameters (may be null or empty)
     */
    public Object[] getParams() {
        return params;
    }
}
