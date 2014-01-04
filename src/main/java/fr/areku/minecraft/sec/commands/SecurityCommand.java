package fr.areku.minecraft.sec.commands;

import fr.areku.minecraft.sec.SecurityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

/**
 * Copyright (C) Areku-Security - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alexandre, 04/01/14
 */
public abstract class SecurityCommand implements CommandExecutor {
    protected SecurityCommand(String command) {
        SecurityPlugin.getInstance().getCommand(command).setExecutor(this);
    }
}
