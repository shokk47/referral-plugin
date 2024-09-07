package plugin.referral.others;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.referral.db.DatabaseManager;

import java.io.File;

/**
 * This class handles the configuration of the plugin's configuration file and the database connection settings.
 * It reads the necessary information from the configuration file (config.yml) and ensures that the database is properly configured.
 */
public class ConfigurationFile {

    // Configuration file paths for database settings
    private static final String HOST_FILEPATH = "database.host";
    private static final String PORT_FILEPATH = "database.port";
    private static final String NAME_FILEPATH = "database.name";
    private static final String USER_FILEPATH = "database.user";
    private static final String PASSWORD_FILEPATH = "database.password";
    private static final String CONFIG_FILE_NAME = "config.yml";

    // Reference to the JavaPlugin instance
    private final JavaPlugin plugin;

    /**
     * Constructor for the ConfigurationFile class.
     * It initializes the configuration file by ensuring defaults are set if the file does not exist.
     *
     * @param plugin The plugin instance.
     */
    public ConfigurationFile(JavaPlugin plugin) {
        this.plugin = plugin;
        createDefaultConfig();
    }

    /**
     * Creates the default configuration if the configuration file does not exist.
     * The default values for the database connection are set if the file is missing.
     */
    private void createDefaultConfig() {
        FileConfiguration config = plugin.getConfig();
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            config.addDefault(HOST_FILEPATH, "localhost");
            config.addDefault(PORT_FILEPATH, 3306);
            config.addDefault(NAME_FILEPATH, "my_database");
            config.addDefault(USER_FILEPATH, "root");
            config.addDefault(PASSWORD_FILEPATH, "password");
            config.options().copyDefaults(true);
            plugin.saveConfig();
        }
    }

    /**
     * Configures the database connection by reading the settings from the configuration file.
     * It retrieves the host, port, database name, username, and password and sets up the database connection accordingly.
     *
     * @throws Exception If any required parameter is missing or null.
     */
    public void configureDatabase() throws Exception {
        FileConfiguration config = plugin.getConfig();
        String dbHost = config.getString(HOST_FILEPATH);
        int dbPort = config.getInt(PORT_FILEPATH);
        String dbName = config.getString(NAME_FILEPATH);
        String dbUser = config.getString(USER_FILEPATH);
        String dbPassword = config.getString(PASSWORD_FILEPATH);

        if (dbHost == null || dbName == null || dbUser == null || dbPassword == null) {
            throw new Exception("Failed to configure the database: one or more parameters are null.");
        }

        // Configure the database connection using the DatabaseManager
        DatabaseManager.getInstance().configure(dbHost, dbPort, dbName, dbUser, dbPassword);
        plugin.getLogger().info("Database successfully configured.");
    }
}
