package me.ghost.banmanager.constructor;

import java.util.UUID;

public class Mute {
	private UUID mutedUuid;
	private String mutedBy;
	private String reason;
	private long muteTime;
	private long duration;

	public Mute(UUID mutedUuid, String mutedBy, String reason, long muteTime, long duration) {
		this.mutedUuid = mutedUuid;
		this.mutedBy = mutedBy;
		this.reason = reason;
		this.muteTime = muteTime;
		this.duration = duration;
	}

	public UUID getMutedUuid() {
		return mutedUuid;
	}

	public String getMutedBy() {
		return mutedBy;
	}

	public String getReason() {
		return reason;
	}

	public long getMuteTime() {
		return muteTime;
	}

	public long getDuration() {
		return duration;
	}

	public boolean isPermanent() {
		return getDuration() == 0;
	}

	public boolean hasExpired() {
		return !isPermanent() && duration < System.currentTimeMillis();
	}
}
