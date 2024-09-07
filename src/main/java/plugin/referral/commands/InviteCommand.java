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
 * This class handles the "/invite" command, allowing players to invite another player.
 * The player will only be registered as invited if they haven't been invited before.
 */
public class InviteCommand implements CommandExecutor {

    private final Logger logger;

    /**
     * Constructor for the InviteCommand class.
     * Initializes the logger using the plugin instance.
     *
     * @param plugin The instance of the main plugin class to access the logger.
     */
    public InviteCommand(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
    }

    /**
     * Handles the "/invite <player>" command.
     * Registers an invitation if the target player hasn't already been invited.
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
            if (args.length < 1) {
                player.sendMessage("You must specify a player to invite.");
                return false;
            }

            String inviterName = player.getName();
            String inviteeName = args[0];

            if (inviterName.equalsIgnoreCase(inviteeName)) {
                player.sendMessage("You cannot invite yourself.");
                logger.info("Player " + inviterName + " tried to invite themselves.");
                return false;
            }

            try {
                boolean alreadyInvited = DatabaseManager.getInstance().isAlreadyInvited(inviterName, inviteeName);
                if (alreadyInvited) {
                    player.sendMessage(inviteeName + " has already been invited.");
                } else {
                    DatabaseManager.getInstance().registerInvitation(inviterName, inviteeName);
                    player.sendMessage("You have successfully invited " + inviteeName + ".");
                }
            } catch (SQLException e) {
                player.sendMessage(inviteeName + " does not exist or is not an active player");
                logger.severe("Error inviting player " + inviteeName + " by " + inviterName + ": " + e.getMessage());
            }
            return true;
        }
        return false;
    }
}
