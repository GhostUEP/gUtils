package me.ghost.permissions.listeners;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import me.ghost.permissions.PermissionsManager;

public class PlayerListener implements Listener {
	private PermissionsManager pmanager;

	public PlayerListener(PermissionsManager pmanager) {
		this.pmanager = pmanager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsync(AsyncPlayerPreLoginEvent event) {
		try {
			pmanager.carregarPerms(event.getUniqueId());
		} catch (SQLException e) {
			e.printStackTrace();
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
					ChatColor.RED + "Nao foi possivel carregar seus dados");
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMonitorLogin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED) {
			pmanager.removePlayerGroup(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (e.getMessage().contains("%")) {
			e.setCancelled(true);
			return;
		}
		if (p.getName().length() < 50) {
			e.setFormat(p.getDisplayName() + ChatColor.RESET + ChatColor.WHITE + ": " + e.getMessage());

		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (PermissionsManager.playerPerms.containsKey(p.getUniqueId())) {
			PermissionsManager.playerPerms.remove(p.getUniqueId());
		}
	}

}
