package me.ghost.pagamento.constructor;

import java.util.UUID;

import me.ghost.permissions.Enum.Group;

public class Expire {
	private UUID uuid;
	private long expire;
	private Group group;

	public Expire(UUID uuid, long expire, Group group) {
		this.uuid = uuid;
		this.expire = expire;
		this.group = group;
	}

	public UUID getUuid() {
		return uuid;
	}

	public long getExpire() {
		return expire;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void addLong(long l) {
		this.expire = l;
	}

}
