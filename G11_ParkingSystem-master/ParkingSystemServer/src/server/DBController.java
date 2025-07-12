package server;

import entities.MonthlyParkingTimeReport;
import entities.MonthlySubscriberReport;
import entities.ParkingHistory;
import entities.ParkingSpace;
import entities.Reservation;
import entities.Subscriber;
import entities.SystemLog;
import utils.EmailUtil;
import utils.DBConnection;

import java.util.Random;

import javax.mail.MessagingException;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DBController handles database connectivity and operations related to user authentication,
 * subscribers, reservations, and parking space management.
 * <p>
 * Provides convenient methods for performing database CRUD operations.
 * </p>
 * 
 * @author Ibrahem_Amar
 */
public class DBController {

	private static final int EXTEND_HOURS_PER_REQUEST = 4;
	
    /**
     * Retrieves a database connection from the singleton {@link DBConnection} utility.
     * <p>
     * The connection returned must be closed after use to prevent resource leaks.
     * </p>
     *
     * @return an active database connection
     * @throws SQLException if unable to establish a connection to the database
     */
    public static Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }
    
    /**
     * Releases a database connection back to the pool.
     *
     * @param conn the connection to release
     */
    public static void releaseConnection(Connection conn) {
        try {
            if (conn != null) {
                DBConnection.getInstance().releaseConnection(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



	// --------- Universal Mappers ---------

    /**
     * Maps the given ResultSet to a ParkingHistory object.
     *
     * @param rs the ResultSet containing parking history data
     * @return a ParkingHistory object mapped from the result set
     * @throws SQLException if a database access error occurs
     */
	public ParkingHistory mapParkingHistory(ResultSet rs) throws SQLException {
		return new ParkingHistory(rs.getInt("history_id"), rs.getString("subscriber_code"),
				rs.getInt("parking_space_id"), rs.getTimestamp("entry_time").toLocalDateTime(),
				rs.getTimestamp("exit_time").toLocalDateTime(), rs.getBoolean("extended"), rs.getInt("extended_hours"),
				rs.getBoolean("was_late"), rs.getBoolean("picked_up"));
	}

	/**
	 * Maps the given ResultSet to a ParkingSpace object.
	 *
	 * @param rs the ResultSet containing parking space data
	 * @return a ParkingSpace object mapped from the result set
	 * @throws SQLException if a database access error occurs
	 */
	public ParkingSpace mapParkingSpace(ResultSet rs) throws SQLException {
		return new ParkingSpace(rs.getInt("parking_space_id"), rs.getBoolean("is_available"));
	}

	/**
	 * Maps the given ResultSet to a Subscriber object.
	 *
	 * @param rs the ResultSet containing subscriber data
	 * @return a Subscriber object mapped from the result set
	 * @throws SQLException if a database access error occurs
	 */
	public Subscriber mapSubscriber(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
		String username = rs.getString("username");
		String email = rs.getString("email");
		String phone = rs.getString("phone_number");
		String subscriberCode = rs.getString("subscriber_code");
		return new Subscriber(id, fullName, username, email, phone, subscriberCode);
	}

	// -------------------------------------

	/**
	 * Validates a user's credentials against the database.
	 *
	 * @param username the username to validate
	 * @param password the password to validate
	 * @return true if the credentials are valid, false otherwise
	 */
	public boolean validateUser(String username, String password) {
	    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setString(1, username);
	            stmt.setString(2, password);
	            try (ResultSet rs = stmt.executeQuery()) {
	                return rs.next();
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Retrieves the role of a user based on their credentials.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the user's role if found, otherwise null
	 */
	public String checkUserCredentials(String username, String password) {
		String sql = "SELECT role FROM users WHERE BINARY username = ? AND BINARY password = ?";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, username);
	            stmt.setString(2, password);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getString("role");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return null;
	}

	
	/**
	 * Retrieves a list of all available parking spaces.
	 *
	 * @return list of available ParkingSpace objects
	 */
	public List<ParkingSpace> getAvailableParkingSpaces() {
	    List<ParkingSpace> spots = new ArrayList<>();
	    String sql = "SELECT * FROM parking_space WHERE is_available = TRUE";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql);
	             ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                spots.add(mapParkingSpace(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return spots;
	}

	

	/**
	 * Retrieves a Subscriber object from the database using their username.
	 *
	 * @param username the subscriber's username
	 * @return Subscriber object if found, null otherwise
	 */
	public Subscriber getSubscriberByUsername(String username) {
	    String sql = """
	            SELECT u.id, u.first_name, u.last_name, u.username, s.email, s.phone_number, s.subscriber_code
	            FROM users u
	            JOIN subscriber s ON s.subscriber_id = u.id
	            WHERE u.username = ?
	            """;
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, username);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return mapSubscriber(rs);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return null;
	}


	/**
	 * Updates a subscriber's email and phone number in the database.
	 *
	 * @param subscriber the subscriber to update
	 * @return true if the update was successful, false otherwise
	 */
	public boolean updateSubscriberInfo(Subscriber subscriber) {
	    String sql = "UPDATE subscriber SET email = ?, phone_number = ? WHERE subscriber_code = ?";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, subscriber.getEmail());
	            stmt.setString(2, subscriber.getPhone());
	            stmt.setString(3, subscriber.getSubscriberCode());
	            int rowsUpdated = stmt.executeUpdate();
	            return rowsUpdated > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Retrieves the parking history for a given subscriber.
	 *
	 * @param subscriberCode the code of the subscriber
	 * @return list of ParkingHistory records
	 */
	public List<ParkingHistory> getParkingHistoryForSubscriber(String subscriberCode) {
	    List<ParkingHistory> history = new ArrayList<>();
	    String sql = """
	            SELECT history_id, subscriber_code, parking_space_id, entry_time, exit_time, extended, extended_hours, was_late, picked_up
	            FROM parking_history
	            WHERE subscriber_code = ?
	            ORDER BY entry_time DESC
	            """;
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, subscriberCode);
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    history.add(mapParkingHistory(rs));
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return history;
	}


	/**
	 * Retrieves a random available parking space ID that does not have an active session
	 * overlapping the next 4 hours.
	 *
	 * @return the parking space ID, or -1 if none available
	 */
	public int getRandomAvailableSpotWithoutA() {
	    String sql = """
	            SELECT ps.parking_space_id
	            FROM parking_space ps
	            WHERE ps.is_available = TRUE
	              AND ps.parking_space_id NOT IN (
	                  SELECT parking_space_id
	                  FROM parking_history
	                  WHERE NOW() < exit_time
	                    AND entry_time < DATE_ADD(NOW(), INTERVAL 4 HOUR)
	              )
	            ORDER BY RAND()
	            LIMIT 1
	            """;
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql);
	             ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("parking_space_id");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return -1; // No suitable spot found
	}

	
	/**
	 * Updates the availability status of a parking space.
	 *
	 * @param parkingSpaceId the ID of the parking space
	 * @param isAvailable    true to mark as available, false to mark as occupied
	 */
	public void setSpotAvailability(int parkingSpaceId, boolean isAvailable) {
	    String sql = "UPDATE parking_space SET is_available = ? WHERE parking_space_id = ?";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setBoolean(1, isAvailable);
	            stmt.setInt(2, parkingSpaceId);
	            stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        System.err.println("Failed to update spot availability.");
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Retrieves a Subscriber object from the database using the given subscriber code.
	 * This method is used to refresh the subscriber's data (e.g., email or phone) from the DB.
	 *
	 * @param subscriberCode The unique subscriber code to look up.
	 * @return A Subscriber object if found; otherwise, null.
	 * @throws SQLException If a database access error occurs.
	 */
	public Subscriber getSubscriberByCode(String subscriberCode) throws SQLException {
	    String sql = "SELECT * FROM subscriber WHERE subscriber_code = ?";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, subscriberCode);
	            ResultSet rs = stmt.executeQuery();
	            if (rs.next()) {
	                return mapSubscriber(rs);
	            }
	        }
	    } finally {
	        releaseConnection(conn);
	    }
	    return null;
	}

	
	/**
	 * Inserts a system log entry for the specified action and subscriber.
	 *
	 * @param action          the action performed
	 * @param target          the target entity
	 * @param subscriberCode  the subscriber's unique code
	 */
	public void insertSystemLog(String action, String target, String subscriberCode) {
	    String sql = """
	            INSERT INTO system_log (action, target, by_user, log_time, note)
	            SELECT ?, ?, u.id, NOW(), 'Car Deposit'
	            FROM users u
	            JOIN subscriber s ON s.subscriber_id = u.id
	            WHERE s.subscriber_code = ?
	            """;
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, action);
	            stmt.setString(2, target);
	            stmt.setString(3, subscriberCode);
	            stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        System.err.println("Failed to insert system log.");
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Inserts a new parking history record into the database.
	 *
	 * @param history the parking history to insert
	 */
	public void insertParkingHistory(ParkingHistory history) {
	    String sql = """
	            INSERT INTO parking_history (
	                subscriber_code,
	                parking_space_id,
	                entry_time,
	                exit_time,
	                extended,
	                extended_hours,
	                was_late,
	                picked_up
	            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
	            """;
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, history.getSubscriberCode());
	            stmt.setInt(2, history.getParkingSpaceId());
	            stmt.setTimestamp(3, Timestamp.valueOf(history.getEntryTime()));
	            stmt.setTimestamp(4, Timestamp.valueOf(history.getExitTime()));
	            stmt.setBoolean(5, history.isExtended());
	            stmt.setInt(6, history.getExtendedHours());
	            stmt.setBoolean(7, history.isWasLate());
	            stmt.setBoolean(8, history.isPickedUp());
	            stmt.executeUpdate();
	            System.out.println("Parking deposit saved for " + history.getSubscriberCode());
	        }
	    } catch (SQLException e) {
	        System.err.println("Failed to insert parking deposit");
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Checks if a subscriber has an active parking reservation.
	 *
	 * @param subscriberCode the subscriber's unique code
	 * @return true if there is an active reservation; false otherwise
	 */
	public boolean hasActiveReservation(String subscriberCode) {
	    String sql = """
	            SELECT 1 FROM parking_history
	            WHERE subscriber_code = ?
	              AND entry_time <= NOW()
	              AND exit_time > NOW()
	            LIMIT 1
	            """;
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, subscriberCode);
	            try (ResultSet rs = stmt.executeQuery()) {
	                return rs.next();
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error checking active reservation:");
	        e.printStackTrace();
	        return false;
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Returns the current active parking session for the given subscriber.
	 * An active session is defined as: entry_time &lt;= NOW() &lt;= exit_time AND picked_up = 0
	 *
	 * @param subscriberCode The code of the subscriber.
	 * @return The active ParkingHistory record, or null if none found.
	 */


	public ParkingHistory getActiveParkingBySubscriber(String subscriberCode) {
	    String query = "SELECT * FROM parking_history " +
	                   "WHERE subscriber_code = ? " +
	                   "AND entry_time <= NOW() " +
	                   "AND exit_time >= NOW() " +
	                   "AND picked_up = 0 " +
	                   "ORDER BY entry_time DESC LIMIT 1";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setString(1, subscriberCode);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return mapParkingHistory(rs);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return null;
	}


	/**
	 * Checks if there is a reservation conflict in the given time window for a specific parking space.
	 *
	 * @param parkingSpaceId The ID of the parking space to check.
	 * @param currentExit    The current reservation exit time.
	 * @param newExit        The proposed new exit time.
	 * @return true if a conflict exists; false otherwise.
	 */
	public boolean isReservationConflict(int parkingSpaceId, LocalDateTime currentExit, LocalDateTime newExit) {
	    String query = "SELECT COUNT(*) FROM reservation " +
	                   "WHERE parking_space_id = ? " +
	                   "AND status = 'active' " +
	                   "AND (reservation_date BETWEEN ? AND ? " +
	                   "OR DATE_ADD(reservation_date, INTERVAL 4 HOUR) BETWEEN ? AND ?)";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setInt(1, parkingSpaceId);
	            stmt.setTimestamp(2, Timestamp.valueOf(currentExit));
	            stmt.setTimestamp(3, Timestamp.valueOf(newExit));
	            stmt.setTimestamp(4, Timestamp.valueOf(currentExit));
	            stmt.setTimestamp(5, Timestamp.valueOf(newExit));
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next() && rs.getInt(1) > 0) {
	                    return true; // Conflict exists
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return false;
	}

	

	/**
	 * Updates the exit time for the subscriber's latest parking session and marks it as extended.
	 *
	 * @param subscriberCode the subscriber's unique code
	 * @param newExitTime    the new exit time to set
	 * @return number of rows updated (should be 1 if successful)
	 */
	public int updateExitTime(String subscriberCode, LocalDateTime newExitTime) {
	    String query = "UPDATE parking_history SET exit_time = ?, extended = 1, extended_hours = extended_hours + ? " +
	                   "WHERE subscriber_code = ? AND exit_time > NOW() ORDER BY exit_time DESC LIMIT 1";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setTimestamp(1, Timestamp.valueOf(newExitTime));
	            stmt.setInt(2, EXTEND_HOURS_PER_REQUEST);
	            stmt.setString(3, subscriberCode);
	            return stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return 0;
	}


	/**
	 * Retrieves the most recent uncollected parking session for the subscriber and specific parking space.
	 *
	 * @param subscriberCode the subscriber's code
	 * @param parkingSpaceId the parking space ID
	 * @return the pending ParkingHistory record or null if not found
	 */
	public ParkingHistory getPendingParkingBySubscriberAndSpot(String subscriberCode, int parkingSpaceId) {
	    String query = "SELECT * FROM parking_history " +
	                   "WHERE subscriber_code = ? AND parking_space_id = ? AND picked_up = 0 " +
	                   "ORDER BY entry_time DESC LIMIT 1";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setString(1, subscriberCode);
	            stmt.setInt(2, parkingSpaceId);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return mapParkingHistory(rs);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return null;
	}

	

	/**
	 * Marks the parking session as picked up, updates exit time, and (if late) sets
	 * extended/was_late flags.
	 *
	 * @param subscriberCode The subscriber's code
	 * @param parkingSpaceId The parking spot ID
	 * @param wasLate        True if pickup is late
	 * @param pickupTime     The actual pickup time (now)
	 * @return number of rows updated (should be 1 if successful)
	 */
	public int completePickup(String subscriberCode, int parkingSpaceId, boolean wasLate, LocalDateTime pickupTime) {
	    String query;
	    if (wasLate) {
	        query = "UPDATE parking_history SET exit_time = ?, picked_up = 1, extended = 1, was_late = 1, extended_hours = extended_hours + ? " +
	                "WHERE subscriber_code = ? AND parking_space_id = ? AND picked_up = 0 ORDER BY entry_time DESC LIMIT 1";
	    } else {
	        query = "UPDATE parking_history SET exit_time = ?, picked_up = 1 " +
	                "WHERE subscriber_code = ? AND parking_space_id = ? AND picked_up = 0 ORDER BY entry_time DESC LIMIT 1";
	    }

	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setTimestamp(1, Timestamp.valueOf(pickupTime));
	            if (wasLate) {
	                stmt.setInt(2, EXTEND_HOURS_PER_REQUEST);
	                stmt.setString(3, subscriberCode);
	                stmt.setInt(4, parkingSpaceId);
	            } else {
	                stmt.setString(2, subscriberCode);
	                stmt.setInt(3, parkingSpaceId);
	            }
	            return stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return 0;
	}

	
	

	/**
	 * Checks if at least 40% of parking spots are available.
	 *
	 * @return true if reservation is possible (≥ 40%), false otherwise.
	 */
	public boolean isReservationPossible() {
	    String totalQuery = "SELECT COUNT(*) FROM parking_space";
	    String availableQuery = "SELECT COUNT(*) FROM parking_space WHERE is_available = TRUE";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        int totalSpots = 0;
	        int availableSpots = 0;

	        // Total spots
	        try (PreparedStatement stmt = conn.prepareStatement(totalQuery);
	             ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                totalSpots = rs.getInt(1);
	            }
	        }

	        // Available spots
	        try (PreparedStatement stmt = conn.prepareStatement(availableQuery);
	             ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                availableSpots = rs.getInt(1);
	            }
	        }

	        if (totalSpots == 0) {
	            return false; // Avoid division by zero
	        }

	        double percentage = (availableSpots / (double) totalSpots) * 100;
	        return percentage >= 40.0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // In case of error, assume not possible
	    } finally {
	        releaseConnection(conn);
	    }
	}

	
	
	/**
	 * Returns a list of available time slots for the given date and subscriber,
	 * ensuring at least one actual parking spot is available for the whole 4-hour
	 * window (not just by overlap percentage). The returned times are only those
	 * where 40% or more of the spots are available for the entire slot.
	 *
	 * @param selectedDate   The date to check.
	 * @param subscriberCode The subscriber making the request.
	 * @return A list of available LocalTime slots for reservation.
	 */
	public List<LocalTime> getAvailableTimesForDate(LocalDate selectedDate, String subscriberCode) {
	    List<LocalTime> availableTimes = new ArrayList<>();
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        int totalSpots = getTotalSpots(conn);
	        if (totalSpots == 0)
	            return availableTimes;

	        List<Integer> spotIds = getAllSpotIds(conn);

	        // Check every 15-minute slot in the day
	        for (LocalTime slotTime = LocalTime.of(0, 0); !slotTime.isAfter(LocalTime.of(23, 45)); slotTime = slotTime.plusMinutes(15)) {
	            LocalDateTime slotStart = LocalDateTime.of(selectedDate, slotTime);
	            LocalDateTime slotEnd = slotStart.plusHours(4);

	            int freeSpots = 0;
	            for (int spotId : spotIds) {
	                if (isSpotAvailableForWindow(conn, spotId, slotStart, slotEnd, subscriberCode)) {
	                    freeSpots++;
	                }
	            }

	            double availablePercent = ((double) freeSpots) / totalSpots;
	            if (availablePercent > 0.4) {
	                availableTimes.add(slotTime);
	            }

	            if (slotTime.equals(LocalTime.of(23, 45)))
	                break;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return availableTimes;
	}

	

	/**
	 * Returns the total number of parking spots in the system.
	 *
	 * @param conn An open database connection.
	 * @return The number of parking spaces.
	 * @throws SQLException If a database access error occurs.
	 */
	private int getTotalSpots(Connection conn) throws SQLException {
		String sql = "SELECT COUNT(*) FROM parking_space";
		try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	/**
	 * Returns a list of all parking spot IDs in the system.
	 *
	 * @param conn An open database connection.
	 * @return List of parking_space_id values.
	 * @throws SQLException If a database access error occurs.
	 */
	private List<Integer> getAllSpotIds(Connection conn) throws SQLException {
		List<Integer> ids = new ArrayList<>();
		String sql = "SELECT parking_space_id FROM parking_space";
		try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				ids.add(rs.getInt("parking_space_id"));
			}
		}
		return ids;
	}

	/**
	 * Checks if a specific parking spot is fully available for the requested window
	 * (4 hours), meaning there are no active reservations or parking sessions
	 * overlapping this time range.
	 *
	 * @param conn           The database connection.
	 * @param spotId         The parking space ID to check.
	 * @param start          The proposed reservation start time.
	 * @param end            The proposed reservation end time.
	 * @param subscriberCode The subscriber's code (used to ensure they have no
	 *                       conflicting parkings).
	 * @return true if the spot is available for the entire window; false otherwise.
	 * @throws SQLException If a database access error occurs.
	 */
	private boolean isSpotAvailableForWindow(Connection conn, int spotId, LocalDateTime start, LocalDateTime end,
			String subscriberCode) throws SQLException {
		// Check for conflicting reservations
		String reservationSql = """
				    SELECT 1 FROM reservation
				    WHERE parking_space_id = ?
				      AND status = 'active'
				      AND (
				        (? < DATE_ADD(reservation_date, INTERVAL 4 HOUR))
				        AND (DATE_ADD(?, INTERVAL 4 HOUR) > reservation_date)
				      )
				    LIMIT 1
				""";
		try (PreparedStatement stmt = conn.prepareStatement(reservationSql)) {
			stmt.setInt(1, spotId);
			stmt.setTimestamp(2, Timestamp.valueOf(start));
			stmt.setTimestamp(3, Timestamp.valueOf(start));
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return false;
			}
		}

		// Check for conflicting parking_history entries
		String historySql = """
				    SELECT 1 FROM parking_history
				    WHERE parking_space_id = ?
				      AND (exit_time > ? AND entry_time < ?)
				    LIMIT 1
				""";
		try (PreparedStatement stmt = conn.prepareStatement(historySql)) {
			stmt.setInt(1, spotId);
			stmt.setTimestamp(2, Timestamp.valueOf(start));
			stmt.setTimestamp(3, Timestamp.valueOf(end));
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return false;
			}
		}

		// Ensure the same subscriber does not already have parking in this window
		String selfSql = """
				    SELECT 1 FROM parking_history
				    WHERE subscriber_code = ?
				      AND (exit_time > ? AND entry_time < ?)
				    LIMIT 1
				""";
		try (PreparedStatement stmt = conn.prepareStatement(selfSql)) {
			stmt.setString(1, subscriberCode);
			stmt.setTimestamp(2, Timestamp.valueOf(start));
			stmt.setTimestamp(3, Timestamp.valueOf(end));
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return false;
			}
		}

		return true;
	}

	/**
	 * Handles all logic for creating a reservation:
	 * - Checks if the subscriber already has a reservation within 4 hours
	 * - Finds a random free parking spot for the requested 4-hour time window
	 * - Generates a unique confirmation code
	 * - Inserts the reservation into the database
	 * - Sends a confirmation email to the subscriber
	 *
	 * @param reservationRequest Reservation object from client (without assigned spot ID or confirmation code)
	 * @return true if the reservation was created successfully, false otherwise
	 */
	public boolean addReservationRandomSpotWithConfirmation(Reservation reservationRequest) {
	    Connection conn = null;
	    try {
	        conn = getConnection();

	        // Step 0: Check if subscriber already has a reservation within 4 hours
	        String overlapCheckSql = """
	            SELECT COUNT(*) FROM reservation 
	            WHERE subscriber_code = ? 
	              AND ABS(TIMESTAMPDIFF(MINUTE, reservation_date, ?)) < 240
	              AND status <> 'CANCELLED'
	        """;
	        try (PreparedStatement checkStmt = conn.prepareStatement(overlapCheckSql)) {
	            checkStmt.setString(1, reservationRequest.getSubscriberCode());
	            checkStmt.setTimestamp(2, Timestamp.valueOf(reservationRequest.getReservationDate()));
	            ResultSet rs = checkStmt.executeQuery();
	            if (rs.next() && rs.getInt(1) > 0) {
	                System.err.println("Subscriber already has a reservation within 4 hours of the requested time.");
	                return false;
	            }
	        }

	        // Step 1: Find a random free parking spot for the reservation time window
	        int spotId = getRandomFreeSpotForReservation(reservationRequest.getReservationDate(), conn);
	        if (spotId == -1) {
	            System.err.println("No available parking spots for the requested time.");
	            return false;
	        }

	        // Step 2: Generate a unique confirmation code
	        int confirmationCode = generateUniqueConfirmationCode(conn);

	        // Step 3: Insert the reservation into the reservation table
	        String reservationSql = "INSERT INTO reservation (subscriber_code, parking_space_id, reservation_date, confirmation_code, status) VALUES (?, ?, ?, ?, ?)";
	        try (PreparedStatement stmt = conn.prepareStatement(reservationSql)) {
	            stmt.setString(1, reservationRequest.getSubscriberCode());
	            stmt.setInt(2, spotId);
	            stmt.setTimestamp(3, Timestamp.valueOf(reservationRequest.getReservationDate()));
	            stmt.setInt(4, confirmationCode);
	            stmt.setString(5, reservationRequest.getStatus());
	            stmt.executeUpdate();
	        }

	        // Step 4: Get subscriber email address
	        String email = getSubscriberEmail(conn, reservationRequest.getSubscriberCode());
	        if (email == null) {
	            throw new SQLException("Subscriber email not found.");
	        }

	        // Step 5: Send confirmation email
	        String subject = "BPARK: Reservation Confirmation";
	        String body = "Your reservation is confirmed.\n" +
	                      "Parking Spot: " + spotId + "\n" +
	                      "Confirmation Code: " + confirmationCode + "\n" +
	                      "Date: " + reservationRequest.getReservationDate();
	        EmailUtil.sendEmail(email, subject, body);

	        return true;

	    } catch (SQLException | MessagingException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        releaseConnection(conn);
	    }
	}

	
	/**
	 * Finds a random free parking spot for a 4-hour window starting at the given
	 * reservation time, only if at least 40% of all parking spots are available in
	 * that window.
	 * 
	 * A parking spot is considered available if: - It has no active reservation
	 * overlapping with the requested time window - It has no entry in
	 * parking_history overlapping with the requested time window
	 * 
	 * @param reservationDateTime The requested reservation start time
	 *                            (LocalDateTime)
	 * @param conn                Active database connection
	 * @return Parking space ID if available and availability threshold is met; -1
	 *         otherwise
	 * @throws SQLException If a database access error occurs
	 */
	private int getRandomFreeSpotForReservation(LocalDateTime reservationDateTime, Connection conn)
			throws SQLException {
		Timestamp start = Timestamp.valueOf(reservationDateTime);
		Timestamp end = Timestamp.valueOf(reservationDateTime.plusHours(4));

		// Count total parking spots
		String countTotalSql = "SELECT COUNT(*) FROM parking_space";
		int totalSpots = 0;
		try (PreparedStatement stmt = conn.prepareStatement(countTotalSql); ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				totalSpots = rs.getInt(1);
			}
		}

		// Count available parking spots in the requested time window
		String countAvailableSql = """
				    SELECT COUNT(*) FROM parking_space ps
				    WHERE ps.parking_space_id NOT IN (
				        SELECT parking_space_id FROM reservation
				        WHERE status = 'active'
				          AND (? < DATE_ADD(reservation_date, INTERVAL 4 HOUR))
				          AND (DATE_ADD(?, INTERVAL 4 HOUR) > reservation_date)
				    )
				    AND ps.parking_space_id NOT IN (
				        SELECT parking_space_id FROM parking_history
				        WHERE (exit_time > ? AND entry_time < DATE_ADD(?, INTERVAL 4 HOUR))
				    )
				""";

		int availableSpots = 0;
		try (PreparedStatement stmt = conn.prepareStatement(countAvailableSql)) {
			stmt.setTimestamp(1, start);
			stmt.setTimestamp(2, start);
			stmt.setTimestamp(3, start);
			stmt.setTimestamp(4, end);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					availableSpots = rs.getInt(1);
				}
			}
		}

		// Check if at least 40% are available
		double availabilityRatio = (double) availableSpots / totalSpots;
		if (availabilityRatio <= 0.4) {
			System.err.println("Reservation denied: less than 40% of parking spots are available.");
			throw new RuntimeException("Reservation denied: less than 40% of parking spots are available.");
		}

		// Select a random available spot
		String selectSpotSql = """
				    SELECT ps.parking_space_id
				    FROM parking_space ps
				    WHERE ps.parking_space_id NOT IN (
				        SELECT parking_space_id FROM reservation
				        WHERE status = 'active'
				          AND (? < DATE_ADD(reservation_date, INTERVAL 4 HOUR))
				          AND (DATE_ADD(?, INTERVAL 4 HOUR) > reservation_date)
				    )
				    AND ps.parking_space_id NOT IN (
				        SELECT parking_space_id FROM parking_history
				        WHERE (exit_time > ? AND entry_time < DATE_ADD(?, INTERVAL 4 HOUR))
				    )
				    ORDER BY RAND()
				    LIMIT 1
				""";

		try (PreparedStatement stmt = conn.prepareStatement(selectSpotSql)) {
			stmt.setTimestamp(1, start);
			stmt.setTimestamp(2, start);
			stmt.setTimestamp(3, start);
			stmt.setTimestamp(4, end);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("parking_space_id");
				}
			}
		}

		return -1;
	}

	/**
	 * Generates a unique 6-digit confirmation code that does not exist in the
	 * reservation table.
	 *
	 * @param conn Active database connection
	 * @return A unique confirmation code
	 * @throws SQLException If a database access error occurs
	 */
	public int generateUniqueConfirmationCode(Connection conn) throws SQLException {
		Random random = new Random();
		int code;

		while (true) {
			code = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999

			String sql = "SELECT COUNT(*) FROM reservation WHERE confirmation_code = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, code);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next() && rs.getInt(1) == 0) {
						// Code is unique
						break;
					}
				}
			}
		}

		return code;
	}

	/**
	 * Retrieves the subscriber's email address based on their subscriber code.
	 *
	 * @param conn           Active database connection
	 * @param subscriberCode The subscriber's unique code
	 * @return Email address if found, or null if not found
	 * @throws SQLException If a database access error occurs
	 */
	public String getSubscriberEmail(Connection conn, String subscriberCode) throws SQLException {
		String sql = "SELECT email FROM subscriber WHERE subscriber_code = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, subscriberCode);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("email");
				}
			}
		}

		return null;
	}

	/**
	 * Convenience method to get a subscriber's email by code, opening its own connection.
	 *
	 * @param subscriberCode The subscriber code.
	 * @return The email address, or null if not found.
	 */
	public String getSubscriberEmail(String subscriberCode) {
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        return getSubscriberEmail(conn, subscriberCode);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        releaseConnection(conn);
	    }
	}

	
	/**
	 * Retrieves username and password by user ID.
	 *
	 * @param userId The user ID (from the scanned tag).
	 * @return {username, password} if found; null otherwise.
	 */
	public String[] getUserCredentialsByUserId(String userId) {
	    String sql = "SELECT username, password FROM users WHERE id = ?";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, userId);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    String username = rs.getString("username");
	                    String password = rs.getString("password");
	                    return new String[] { username, password };
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return null;
	}


	
	/**
	 * Retrieves all active (ongoing) parking sessions from the database.
	 *
	 * @return a list of ParkingHistory objects for active parkings
	 */
	public List<ParkingHistory> getAllActiveParkings() {
	    String sql = "SELECT * FROM parking_history " +
	                 "WHERE picked_up = 0 AND entry_time <= NOW() AND exit_time >= NOW() " +
	                 "ORDER BY entry_time DESC";
	    List<ParkingHistory> list = new ArrayList<>();
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql);
	             ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapParkingHistory(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return list;
	}


	/**
	 * Retrieves all subscribers along with user account details.
	 *
	 * @return a list of Subscriber objects
	 */
	public List<Subscriber> getAllSubscribers() {
	    String sql = """
	        SELECT
	            s.subscriber_id AS id,
	            s.subscriber_code,
	            s.email,
	            s.phone_number,
	            u.first_name,
	            u.last_name,
	            u.username
	        FROM
	            subscriber s
	        JOIN
	            users u ON s.subscriber_id = u.id
	        """;
	    List<Subscriber> list = new ArrayList<>();
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql);
	             ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapSubscriber(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return list;
	}

	

	/**
	 * Adds a new subscriber to the system by inserting into both users and subscriber tables.
	 * Rolls back the transaction on failure.
	 *
	 * @param subscriber  the subscriber entity
	 * @param password    the account password
	 * @param firstName   the user's first name
	 * @param lastName    the user's last name
	 * @return true if successfully added, false otherwise
	 */
	public boolean addSubscriber(Subscriber subscriber, String password, String firstName, String lastName) {
	    String insertUser = "INSERT INTO users (id, username, password, role, first_name, last_name) VALUES (?, ?, ?, 'subscriber', ?, ?)";
	    String insertSub = "INSERT INTO subscriber (subscriber_id, email, phone_number, subscriber_code) VALUES (?, ?, ?, ?)";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        conn.setAutoCommit(false);

	        try (PreparedStatement userStmt = conn.prepareStatement(insertUser);
	             PreparedStatement subStmt = conn.prepareStatement(insertSub)) {

	            String subscriberCode = "SUB" + subscriber.getId();

	            userStmt.setInt(1, subscriber.getId());
	            userStmt.setString(2, subscriberCode);
	            userStmt.setString(3, password);
	            userStmt.setString(4, firstName);
	            userStmt.setString(5, lastName);
	            userStmt.executeUpdate();

	            subStmt.setInt(1, subscriber.getId());
	            subStmt.setString(2, subscriber.getEmail());
	            subStmt.setString(3, subscriber.getPhone());
	            subStmt.setString(4, subscriberCode);
	            subStmt.executeUpdate();

	            conn.commit();
	            return true;

	        } catch (SQLException e) {
	            conn.rollback();
	            e.printStackTrace();
	            return false;
	        } finally {
	            conn.setAutoCommit(true);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        releaseConnection(conn);
	    }
	}

	
	/**
	 * Inserts a system log record for subscriber insertion.
	 *
	 * @param action    the action performed
	 * @param target    the affected object
	 * @param byUserId  the ID of the user who performed the action
	 */
	public void insertSubscriberSystemLog(String action, String target, int byUserId) {
	    String sql = """
	        INSERT INTO system_log (action, target, by_user, log_time, note)
	        VALUES (?, ?, ?, NOW(), ?)
	        """;
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, action);
	            stmt.setString(2, target);
	            stmt.setInt(3, byUserId);
	            stmt.setString(4, "Subscriber inserted");
	            stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        System.err.println("Failed to insert system log.");
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}

	

	/**
	 * Retrieves the next available system log ID (max + 1).
	 *
	 * @return the next log ID, or 1 if the table is empty or error occurs
	 */
	public int getNextSystemLogId() {
	    String sql = "SELECT MAX(log_id) FROM system_log";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql);
	             ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1) + 1;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return 1;
	}


	/**
	 * Retrieves the user ID by username.
	 *
	 * @param username the username to search for
	 * @return the user ID if found, or -1 if not found or error occurs
	 */
	public int getUserIdByUsername(String username) {
	    String sql = "SELECT id FROM users WHERE username = ?";
	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, username);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getInt("id");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	    return -1; // Not found
	}


	/**
	 * Retrieves all system logs from the database, ordered by most recent first.
	 *
	 * @return a list of SystemLog objects
	 */
	public List<SystemLog> getAllSystemLogs() {
	    List<SystemLog> logs = new ArrayList<>();
	    String sql = "SELECT log_id, action, target, by_user, log_time, note FROM system_log ORDER BY log_time DESC";

	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {

	            while (rs.next()) {
	                SystemLog log = new SystemLog();
	                log.setLogId(rs.getInt("log_id"));
	                log.setAction(rs.getString("action"));
	                log.setTarget(rs.getString("target"));
	                log.setByUser(rs.getInt("by_user"));
	                log.setLogTime(rs.getTimestamp("log_time").toLocalDateTime());
	                log.setNote(rs.getString("note"));
	                logs.add(log);
	            }

	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching system logs: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }

	    return logs;
	}


	/**
	 * Retrieves the email and phone number of a subscriber by their subscriber code.
	 *
	 * @param subscriberCode The unique subscriber code.
	 * @return A map containing "email" and "phone" keys if found, or null otherwise.
	 * @throws SQLException if a database access error occurs
	 */
	public Map<String, String> getSubscriberContactByCode(String subscriberCode) throws SQLException {
	    String sql = "SELECT email, phone_number FROM subscriber WHERE subscriber_code = ?";
	    Connection conn = null;

	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, subscriberCode);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    Map<String, String> contact = new HashMap<>();
	                    contact.put("email", rs.getString("email"));
	                    contact.put("phone", rs.getString("phone_number"));
	                    return contact;
	                }
	            }
	        }
	    } finally {
	        releaseConnection(conn);
	    }

	    return null; // No subscriber found
	}


	/**
	 * Retrieves an active reservation from the database by confirmation code.
	 *
	 * @param code the confirmation code entered by the subscriber
	 * @return a Reservation object if found and active; otherwise null
	 */
	public Reservation getReservationByConfirmationCode(String code) {
	    String sql = "SELECT * FROM reservation WHERE confirmation_code = ? AND status = 'active'";
	    Connection conn = null;

	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, code);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return new Reservation(
	                        rs.getInt("reservation_id"),
	                        rs.getString("subscriber_code"),
	                        rs.getInt("parking_space_id"),
	                        rs.getTimestamp("reservation_date").toLocalDateTime(),
	                        rs.getInt("confirmation_code"),
	                        rs.getString("status")
	                    );
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }

	    return null;
	}


	/**
	 * Marks a reservation as expired (used or time has passed).
	 *
	 * @param reservationId the reservation ID to update
	 */
	public void markReservationExpired(int reservationId) {
	    String sql = "UPDATE reservation SET status = 'expired' WHERE reservation_id = ?";
	    Connection conn = null;

	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, reservationId);
	            stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Inserts a parking history record using reservation data.
	 *
	 * @param reservation the reservation object to base the parking on
	 * @param entryTime   the actual time the subscriber entered
	 * @param exitTime    the planned exit time (usually 4 hours after reservation)
	 */
	public void insertParkingFromReservation(Reservation reservation, LocalDateTime entryTime, LocalDateTime exitTime) {
	    String sql = """
	        INSERT INTO parking_history (
	            subscriber_code,
	            parking_space_id,
	            entry_time,
	            exit_time,
	            extended,
	            was_late,
	            picked_up
	        ) VALUES (?, ?, ?, ?, ?, ?, ?)
	    """;
	    Connection conn = null;

	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, reservation.getSubscriberCode());
	            stmt.setInt(2, reservation.getParkingSpaceId());
	            stmt.setTimestamp(3, Timestamp.valueOf(entryTime));
	            stmt.setTimestamp(4, Timestamp.valueOf(exitTime));
	            stmt.setBoolean(5, false); // extended
	            stmt.setBoolean(6, false); // was_late
	            stmt.setBoolean(7, false); // picked_up
	            stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}


	/**
	 * Cancels a reservation by updating its status to 'cancelled'.
	 *
	 * @param reservationId the ID of the reservation to cancel
	 */
	public void cancelReservation(int reservationId) {
	    String sql = "UPDATE reservation SET status = 'cancelled' WHERE reservation_id = ?";
	    Connection conn = null;

	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, reservationId);
	            stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}

	
	/**
	 * Loads a MonthlyParkingTimeReport from the database for the given year and month.
	 * This retrieves the normal, extended, and delayed hours from the aggregated
	 * monthly_parking_time_report table, and returns it as a MonthlyParkingTimeReport entity.
	 *
	 * @param year  the year to load
	 * @param month the month to load
	 * @return a MonthlyParkingTimeReport if found, null otherwise
	 */
	public static MonthlyParkingTimeReport loadMonthlyParkingTimeReport(int year, int month) {
	    MonthlyParkingTimeReport report = null;
	    String sql = """
	        SELECT normal_hours, extended_hours, delayed_hours
	        FROM monthly_parking_time_report
	        WHERE year = ? AND month = ?
	    """;

	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, year);
	            stmt.setInt(2, month);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    int normal = rs.getInt("normal_hours");
	                    int extended = rs.getInt("extended_hours");
	                    int delayed = rs.getInt("delayed_hours");
	                    String ym = year + "-" + String.format("%02d", month);
	                    report = new MonthlyParkingTimeReport(ym, normal, extended, delayed);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }

	    return report;
	}

	
	/**
	 * Loads a MonthlySubscriberReport from the database for the given year and month.
	 * This retrieves the CSV-like daily subscriber counts from the
	 * monthly_subscriber_report table and converts them into a list of integers.
	 *
	 * @param year  the year to load
	 * @param month the month to load
	 * @return a MonthlySubscriberReport if found, null otherwise
	 */
	public static MonthlySubscriberReport loadMonthlySubscriberReport(int year, int month) {
	    MonthlySubscriberReport report = null;
	    String sql = """
	        SELECT daily_subscribers
	        FROM monthly_subscriber_report
	        WHERE year = ? AND month = ?
	    """;

	    Connection conn = null;
	    try {
	        conn = getConnection();
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, year);
	            stmt.setInt(2, month);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    String raw = rs.getString("daily_subscribers");
	                    List<Integer> counts = new ArrayList<>();
	                    if (raw != null && !raw.isBlank()) {
	                        for (String part : raw.split(",")) {
	                            counts.add(Integer.parseInt(part.trim()));
	                        }
	                    }
	                    String ym = year + "-" + String.format("%02d", month);
	                    report = new MonthlySubscriberReport(ym, counts);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }

	    return report;
	}

	
	
	/**
	 * Generates and stores the monthly reports for a given year and month.
	 * It aggregates:
	 *  - normal parking hours
	 *  - extended parking hours
	 *  - delayed parking hours
	 *  from the parking_history table, and also
	 *  - daily subscriber parking counts
	 * storing them into the monthly_parking_time_report and
	 * monthly_subscriber_report tables with upsert logic.
	 *
	 * @param year  the year to generate
	 * @param month the month to generate
	 */
	public static void generateMonthlyReports(int year, int month) {
	    int normalHours = 0;
	    int extendedHours = 0;
	    int delayedHours = 0;
	    List<Integer> dailyCounts = new ArrayList<>();
	    String ymPrefix = year + "-" + String.format("%02d", month);

	    Connection conn = null;
	    try {
	        conn = getConnection();

	        // Aggregate parking hours
	        String sql = """
	            SELECT entry_time, exit_time, extended, was_late
	            FROM parking_history
	            WHERE DATE_FORMAT(entry_time, '%Y-%m') = ?
	        """;
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setString(1, ymPrefix);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                Timestamp entry = rs.getTimestamp("entry_time");
	                Timestamp exit = rs.getTimestamp("exit_time");
	                if (exit == null) continue;

	                long hours = Duration.between(entry.toLocalDateTime(), exit.toLocalDateTime()).toHours();

	                if (rs.getBoolean("extended")) {
	                    extendedHours += hours;
	                } else if (rs.getBoolean("was_late")) {
	                    delayedHours += hours;
	                } else {
	                    normalHours += hours;
	                }
	            }
	        }

	        // Aggregate subscriber daily counts
	        YearMonth ym = YearMonth.of(year, month);
	        int lastDay = ym.lengthOfMonth();

	        for (int day = 1; day <= lastDay; day++) {
	            String date = String.format("%d-%02d-%02d", year, month, day);
	            String countSql = """
	                SELECT COUNT(DISTINCT subscriber_code)
	                FROM parking_history
	                WHERE DATE(entry_time) = ?
	            """;
	            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
	                ps.setString(1, date);
	                ResultSet rs = ps.executeQuery();
	                int count = 0;
	                if (rs.next()) count = rs.getInt(1);
	                dailyCounts.add(count);
	            }
	        }

	        // Store into monthly_parking_time_report
	        String storeTime = """
	            INSERT INTO monthly_parking_time_report (year, month, normal_hours, extended_hours, delayed_hours)
	            VALUES (?, ?, ?, ?, ?)
	            ON DUPLICATE KEY UPDATE
	                normal_hours=VALUES(normal_hours),
	                extended_hours=VALUES(extended_hours),
	                delayed_hours=VALUES(delayed_hours)
	        """;
	        try (PreparedStatement ps = conn.prepareStatement(storeTime)) {
	            ps.setInt(1, year);
	            ps.setInt(2, month);
	            ps.setInt(3, normalHours);
	            ps.setInt(4, extendedHours);
	            ps.setInt(5, delayedHours);
	            ps.executeUpdate();
	        } 

	        // Store into monthly_subscriber_report
	        String csvCounts = dailyCounts.stream()
	            .map(Object::toString)
	            .reduce((a, b) -> a + "," + b)
	            .orElse("");

	        String storeSubs = """
	            INSERT INTO monthly_subscriber_report (year, month, daily_subscribers)
	            VALUES (?, ?, ?)
	            ON DUPLICATE KEY UPDATE
	                daily_subscribers=VALUES(daily_subscribers)
	        """;
	        try (PreparedStatement ps = conn.prepareStatement(storeSubs)) {
	            ps.setInt(1, year);
	            ps.setInt(2, month);
	            ps.setString(3, csvCounts);
	            ps.executeUpdate();
	        }

	        System.out.printf("Monthly reports for %s generated successfully.%n", ymPrefix);

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        releaseConnection(conn);
	    }
	}


}
