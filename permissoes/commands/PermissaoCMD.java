package me.ghost.permissoes.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.ghost.permissions.PermissionsManager;
import me.ghost.permissions.Enum.Alterar;
import me.ghost.permissions.Enum.Group;
import me.ghost.utils.UUIDFetcher;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PermissaoCMD implements CommandExecutor {
	private PermissionsManager manager;

	public PermissaoCMD(PermissionsManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (label.equalsIgnoreCase("setperm")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(p, Group.ADMIN)) {
					p.sendMessage(ChatColor.RED + "Sem permissão");
					return true;
				}
			}
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Use /setperm [nick] [add/remove] [perm]");
			} else if (args.length == 1) {
				new BukkitRunnable() {

					@Override
					public void run() {
						final Player target = manager.getServer().getPlayer(args[0]);
						UUID uuid = null;
						if (target != null) {
							uuid = target.getUniqueId();
						} else {
							try {
								uuid = UUIDFetcher.getUUIDOf(args[0]);
							} catch (Exception e) {
							}
						}
						if (uuid == null) {
							sender.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
							return;
						}
						if (PermissionsManager.playerPerms.containsKey(uuid)) {
							List<String> perms = new ArrayList<String>();
							for (String name : PermissionsManager.playerPerms.get(uuid)) {
								perms.add(name);
							}
							Collections.sort(perms, String.CASE_INSENSITIVE_ORDER);
							String list = StringUtils.join(perms, ", ");
							sender.sendMessage(ChatColor.YELLOW + "O jogador '" + args[0] + "' possui as permissões: "
									+ ChatColor.GRAY + list);
							return;
						} else {
							try {
								List<String> perms = manager.carregarPermsOff(uuid);
								Collections.sort(perms, String.CASE_INSENSITIVE_ORDER);
								String list = StringUtils.join(perms, ", ");
								sender.sendMessage(ChatColor.YELLOW + "O jogador '" + args[0]
										+ "' possui as permissões: " + ChatColor.GRAY + list);
							} catch (SQLException e) {
								e.printStackTrace();
								manager.getLogger().info("Erro ao verificar as perm");
								sender.sendMessage(ChatColor.RED + "Erro ao verificar as perm");
							}
							return;
						}

					}
				}.runTaskAsynchronously(manager.getPlugin());
			} else if (args.length == 2) {
				sender.sendMessage(ChatColor.RED + "Use /setperm [nick] [add/remove] [perm]");
			} else if (args.length == 3) {
				final String addremove = args[1];
				if (!(addremove.equalsIgnoreCase("add") || addremove.equalsIgnoreCase("remove"))) {
					sender.sendMessage(ChatColor.RED + "Use /setperm [nick] [add/remove] [perm]");
					return true;
				}
				new BukkitRunnable() {

					@Override
					public void run() {

						final Player target = manager.getServer().getPlayer(args[0]);
						UUID uuid = null;
						if (target != null) {
							uuid = target.getUniqueId();
						} else {
							try {
								uuid = UUIDFetcher.getUUIDOf(args[0]);
							} catch (Exception e) {
							}
						}
						if (uuid == null) {
							sender.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
							return;
						}
						if (addremove.equalsIgnoreCase("add")) {
							try {
								manager.alterarPermissao(uuid, args[2], Alterar.ADICIONAR);
								sender.sendMessage(ChatColor.YELLOW + "Você adicionou: '" + args[2] + "' "
										+ "ao jogador: " + args[0]);
								return;
							} catch (SQLException e) {
								e.printStackTrace();
								manager.getLogger().info("Erro ao adicionar a perm");
								sender.sendMessage(ChatColor.RED + "Erro ao adicionar a perm");
								return;
							}
						} else if (addremove.equalsIgnoreCase("remove")) {
							try {
								manager.alterarPermissao(uuid, args[2], Alterar.REMOVER);
								sender.sendMessage(ChatColor.YELLOW + "Você removeu: '" + args[2] + "' "
										+ "do jogador: " + args[0]);
								return;
							} catch (SQLException e) {
								e.printStackTrace();
								manager.getLogger().info("Erro ao remover a perm");
								sender.sendMessage(ChatColor.RED + "Erro ao remover a perm");
								return;
							}
						}
					}
				}.runTaskAsynchronously(manager.getPlugin());
			}
			return true;
		}
		return false;
	}
}
