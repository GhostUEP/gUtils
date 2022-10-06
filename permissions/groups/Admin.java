package me.ghost.permissions.groups;

import java.util.ArrayList;
import java.util.List;

public class Admin extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("minecraft.command.tp");
		permissions.add("minecraft.command.stop");
		permissions.add("bukkit.command.teleport");
		permissions.add("bukkit.command.tps");
		return permissions;
	}

}
