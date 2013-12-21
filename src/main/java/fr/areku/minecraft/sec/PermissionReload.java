package fr.areku.minecraft.sec;

import java.util.TimerTask;

public class PermissionReload extends TimerTask {
    private SecurityPlugin plugin;

    public PermissionReload(SecurityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (SecurityPlugin.verbose)
            SecurityPlugin.log("reloading permissions");

        try {
            ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().reset();
            if (SecurityPlugin.verbose)
                SecurityPlugin.log("\t>OK");
        } catch (ru.tehkode.permissions.exceptions.PermissionBackendException e) {
            //e.printStackTrace();
        }
    }

}
