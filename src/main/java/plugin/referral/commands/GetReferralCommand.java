package plugin.referral.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.referral.db.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This class handles the "/getReferral" command execution, allowing players to retrieve their referral code.
 * If the player has a referral code stored in the database, it is displayed to them.
 * If the code cannot be retrieved, the player is informed to try reconnecting.
 */
public class GetReferralCommand implements CommandExecutor {

    // Reference to the JavaPlugin's logger
    private final Logger logger;

    /**
     * Constructor for the GetReferralCommand class.
     * Initializes the logger using the plugin instance.
     *
     * @param plugin The instance of the main plugin class to access the logger.
     */
    public GetReferralCommand(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
    }

    /**
     * Handles the referral command execution.
     * Retrieves the player's referral code from the database and sends it to the player.
     * Logs any database errors encountered.
     *
     * @param sender  The entity that executed the command.
     * @param command The command that was executed.
     * @param label   The alias of the command.
     * @param args    The command arguments.
     * @return true if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            String playerName = player.getName();

            try {
                String referralCode = DatabaseManager.getInstance().getReferralCode(playerName);
                if (referralCode != null) {
                    player.sendMessage("Your referral code is: " + referralCode);
                } else {
                    player.sendMessage("Unable to retrieve your referral code. Try reconnecting to the server.");
                }
            } catch (SQLException e) {
                player.sendMessage("An error occurred while executing the command. Please contact the staff.");
                logger.severe("Error retrieving referral code for player " + playerName + ": " + e.getMessage());
            }
            return true;
        }
        return false;
    }
}
