package fr.areku.minecraft.sec;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Copyright (C) Areku-Security - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alexandre, 20/12/13
 */
public class Password implements Listener {

    private SecurityPlugin plugin;
    private boolean enabled;

    public Password(SecurityPlugin plugin) {
        this.plugin = plugin;
        this.enabled = this.plugin.getConfig().getBoolean("password.enabled");
        this.plugin.getServer().getPluginManager()
                .registerEvents(this, this.plugin);
        this.plugin.getServer().getPluginManager()
                .registerEvents(new PlayerLocker(), this.plugin);
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
            SecurityPlugin.log(Level.WARNING, "Cannot enable password");
            this.enabled = false;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.YELLOW + "Connectez-vous avec la commande /l <mot de passe>");
        Volatile.set("lock." + event.getPlayer().getName(), event.getPlayer().getLocation().clone());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Volatile.delete("lock." + event.getPlayer().getName());
    }

    private String join(String[] s) {
        StringBuilder sb = new StringBuilder();
        for (String ss : s) {
            sb.append(ss);
        }
        return sb.toString();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] cmd = event.getMessage().split(" ", 2);
        if (cmd[0].equalsIgnoreCase("/l") || cmd[0].equalsIgnoreCase("/login")) {
            boolean rslt = false;
            String msg = "";
            if (this.plugin.mySQLClient.checkConnectionIsAlive(true)) {
                try {
                    PreparedStatement preparedQuery = this.plugin.mySQLClient.prepareStatement(this.plugin.passwordCommand);
                    preparedQuery.setString(1, event.getPlayer().getName());
                    preparedQuery.setString(2, cmd[1]);
                    ResultSet query = preparedQuery.executeQuery();
                    int i = 0;
                    while (query.next()) {
                        i++;
                    }
                    if (i == 0) {
                        rslt = true;
                        msg = "Mot de passe incorrecte";
                    } else if (i != 1) {
                        rslt = true;
                        msg = "Erreur de base de donnée, serveur inacessible. Reessayez plus tard !";
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    rslt = true;
                    msg = "Erreur de base de donnée, serveur innacessible. Reessayez plus tard !";
                }
            } else {
                rslt = true;
                msg = "Erreur de base de donnée, serveur innacessible. Reessayez plus tard !";
            }
            if (rslt) {
                event.getPlayer().sendMessage(ChatColor.RED + msg);
                event.getPlayer().kickPlayer(msg);
            } else {
                event.getPlayer().sendMessage(ChatColor.GREEN + "Vous êtes maintenant connecté !");
                Volatile.delete("lock." + event.getPlayer().getName());
            }
            event.setCancelled(true);
        }
    }
}
