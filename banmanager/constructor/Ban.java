package me.ghost.banmanager.constructor;

import java.util.Calendar;
import java.util.UUID;

public class Ban {
	private UUID bannedUuid;
	private String reason;
	private String bannedBy;
	private long banTime;
	private long duration;
	private boolean unbanned;
	private int previousBans;

	public Ban(UUID bannedUuid, String bannedBy, String reason, long banTime, long duration, boolean unbanned) {
		this.bannedUuid = bannedUuid;
		this.bannedBy = bannedBy;
		this.reason = reason;
		this.banTime = banTime;
		this.duration = duration;
		this.unbanned = unbanned;
		this.previousBans = 0;
		if (unbanned)
			previousBans++;
	}

	public UUID getBannedUuid() {
		return bannedUuid;
	}

	public String getBannedBy() {
		return bannedBy;
	}

	public String getReason() {
		return reason;
	}

	public long getBanTime() {
		return banTime;
	}

	public long getDuration() {
		return duration;
	}

	public int getPreviousBans() {
		return previousBans;
	}

	public boolean isUnbanned() {
		return unbanned;
	}

	public void unban() {
		unbanned = true;
		previousBans++;
	}

	public void setNewBan(String bannedBy, String reason, long banTime, long duration, boolean unbanned) {
		this.bannedBy = bannedBy;
		this.reason = reason;
		this.banTime = banTime;
		this.duration = duration;
		this.unbanned = unbanned;
		if (unbanned)
			previousBans++;
	}

	public boolean isPermanent() {
		return getDuration() == 0;
	}

	public boolean hasExpired() {
		return !isPermanent() && duration < Calendar.getInstance().getTimeInMillis();
	}
}
