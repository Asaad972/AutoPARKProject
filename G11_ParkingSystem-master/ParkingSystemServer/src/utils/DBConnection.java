package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that manages a simple database connection pool.
 * This avoids creating a new connection every time and improves performance.
 * Connections should be released back to the pool after use.
 */
public class DBConnection {

    private static DBConnection instance;

    private static final String URL = "jdbc:mysql://localhost:3306/bpark?serverTimezone=Asia/Jerusalem&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Aa123456";

    private static final int INITIAL_POOL_SIZE = 5;

    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections;

    /**
     * Private constructor to initialize the connection pool.
     *
     * @throws SQLException if a connection fails to initialize
     */
    private DBConnection() throws SQLException {
        this.connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);
        this.usedConnections = new ArrayList<>();
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            connectionPool.add(createConnection());
        }
    }

    /**
     * Returns the Singleton instance of DBConnection.
     *
     * @return singleton DBConnection instance
     * @throws SQLException if connection pool fails to initialize
     */
    public static synchronized DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Retrieves a connection from the pool.
     *
     * @return available database connection
     * @throws SQLException if no connection is available
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            // Optional: grow pool if needed (or throw exception)
            connectionPool.add(createConnection());
        }
        Connection conn = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(conn);
        return conn;
    }

    /**
     * Returns a connection back to the pool.
     *
     * @param conn the connection to release
     * @return true if successfully returned; false otherwise
     */
    public synchronized boolean releaseConnection(Connection conn) {
        connectionPool.add(conn);
        return usedConnections.remove(conn);
    }

    /**
     * Closes all connections in the pool and used list.
     *
     * @throws SQLException if a connection cannot be closed
     */
    public synchronized void shutdown() throws SQLException {
        for (Connection conn : connectionPool) conn.close();
        for (Connection conn : usedConnections) conn.close();
        connectionPool.clear();
        usedConnections.clear();
    }

    /**
     * Creates a new database connection.
     *
     * @return new database connection
     * @throws SQLException if connection fails
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
