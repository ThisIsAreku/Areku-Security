package fr.areku.minecraft.sec;

import java.util.TimerTask;

import org.bukkit.command.CommandException;

//import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionReload extends TimerTask {
	//private SecurityPlugin plugin;

	public PermissionReload(SecurityPlugin plugin) {
		//this.plugin = plugin;
	}

	@Override
	public void run() {

		try {
			if (SecurityPlugin.verbose)
				SecurityPlugin.log("reloading permissions");
			
			//PermissionsEx.getPermissionManager().reset();
			if (SecurityPlugin.verbose)
					SecurityPlugin.log("\t>OK");
		} catch (CommandException ce) {
			SecurityPlugin.logException(ce, "Permissions reload error");
		}
	}

}
