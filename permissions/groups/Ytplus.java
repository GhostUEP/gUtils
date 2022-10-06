package me.ghost.permissions.groups;

import java.util.ArrayList;
import java.util.List;

public class Ytplus extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("minecraft.command.tp");
		permissions.add("bukkit.command.teleport");
		return permissions;
	}

}
