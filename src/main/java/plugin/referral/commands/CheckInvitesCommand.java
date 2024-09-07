package plugin.referral.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.referral.db.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class handles the "/checkInvites" command, allowing players to see how many users they have invited.
 */
public class CheckInvitesCommand implements CommandExecutor {

    private final Logger logger;

    /**
     * Constructor for the CheckInvitesCommand class.
     * Initializes the logger using the plugin instance.
     *
     * @param plugin The instance of the main plugin class to access the logger.
     */
    public CheckInvitesCommand(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
    }

    /**
     * Handles the "/checkInvites" command.
     * Retrieves the list of players invited by the user and sends it to them.
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
            String inviterName = player.getName();

            try {
                List<String> invites = DatabaseManager.getInstance().getInvites(inviterName);

                if (invites.isEmpty()) {
                    player.sendMessage("You haven't invited any players yet.");
                } else {
                    player.sendMessage("You have invited the following players: " + invites.size());
                }

                logger.info("Player " + inviterName + " checked their invites. Total invites: " + invites.size());
            } catch (SQLException e) {
                player.sendMessage("An error occurred while checking your invites. Please contact the staff.");
                logger.severe("Error checking invites for player " + inviterName + ": " + e.getMessage());
            }
            return true;
        }
        return false;
    }
}
