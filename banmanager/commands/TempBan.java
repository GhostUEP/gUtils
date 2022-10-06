package me.ghost.banmanager.commands;

import java.util.UUID;

import me.ghost.banmanager.BanManagement;
import me.ghost.permissions.PermissionsManager;
import me.ghost.permissions.Enum.Group;
import me.ghost.utils.DateUtils;
import me.ghost.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TempBan implements CommandExecutor {

	private BanManagement manager;

	public TempBan(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("tempban")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(player, Group.TRIAL)) {
					sender.sendMessage(ChatColor.RED + "Sem permissão!");
					return true;
				}
			}
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED
						+ "Use: /tempban [player] [tempo] [motivo]");
				return true;
			}

			final Player target = manager.getServer().getPlayer(args[0]);
			if (target == null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (PermissionsManager.isGroup(player, Group.TRIAL)) {
						sender.sendMessage(ChatColor.RED
								+ "Voce nao possui permissao para banir players offline!");
						return true;
					}
				}
			}
			final String[] argss = args;
			final CommandSender senderr = sender;
			new BukkitRunnable() {
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
						senderr.sendMessage(ChatColor.RED
								+ "Parece que o player nao existe!");
						return;
					}
					try {
						if (manager.isBanned(uuid)) {
							if (!manager.getBan(uuid).isUnbanned()) {
								senderr.sendMessage(ChatColor.RED
										+ "O player ja esta banido");
								return;
							}
						}
					} catch (Exception e2) {
						senderr.sendMessage(ChatColor.RED
								+ "Erro ao conectar ao banco de dados");
						return;
					}
					if (permManager.getPlayerGroup(uuid).ordinal() >= 5
							&& senderr instanceof Player
							&& permManager.getPlayerGroup((Player) senderr) != Group.DONO
							&& permManager.getPlayerGroup((Player) senderr) != Group.ADMIN) {
						senderr.sendMessage(ChatColor.RED
								+ "Voce nao pode banir alguém da staff");
						return;
					}
					long expiresCheck;
					try {
						expiresCheck = DateUtils.parseDateDiff(argss[1], true);
					} catch (Exception e1) {
						senderr.sendMessage("Formato invalido");
						return;
					}
					String tempo = DateUtils
							.formatDifference((expiresCheck - System
									.currentTimeMillis()) / 1000);
					StringBuilder builder = new StringBuilder();
					for (int i = 2; i < argss.length; i++) {
						String espaco = " ";
						if (i >= argss.length - 1)
							espaco = "";
						builder.append(argss[i] + espaco);
					}
					senderr.sendMessage(ChatColor.WHITE + "O Jogador "
							+ argss[0] + " foi temporariamente banido por "
							+ tempo + ". Motivo: " + ChatColor.RED
							+ builder.toString());
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == senderr)
							continue;
						if (!PermissionsManager.hasGroupPermission(player,
								Group.TRIAL))
							continue;
						player.sendMessage(ChatColor.WHITE
								+ argss[0]
								+ " foi temporariamente banido do servidor por "
								+ senderr.getName() + ". Tempo Restante "
								+ tempo + "! Motivo: " + ChatColor.RED
								+ builder.toString());
					}
					if (target != null) {
						String kickMessage = ChatColor.WHITE
								+ "Voce foi temporariamente banido do servidor por "
								+ senderr.getName() + ".\nTempo Restante "
								+ tempo + "!\nMotivo: " + ChatColor.RED
								+ builder.toString();
						kickPlayer(target, kickMessage);
					}
					try {
						manager.ban(new me.ghost.banmanager.constructor.Ban(
								uuid, senderr.getName(), builder.toString(),
								System.currentTimeMillis(), expiresCheck, false));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(manager.getPlugin());
		}
		return false;
	}

	public void kickPlayer(final Player player, final String message) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.kickPlayer(message);
			}
		}.runTask(manager.getPlugin());
	}
}
