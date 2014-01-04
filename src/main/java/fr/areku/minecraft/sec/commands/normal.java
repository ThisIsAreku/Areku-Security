package fr.areku.minecraft.sec.commands;

import fr.areku.minecraft.sec.SecurityPlugin;
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
public class normal extends SecurityCommand {
    public normal() {
        super("normal");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Impossible d'utiliser cette commande en tant que Console");
            return true;
        }
        Player p = (Player) commandSender;

        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().clear();


        p.sendMessage(ChatColor.BLUE + "Vous avez été réinitialisé, bon jeu !");
        return true;
    }
}
