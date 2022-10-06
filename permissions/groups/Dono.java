package me.ghost.permissions.groups;

import java.util.ArrayList;
import java.util.List;

public class Dono extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("*");
		return permissions;
	}

}
