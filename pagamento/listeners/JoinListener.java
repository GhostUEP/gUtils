package me.ghost.pagamento.listeners;

import java.sql.SQLException;
import java.util.UUID;

import me.ghost.main.Main;
import me.ghost.pagamento.BuyManager;
import me.ghost.pagamento.constructor.Expire;
import me.ghost.permissions.Enum.Group;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {
	private BuyManager manager;

	public JoinListener(BuyManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsync(AsyncPlayerPreLoginEvent event) {
		try {
			manager.loadExpire(event.getUniqueId());
		} catch (Exception e) {
			event.disallow(Result.KICK_OTHER,
					ChatColor.RED + "Nao foi possivel reconhecer seus status de compras, tente novamente");
		}
		final UUID uuid = event.getUniqueId();
		if (!manager.expires.containsKey(uuid))
			return;
		final Expire expire = manager.getExpire(uuid);
		if (expire == null)
			return;
		if (expire.getExpire() < System.currentTimeMillis()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					manager.getPlugin().getPermissionManager().setPlayerGroup(expire.getUuid(), Group.NORMAL);
					try {
						manager.getPlugin().getPermissionManager().removePlayer(expire.getUuid());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						manager.removeExpire(expire.getUuid());
					} catch (Exception e) {
						e.printStackTrace();
					}
					manager.expires.remove(uuid);

					new BukkitRunnable() {
						@Override
						public void run() {
							Player target = Bukkit.getPlayer(uuid);
							if (target != null) {
								target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "SEU VIP EXPIROU!"
										+ ChatColor.RESET + ChatColor.GRAY + " Para comprar novamente entre no site: "
										+ ChatColor.GOLD + Main.plugin.site);

							}
						}
					}.runTaskLater(manager.getPlugin(), 5);
				}
			}.runTaskLaterAsynchronously(manager.getPlugin(), 40);
		}
	}

}
