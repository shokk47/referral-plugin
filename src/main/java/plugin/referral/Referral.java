package plugin.referral;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.referral.commands.CheckInvitesCommand;
import plugin.referral.commands.GetReferralCommand;
import plugin.referral.events.PlayerJoinListener;
import plugin.referral.others.ConfigurationFile;
import plugin.referral.commands.InviteCommand;

import java.sql.SQLException;

public final class Referral extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            ConfigurationFile config = new ConfigurationFile(this);
            config.configureDatabase();
        } catch (SQLException e) {
            getLogger().severe("Errore di connessione al database durante l'abilitazione del plugin Referral.");
            getServer().getPluginManager().disablePlugin(this);
        } catch (Exception e) {
            getLogger().severe("Errore imprevisto durante l'abilitazione del plugin Referral.");
            getServer().getPluginManager().disablePlugin(this);
        }

        getCommand("getReferral").setExecutor(new GetReferralCommand(this));
        getCommand("invite").setExecutor(new InviteCommand(this));
        getCommand("checkInvites").setExecutor(new CheckInvitesCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
