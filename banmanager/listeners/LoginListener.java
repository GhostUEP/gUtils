package me.ghost.banmanager.listeners;

import java.util.UUID;

import me.ghost.banmanager.BanManagement;
import me.ghost.banmanager.constructor.Ban;
import me.ghost.banmanager.constructor.Mute;
import me.ghost.utils.DateUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class LoginListener implements Listener {
	private BanManagement manager;

	public LoginListener(BanManagement manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		if (!manager.isMuted(p))
			return;
		Mute mute = manager.getMute(p);
		if (mute.hasExpired()) {
			try {
				manager.unmute(mute.getMutedUuid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		if (mute.isPermanent()) {
			p.sendMessage(ChatColor.WHITE + "Voce foi mutado permanentemente por " + mute.getMutedBy() + "! Motivo: " + ChatColor.RED + mute.getReason());
		} else {
			String tempo = DateUtils.formatDifference((mute.getDuration() - System.currentTimeMillis()) / 1000);
			p.sendMessage(ChatColor.WHITE + "Voce foi mutado por " + tempo + " pelo player " + mute.getMutedBy() + "! Motivo: " + ChatColor.RED + mute.getReason());
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsync(AsyncPlayerPreLoginEvent event) {
		try {
			manager.loadBanAndMute(event.getUniqueId());
		} catch (Exception e) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "Nao foi possivel carregar banimento, tente novamente em breve");
		}
		
		final UUID uuid = event.getUniqueId();
		if (event.getLoginResult() != Result.ALLOWED)
			return;
		Ban ban;
		try {
			if (!manager.isBanned(uuid))
				return;
			ban = manager.getBan(uuid);
		} catch (Exception e1) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "Nao foi possivel carregar banimento, tente novamente em breve");
			return;
		}
		if (ban.isUnbanned())
			return;
		if (ban.hasExpired()) {
			try {
				manager.removeTempban(ban.getBannedUuid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		event.disallow(Result.KICK_BANNED, BanManagement.getBanMessage(ban));
	}

}
