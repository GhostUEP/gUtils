package me.ghost.banmanager.commands;

import java.util.UUID;

import me.ghost.banmanager.BanManagement;
import me.ghost.permissions.PermissionsManager;
import me.ghost.permissions.Enum.Group;
import me.ghost.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Mute implements CommandExecutor {

	private BanManagement manager;

	public Mute(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("mute")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(player, Group.TRIAL)) {
					sender.sendMessage(ChatColor.RED
							+ "Sem permissão!");
					return true;
				}
			}
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED
						+ "Uso correto: /mute <player> <motivo>");
				return true;
			}
			final Player target = manager.getServer().getPlayer(args[0]);
			if (target == null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (PermissionsManager.isGroup(player, Group.TRIAL)) {
						sender.sendMessage(ChatColor.RED
								+ "Voce nao possui permissao para mutar players offline!");
						return true;
					}
				}
			}
			final String[] argss = args;
			final CommandSender senderr = sender;
			new Thread(new Runnable() {
				@Override
				public void run() {
					PermissionsManager permManager = manager.getPlugin()
							.getPermissionManager();
					UUID uuid = null;
					if (target != null) {
						uuid = target.getUniqueId();
					} else {
						try {
							uuid = UUIDFetcher.getUUIDOf(argss[0]);
						} catch (Exception e) {
							senderr.sendMessage(ChatColor.RED
									+ "O player nao existe");
							return;
						}
					}
					if (uuid == null) {
						senderr.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
						return;
					}
					try {
						manager.loadBanAndMute(uuid);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (manager.isMuted(uuid)) {
						senderr.sendMessage(ChatColor.RED
								+ "O player ja esta mutado");
						return;
					}
					if (permManager.getPlayerGroup(uuid).ordinal() >= 5
							&& senderr instanceof Player
							&& permManager.getPlayerGroup((Player) senderr) != Group.DONO
							&& permManager.getPlayerGroup((Player) senderr) != Group.ADMIN) {
						senderr.sendMessage(ChatColor.RED
								+ "Voce nao pode mutar alguém da staff");
						return;
					}
					StringBuilder builder = new StringBuilder();
					for (int i = 1; i < argss.length; i++) {
						String espaco = " ";
						if (i >= argss.length - 1)
							espaco = "";
						builder.append(argss[i] + espaco);
					}
					senderr.sendMessage(ChatColor.WHITE + "O Jogador "
							+ argss[0] + " foi mutado. Motivo: "
							+ ChatColor.RED + builder.toString());
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == senderr)
							continue;
						if (!PermissionsManager.hasGroupPermission(player,
								Group.TRIAL))
							continue;
						player.sendMessage(ChatColor.WHITE + argss[0]
								+ " foi mutado por " + senderr.getName()
								+ "! Motivo: " + ChatColor.RED
								+ builder.toString());
					}
					if (target != null) {
						target.sendMessage(ChatColor.WHITE
								+ "Voce foi mutado por " + senderr.getName()
								+ "! Motivo: " + ChatColor.RED
								+ builder.toString());
					}
					try {
						manager.mute(new me.ghost.banmanager.constructor.Mute(
								uuid, senderr.getName(), builder.toString(),
								System.currentTimeMillis(), 0));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		return false;
	}
}
