package fr.areku.minecraft.sec;

import fr.areku.minecraft.commons.MySQLPool;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Copyright (C) Areku-Security - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alexandre, 04/01/14
 */
public class BaseSecurityClass implements Listener {
    private String name;
    private boolean enabled;

    public BaseSecurityClass(String name) {
        this.name = name;
        this.enabled = SecurityPlugin.getInstance().getConfig().getBoolean(name + ".enabled");
        Bukkit.getServer().getPluginManager().registerEvents(this, SecurityPlugin.getInstance());

        loadMysql();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        SecurityPlugin.getInstance().getConfig().set(name + ".enabled", enabled);
        try {
            SecurityPlugin.getInstance().getConfig().save(SecurityPlugin.getInstance().cfgFile);
        } catch (IOException e) {
            SecurityPlugin.log(Level.WARNING, "Cannot save " + name + " stated");
        }
    }

    public void loadMysql() {
        try {
            if (isEnabled())
                SecurityPlugin.getInstance().mySQLClient.connect();
        } catch (SQLException e) {
            SecurityPlugin.log(Level.WARNING, "Cannot enable " + name);
            setEnabled(false);
        }
    }

    public MySQLPool getMysqlClient() {
        return SecurityPlugin.getInstance().mySQLClient;
    }
}
