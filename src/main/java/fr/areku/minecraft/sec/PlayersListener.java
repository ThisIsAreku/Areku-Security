package fr.areku.minecraft.sec;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayersListener implements Listener {
    public PlayersListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SecurityPlugin.getInstance());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (Volatile.contains("server-lock")) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Le serveur est actuellement en maintenance");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Object lastp = Volatile.get("lastPlayerLogin");
        Object lastpTime = Volatile.get("lastPlayerLoginTime");
        if ((lastp != null) && (lastpTime != null)) {
            event.getPlayer().sendMessage("Last login: " + ChatColor.YELLOW + lastp.toString() + ChatColor.RESET + " (" + lastpTime.toString() + ")");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Volatile.set("lastPlayerLoginTime", simpleDateFormat.format(new Date()));
        Volatile.set("lastPlayerLogin", event.getPlayer().getName());
    }
}
