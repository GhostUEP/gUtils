package me.ghost.tag.listeners;

import me.ghost.tag.TagManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {
	private TagManager manager;

	public JoinListener(TagManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntrar(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		manager.setMaxTag(p);
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				for (Player on : Bukkit._INVALID_getOnlinePlayers()) {
					manager.update(on.getScoreboard());
				}
			}
		}.runTaskTimer(manager.getPlugin(), 0L, 20L);
	}

}
