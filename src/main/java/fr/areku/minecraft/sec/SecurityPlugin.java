package fr.areku.minecraft.sec;

import fr.areku.minecraft.commons.MySQLPool;
import fr.areku.minecraft.sec.commands.normal;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * s
 *
 * @author Alexandre
 */
public class SecurityPlugin extends JavaPlugin {

    static SecurityPlugin instance;
    static boolean verbose = false;
    public String mysql_url = "";
    public String mysql_user = "";
    public String mysql_pass = "";
    public int permissionReloadScheduleId;
    public MySQLPool mySQLClient;
    public File cfgFile;

    public PermissionReload permissionReload;
    public long permissionInterval = 0L;

    //public Borders borderFilter;
    public int min_x;
    public int max_x;
    public int min_z;
    public int max_z;

    public WhiteList whitelistFilter;
    public String whiteListCommand = "";

    public Password passwordChecker;
    public String passwordCommand = "";

    @Override
    public void onDisable() {
        try {
            if (mySQLClient != null) mySQLClient.close();
        } catch (SQLException e) {
            logException(e, "close");
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        //this.getCommand("ointernal").setExecutor(new Commandr(this));
        // log.log(Level.INFO, "[OrvaleInternal] enabled v{0}",
        // this.getDescription().getVersion());
        if (getServer().getPluginManager().getPlugin("plugins-common") == null) {
            log(Level.SEVERE, "Ce plugin requiert plugins-common");
            getPluginLoader().disablePlugin(this);
            return;
        }
        try {
            loadConfig();

            boolean ready = false;
            while (!ready) {
                try {
                    this.mySQLClient = MySQLPool.getPool();
                    ready = true;
                } catch (Exception e) {
                    Thread.sleep(500);
                    // empty
                }
            }
            /*
             * Permissions
			 */
            if (this.permissionInterval > 0) {
                this.permissionReload = new PermissionReload(this);
                this.permissionReloadScheduleId = this.getServer()
                        .getScheduler()
                        .scheduleSyncRepeatingTask(this,
                                permissionReload, permissionInterval,
                                permissionInterval);
            }
            /*
             * Whitelist
			 */
            this.whitelistFilter = new WhiteList(this);
            /*
             * Password
			 */
            this.passwordChecker = new Password(this);

			/*
             * Security
			 */
            //this.securityFilter = new Security(this);
			
			/*
			 * Borders
			 */
            //this.borderFilter = new Borders(this);

            getServer().getPluginManager().registerEvents(new PlayersListener(), this);


            getCommand("normal").setExecutor(new normal());

        } catch (Exception e) {
            logException(e, "Areku-Security error..");
            this.setEnabled(false);
        }
    }


    public void reload() {
        try {
            this.getServer()
                    .getScheduler().cancelTask(this.permissionReloadScheduleId);
            this.mySQLClient.close();
        } catch (SQLException e) {
            // empty
        }
        try {
            loadConfig();

            log("Opening MySQL connection...");
			/*
			 * Permissions
			 */
            PermissionReload pr = new PermissionReload(this);
            if (this.permissionInterval > 0) {
                this.permissionReloadScheduleId = this.getServer()
                        .getScheduler()
                        .scheduleSyncRepeatingTask(this,
                                pr, permissionInterval,
                                permissionInterval);
            }
			/*
			 * Whitelist
			 */
        } catch (Exception e) {
            logException(e, "Areku-Security error..");
            this.setEnabled(false);
        }
    }

    public void loadConfig() throws IOException, InvalidConfigurationException {
        File cfgDir = this.getDataFolder();
        cfgFile = new File(cfgDir, "config.yml");

        if (!cfgDir.exists())
            if (!cfgDir.mkdirs())
                throw new IOException("Error creating dirs");

        if (!cfgFile.exists())
            copy(this.getResource("config.yml"), cfgFile);

        this.getConfig().load(cfgFile);
        this.getConfig().addDefaults(YamlConfiguration.loadConfiguration(this.getResource("config.yml")));
        this.getConfig().options().copyDefaults(true);

        this.permissionInterval = this.getConfig().getInt("permissions.interval");

        this.whiteListCommand = this.getConfig().getString("whitelist.command");
        this.passwordCommand = this.getConfig().getString("password.command");

        this.mysql_url = this.getConfig().getString("mysql.url");
        this.mysql_user = this.getConfig().getString("mysql.user");
        this.mysql_pass = this.getConfig().getString("mysql.pass");

        this.min_x = this.getConfig().getInt("borders.x.min");
        this.max_x = this.getConfig().getInt("borders.x.max");
        this.min_z = this.getConfig().getInt("borders.z.min");
        this.max_z = this.getConfig().getInt("borders.z.max");

        SecurityPlugin.verbose = this.getConfig().getBoolean("verbose");

        this.getConfig().save(cfgFile);

        log("Permissions");
        log(String.format("\t interval : %s s", this.permissionInterval));
        log("Whitelist");
        log(String.format("\t command : \"%s\"", this.whiteListCommand));

        this.permissionInterval *= 20;
    }

    private void copy(InputStream src, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = src.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        src.close();
        out.close();
    }

    public static void log(Level l, String m) {
        instance.getLogger().log(l, m);
    }

    public static void log(String m) {
        instance.getLogger().log(Level.INFO, m);
    }

    public static void logException(Exception e, String m) {
        log(Level.SEVERE, "---------------------------------------");
        log(Level.SEVERE, "--- an unexpected error has occured ---");
        log(Level.SEVERE, "-- please send line below to the dev --");
        log(Level.SEVERE, "Message: " + m);
        if (e instanceof SQLException) {
            log(Level.SEVERE, "SQLState: " + ((SQLException) e).getSQLState());
            log(Level.SEVERE, "Error Code: " + ((SQLException) e).getErrorCode());
        }
        log(Level.SEVERE, e.toString() + " : " + e.getLocalizedMessage());
        for (StackTraceElement t : e.getStackTrace()) {
            log(Level.SEVERE, "\t" + t.toString());
        }
        log(Level.SEVERE, "---------------------------------------");
    }
}
