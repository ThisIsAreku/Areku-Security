package fr.areku.minecraft.sec;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.*;

/**
 * Copyright (C) Areku-Security - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alexandre, 20/12/13
 */
public class PlayerLocker implements Listener {

    public PlayerLocker() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SecurityPlugin.getInstance());
    }

    private void restrictEvent(Event event) {
        Player p;
        if (event instanceof PlayerEvent)
            p = ((PlayerEvent) event).getPlayer();
        else if (event instanceof BlockPlaceEvent)
            p = ((BlockPlaceEvent) event).getPlayer();
        else if (event instanceof BlockBreakEvent)
            p = ((BlockBreakEvent) event).getPlayer();
        else
            return;
        String pname = p.getName();
        if (Volatile.contains("lock." + pname)) {
            if (event instanceof PlayerMoveEvent) {
                Location l = (Location) Volatile.get("lock." + pname);
                ((PlayerMoveEvent) event).setFrom(l);
                ((PlayerMoveEvent) event).setTo(l);
            } else {
                if (event instanceof Cancellable)
                    ((Cancellable) event).setCancelled(true);
            }
            if (!Volatile.contains("lastmsgtime." + pname)) {
            } else if (System.currentTimeMillis() - (long) Volatile.get("lastmsgtime." + pname) > 5000) {
                sendTimedMessage(p, ChatColor.YELLOW + "Utilisez la commande /l <mot de passe> pour vous connecter");
            }
        }
    }

    private void sendTimedMessage(Player p, String message) {
        Volatile.set("lastmsgtime." + p.getName(), System.currentTimeMillis());
        p.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        restrictEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        restrictEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        //event.setCancelled(Volatile.contains("lock." + event.getPlayer().getName()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        restrictEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        restrictEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        restrictEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        restrictEvent(event);
    }


    // just cancel theses events
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player)
            event.setCancelled(Volatile.contains("lock." + ((Player) event.getTarget()).getName()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            event.setCancelled(Volatile.contains("lock." + ((Player) event.getEntity()).getName()));
    }
}
