package fr.areku.minecraft.sec;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Copyright (C) Areku-Security - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alexandre, 20/12/13
 */
public class Password extends BaseSecurityClass {

    public Password() {
        super("password");
        registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isEnabled())
            return;
        event.setJoinMessage(ChatColor.YELLOW + "[" + ChatColor.DARK_GREEN + "x" + ChatColor.YELLOW + "] " + event.getPlayer().getName());
        event.getPlayer().sendMessage(ChatColor.YELLOW + "Connectez-vous avec la commande /l <mot de passe>");
        Volatile.set("lock." + event.getPlayer().getName(), event.getPlayer().getLocation().clone());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!isEnabled())
            return;
        event.setQuitMessage(ChatColor.YELLOW + "[" + ChatColor.RED + "-" + ChatColor.YELLOW + "] " + event.getPlayer().getName());
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
        if (!isEnabled())
            return;
        if (!(event.getMessage().startsWith("/l") || event.getMessage().startsWith("/login")))
            return;
        String[] cmd = event.getMessage().split(" ", 2);
        if (!(cmd[0].equalsIgnoreCase("/l") || cmd[0].equalsIgnoreCase("/login")))
            return;

        if (cmd.length != 2) {
            event.getPlayer().sendMessage(ChatColor.RED + "Usage: /l <mot de passe>");
            event.setCancelled(true);
            return;
        }

        event.getPlayer().sendMessage(ChatColor.BLUE + "Vérification en cours...");
        loadMysql();
        boolean rslt = false;
        String msg = "";
        if (getMysqlClient().checkConnectionIsAlive(true)) {
            try {
                PreparedStatement preparedQuery = getMysqlClient().prepareStatement(SecurityPlugin.getInstance().passwordCommand);
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
            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GREEN + "+" + ChatColor.YELLOW + "] " + event.getPlayer().getName());
            Volatile.delete("lock." + event.getPlayer().getName());
            SecurityPlugin.getInstance().getLogger().info(event.getPlayer().getName() + " : logged in successfully");
        }
        event.setCancelled(true);
    }
}
