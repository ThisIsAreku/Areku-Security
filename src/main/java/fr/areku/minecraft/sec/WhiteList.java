package fr.areku.minecraft.sec;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * @author Alexandre
 */
public class WhiteList implements Listener {

    private SecurityPlugin plugin;
    private boolean enabled;

    public WhiteList(SecurityPlugin plugin) {
        this.plugin = plugin;
        this.enabled = this.plugin.getConfig().getBoolean("whitelist.enabled");
        this.plugin.getServer().getPluginManager()
                .registerEvents(this, this.plugin);
        loadMysql();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.plugin.getConfig().set("whitelist.enabled", enabled);
        try {
            this.plugin.getConfig().save(this.plugin.cfgFile);
        } catch (IOException e) {
            SecurityPlugin.log(Level.WARNING, "Cannot save whitelist stated");
        }
        loadMysql();
    }

    private void loadMysql() {
        try {
            if (this.enabled) {
                //SecurityPlugin.log("Opening MySQL connection...");
                this.plugin.mySQLClient.connect();
            } else {
                this.plugin.mySQLClient.close();
            }
        } catch (SQLException e) {
            SecurityPlugin.log(Level.WARNING, "Cannot enable whitelist");
            this.enabled = false;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!this.enabled)
            return;
        if (this.plugin
                .getServer()
                .getWhitelistedPlayers()
                .contains(
                        this.plugin.getServer().getOfflinePlayer(
                                event.getName()))) {
            if (SecurityPlugin.verbose) {
                SecurityPlugin.log(String.format("Bypass check for %s", event
                        .getName()));
            }
            return;
        }
        AsyncPlayerPreLoginEvent.Result rslt = AsyncPlayerPreLoginEvent.Result.ALLOWED;
        String msg = "";
        if (this.plugin.mySQLClient.checkConnectionIsAlive(true)) {
            try {
                PreparedStatement preparedQuery = this.plugin.mySQLClient.prepareStatement(this.plugin.whiteListCommand);
                preparedQuery.setString(1, event.getName());
                ResultSet query = preparedQuery.executeQuery();
                int i = 0;
                while (query.next()) {
                    i++;
                }
                if (i == 0) {
                    rslt = AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST;
                    msg = "Vous n'êtes pas autorisé a vous connecter. Vous devez vous enregistrer d'abord";
                } else if (i != 1) {
                    rslt = AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
                    msg = "Erreur de base de donnée, serveur inacessible. Reessayez plus tard !";
                }
            } catch (SQLException ex) {
                rslt = AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
                msg = "Erreur de base de donnée, serveur innacessible. Reessayez plus tard !";
            }
        } else {
            rslt = AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
            msg = "Erreur de base de donnée, serveur innacessible. Reessayez plus tard !";
        }
        if (SecurityPlugin.verbose) {
            SecurityPlugin.log(String.format("%s for %s", rslt.toString(), event
                    .getName()));
        }
        event.disallow(rslt, msg);
    }
}