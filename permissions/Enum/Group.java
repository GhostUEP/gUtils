package me.ghost.permissions.Enum;

import me.ghost.permissions.groups.Admin;
import me.ghost.permissions.groups.Dono;
import me.ghost.permissions.groups.MainGroup;
import me.ghost.permissions.groups.Mvp;
import me.ghost.permissions.groups.Normal;
import me.ghost.permissions.groups.Pro;
import me.ghost.permissions.groups.Trial;
import me.ghost.permissions.groups.Yt;
import me.ghost.permissions.groups.Ytplus;

public enum Group {
	NORMAL(new Normal()),//
	MVP(new Mvp()),//
	PRO(new Pro()),//
	YT(new Yt()),//
	YTPLUS(new Ytplus()),//
	TRIAL(new Trial()), //
	MOD(new Trial()), //
	ADMIN(new Admin()), //
	DONO(new Dono());

	private MainGroup group;

	private Group(MainGroup group) {
		this.group = group;
	}

	public MainGroup getGroup() {
		return group;
	}
}
