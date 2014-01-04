package fr.areku.minecraft.sec.commands;

import fr.areku.minecraft.sec.Volatile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (C) Areku-Security - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alexandre, 04/01/14
 */
public class serverLock extends SecurityCommand {
    public serverLock() {
        super("server-lock");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Impossible d'utiliser cette commande en tant que Console");
            return true;
        }
        if (!(commandSender.isOp())) {
            return false;
        }

        if (strings.length == 0 || (strings.length > 0 && strings[0].equalsIgnoreCase("on"))) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Server is "+ChatColor.BOLD+"locked");
            Volatile.set("server-lock", true);
        } else if (strings[0].equalsIgnoreCase("off")) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Server is "+ChatColor.BOLD+"unlocked");
            Volatile.delete("server-lock");
        }

        return false;
    }
}
