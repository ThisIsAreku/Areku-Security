package fr.areku.minecraft.sec.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (C) Areku-Security - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alexandre, 01/01/14
 */
public class normal implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Impossible d'utiliser cette commande en tant que Console");
            return true;
        }
        Player p = (Player) commandSender;

        p.setGameMode(GameMode.SURVIVAL);


        p.sendMessage(ChatColor.BLUE + "Vous avez été réinitialisé," + ChatColor.MAGIC +" bon jeu !");
        return true;
    }
}
