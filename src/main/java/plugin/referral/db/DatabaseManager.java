package plugin.referral.db;

import plugin.referral.others.ReferralCodeGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing the database operations for the referral system.
 * It provides methods to configure the database, handle referrals, and invitations between players.
 */
public final class DatabaseManager {

    private static DatabaseManager instance;
    private static String dbHost;
    private static int dbPort;
    private static String dbName;
    private static String dbUser;
    private static String dbPassword;

    /**
     * Private constructor to prevent instantiation outside the singleton pattern.
     */
    private DatabaseManager() throws SQLException {
    }

    /**
     * Retrieves the singleton instance of DatabaseManager.
     *
     * @return The singleton instance of DatabaseManager.
     * @throws SQLException If any SQL error occurs during instantiation.
     */
    public static DatabaseManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Configures the database connection parameters and ensures that the necessary tables are created.
     *
     * @param host     The database host.
     * @param port     The database port.
     * @param name     The database name.
     * @param user     The database username.
     * @param password The database password.
     * @throws SQLException If any SQL error occurs while configuring the database or creating tables.
     */
    public void configure(String host, int port, String name, String user, String password) throws SQLException {
        dbHost = host;
        dbPort = port;
        dbName = name;
        dbUser = user;
        dbPassword = password;

        String sqlReferral = "CREATE TABLE IF NOT EXISTS Referral ("
                + "username VARCHAR(255) PRIMARY KEY, "
                + "referral_code CHAR(8) NOT NULL"
                + ")";

        String sqlInvitations = "CREATE TABLE IF NOT EXISTS invitations ("
                + "inviter VARCHAR(255), "
                + "invitee VARCHAR(255), "
                + "PRIMARY KEY (inviter, invitee), "
                + "FOREIGN KEY (inviter) REFERENCES Referral(username) ON DELETE CASCADE, "
                + "FOREIGN KEY (invitee) REFERENCES Referral(username) ON DELETE CASCADE"
                + ")";

        instance.executeUpdate(sqlReferral);
        instance.executeUpdate(sqlInvitations);
    }

    /**
     * Retrieves the referral code for a given username.
     *
     * @param username The username of the player.
     * @return The referral code associated with the username, or null if not found.
     * @throws SQLException If any SQL error occurs while retrieving the referral code.
     */
    public String getReferralCode(String username) throws SQLException {
        String query = "SELECT referral_code FROM Referral WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("referral_code");
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a list of invitees invited by a specific inviter.
     *
     * @param inviter The username of the inviter.
     * @return A list of invitees invited by the inviter.
     * @throws SQLException If any SQL error occurs while retrieving the invitees.
     */
    public List<String> getInvites(String inviter) throws SQLException {
        List<String> invites = new ArrayList<>();
        String query = "SELECT invitee FROM invitations WHERE inviter = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, inviter);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invites.add(rs.getString("invitee"));
                }
            }
        }
        return invites;
    }

    /**
     * Checks if a specific invitee has already been invited by the inviter.
     *
     * @param inviter The username of the inviter.
     * @param invitee The username of the invitee.
     * @return true if the invitee has already been invited, false otherwise.
     * @throws SQLException If any SQL error occurs while checking the invitation status.
     */
    public boolean isAlreadyInvited(String inviter, String invitee) throws SQLException {
        String query = "SELECT COUNT(*) FROM invitations WHERE inviter = ? AND invitee = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, inviter);
            stmt.setString(2, invitee);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Return true if there is at least one record
                }
            }
        }
        return false;
    }

    /**
     * Registers a new invitation from an inviter to an invitee in the database.
     *
     * @param inviter The username of the inviter.
     * @param invitee The username of the invitee.
     * @throws SQLException If any SQL error occurs while registering the invitation.
     */
    public void registerInvitation(String inviter, String invitee) throws SQLException {
        String query = "INSERT INTO invitations (inviter, invitee) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, inviter);
            stmt.setString(2, invitee);
            stmt.executeUpdate();
        }
    }

    /**
     * Registers a new referral for a given username, generating a new referral code.
     *
     * @param username The username of the player to register the referral for.
     * @throws SQLException If any SQL error occurs while registering the referral.
     */
    public void registerReferral(String username) throws SQLException {
        String query = "INSERT INTO Referral (username, referral_code) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ReferralCodeGenerator referralCode = new ReferralCodeGenerator();
            stmt.setString(1, username);
            stmt.setString(2, referralCode.getCode());
            stmt.executeUpdate();
        }
    }

    /**
     * Establishes a connection to the MySQL database.
     *
     * @return The database connection.
     * @throws SQLException If any SQL error occurs while establishing the connection.
     */
    private Connection getConnection() throws SQLException {
        String connectionUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword;
        return DriverManager.getConnection(connectionUrl);
    }

    /**
     * Executes an update query to create or modify tables in the database.
     *
     * @param sql The SQL query to execute.
     * @throws SQLException If any SQL error occurs while executing the update.
     */
    private void executeUpdate(String sql) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}
