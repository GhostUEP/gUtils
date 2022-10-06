package me.ghost.permissoes.commands;

import java.sql.SQLException;
import java.util.UUID;

import me.ghost.permissions.PermissionsManager;
import me.ghost.permissions.Enum.Group;
import me.ghost.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GroupsetUUIDCMD implements CommandExecutor {
	private PermissionsManager manager;

	public GroupsetUUIDCMD(PermissionsManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("groupsetuuid")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(player, Group.ADMIN)) {
					player.sendMessage(ChatColor.RED + "Sem permissão");
					return true;
				}
			}
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Use: /groupset [uuid] [grupo]");
				return true;
			}
			Group groupo = null;
			try {
				groupo = Group.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Esse grupo nao existe");
				return true;
			}
			final Group group = groupo;
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (manager.getPlayerGroup(player) == Group.ADMIN) {
					if (group == Group.ADMIN || group == Group.DONO) {
						player.sendMessage(ChatColor.RED + "Você não pode alterar o grupo desse player");
						return true;
					}
				}
			}
			final String[] argss = args;
			final CommandSender senderr = sender;
			if (group == Group.NORMAL) {
				new BukkitRunnable() {
					@Override
					public void run() {
						UUID uuid = null;
						try {
							uuid = UUIDFetcher.getUUID(argss[0].replace("-", ""));
						} catch (Exception e) {
						}
						if (uuid == null) {
							senderr.sendMessage(ChatColor.RED + "UUID não existe!");
							return;
						}
						if (senderr instanceof Player) {
							Player player = (Player) senderr;
							if (manager.getPlayerGroup(uuid) == Group.DONO
									&& manager.getPlayerGroup(player) == Group.ADMIN) {
								senderr.sendMessage(ChatColor.RED + "Você não pode alterar o grupo de um dono");
								return;
							}
						}
						if (group == manager.getPlayerGroup(uuid)) {
							senderr.sendMessage(ChatColor.RED + "O jogador já esta no grupo " + group.toString());
							return;
						}
						manager.removePlayerGroup(uuid);
						try {
							manager.removePlayer(uuid);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						senderr.sendMessage(ChatColor.WHITE + argss[0] + " setado como: " + group.toString()
								+ ChatColor.GREEN + "" + ChatColor.BOLD + " SUCESSO!");
					}
				}.runTaskAsynchronously(manager.getPlugin());
				return true;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					UUID uuid = null;
					try {
						uuid = UUIDFetcher.getUUID(argss[0].replace("-", ""));
					} catch (Exception e) {
					}
					if (uuid == null) {
						senderr.sendMessage(ChatColor.RED + "UUID não existe!");
						return;
					}
					if (senderr instanceof Player) {
						Player player = (Player) senderr;
						if (manager.getPlayerGroup(uuid) == Group.DONO
								&& manager.getPlayerGroup(player) == Group.ADMIN) {
							senderr.sendMessage(ChatColor.RED + "Você não pode alterar o grupo de um dono");
							return;
						}
					}
					if (group == manager.getPlayerGroup(uuid)) {
						senderr.sendMessage(ChatColor.RED + "O jogador já esta no grupo " + group.toString());
						return;
					}
					manager.setPlayerGroup(uuid, group);
					try {
						manager.savePlayerGroup(uuid, group);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					senderr.sendMessage(ChatColor.WHITE + argss[0] + " setado como: " + group.toString()
							+ ChatColor.GREEN + "" + ChatColor.BOLD + " SUCESSO!");
				}
			}.runTaskAsynchronously(manager.getPlugin());
			return true;
		}
		return false;
	}
}
