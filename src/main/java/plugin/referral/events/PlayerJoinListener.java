package plugin.referral.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.referral.db.DatabaseManager;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Listener class for handling player join events and registering referral codes if necessary.
 */
public class PlayerJoinListener implements Listener {

    private final Logger logger;

    /**
     * Constructor for the PlayerJoinListener.
     *
     * @param plugin The instance of the main plugin class to access the logger.
     */
    public PlayerJoinListener(JavaPlugin plugin) {
        this.logger = plugin.getLogger(); // Access the plugin's logger
    }

    /**
     * Event handler for PlayerJoinEvent.
     * Registers a referral code for the player if they do not already have one.
     * Logs any database errors encountered.
     *
     * @param event The player join event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        try {
            String referralCode = DatabaseManager.getInstance().getReferralCode(playerName);
            if (referralCode == null) {
                DatabaseManager.getInstance().registerReferral(playerName);
                player.sendMessage("Your referral code has been generated.");
            }
        } catch (SQLException e) {
            player.sendMessage("An error occurred while accessing the database. Please contact the administrator.");
            logger.severe("Error accessing database for player " + playerName + ": " + e.getMessage());
        }
    }
}
