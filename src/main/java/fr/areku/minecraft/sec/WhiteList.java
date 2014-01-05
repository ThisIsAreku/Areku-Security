package fr.areku.minecraft.sec;

import org.bukkit.Bukkit;
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
public class WhiteList extends BaseSecurityClass {

    public WhiteList() {
        super("whitelist");
        registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!isEnabled())
            return;
        loadMysql();
        if (Bukkit.getServer().getWhitelistedPlayers().contains(Bukkit.getServer().getOfflinePlayer(event.getName()))) {
            if (SecurityPlugin.verbose) {
                SecurityPlugin.log(String.format("Bypass check for %s", event.getName()));
            }
            return;
        }
        AsyncPlayerPreLoginEvent.Result rslt = AsyncPlayerPreLoginEvent.Result.ALLOWED;
        String msg = "";
        if (SecurityPlugin.getInstance().mySQLClient.checkConnectionIsAlive(true)) {
            try {
                PreparedStatement preparedQuery = SecurityPlugin.getInstance().mySQLClient.prepareStatement(SecurityPlugin.getInstance().whiteListCommand);
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