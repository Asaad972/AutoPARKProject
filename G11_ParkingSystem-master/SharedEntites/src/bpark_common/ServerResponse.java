package bpark_common;

import java.io.Serializable;

/**
 * Represents a generic response sent from the server to the client in the BPARK system.
 * This entity can carry commands, status, messages, and optional data objects.
 *
 * Usage examples:
 * - Indicate operation success/failure.
 * - Send data (such as lists or entities) to the client.
 * - Transmit messages or errors in a consistent format.
 *
 * @author BPARK
 */
public class ServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The command or type of this response (e.g., "LOGIN", "ERROR", "HISTORY_LIST").
     */
    private String command;

    /**
     * Indicates whether the server operation was successful.
     */
    private boolean success;

    /**
     * A human-readable message about the response (e.g., "Login successful", "No spots available").
     */
    private String message;

    /**
     * An optional data object (can be a List, entity, or null) included in this response.
     */
    private Object data;

    /**
     * Constructs a ServerResponse with all properties.
     *
     * @param command  The type/command of the response.
     * @param success  Whether the action was successful.
     * @param message  Additional message for the client.
     * @param data     Optional data payload.
     */
    public ServerResponse(String command, boolean success, String message, Object data) {
        this.command = command;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * @return The response command/type.
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return true if the operation succeeded, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return The message attached to this response.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return The data object attached to this response, or null if none.
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the response command/type.
     * @param command The command/type.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Sets the success flag.
     * @param success true if successful, false otherwise.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Sets the message text.
     * @param message The message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the data payload.
     * @param data The data object.
     */
    public void setData(Object data) {
        this.data = data;
    }
}
