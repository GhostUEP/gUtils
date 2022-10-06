package me.ghost.permissoes.commands;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.ghost.permissions.PermissionsManager;
import me.ghost.permissions.Enum.Alterar;
import me.ghost.permissions.Enum.Group;

public class GrouppermCMD implements CommandExecutor {
	private PermissionsManager manager;

	public GrouppermCMD(PermissionsManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (label.equalsIgnoreCase("groupperm")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(p, Group.ADMIN)) {
					p.sendMessage(ChatColor.RED + "Sem permissão");
					return true;
				}
			}
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Use /groupperm [grupo] [add/remove] [perm]");
			} else if (args.length == 1) {
				new BukkitRunnable() {

					@Override
					public void run() {
						Group groupo = null;
						try {
							groupo = Group.valueOf(args[0].toUpperCase());
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Esse grupo nao existe");
							return;
						}
						try {
							List<String> perms = manager.carregarPermsGrupo(groupo.toString().toLowerCase());
							Collections.sort(perms, String.CASE_INSENSITIVE_ORDER);
							String list = StringUtils.join(perms, ", ");
							sender.sendMessage(ChatColor.YELLOW + "O grupo '" + args[0].toUpperCase()
									+ "' possui as permissões: " + ChatColor.GRAY + list);
						} catch (SQLException e) {
							e.printStackTrace();
							manager.getLogger().info("Erro ao verificar as perm");
							sender.sendMessage(ChatColor.RED + "Erro ao verificar as perm");
						}
						return;

					}
				}.runTaskAsynchronously(manager.getPlugin());
			} else if (args.length == 2) {
				sender.sendMessage(ChatColor.RED + "Use /groupperm [grupo] [add/remove] [perm]");
			} else if (args.length == 3) {
				final String addremove = args[1];
				if (!(addremove.equalsIgnoreCase("add") || addremove.equalsIgnoreCase("remove"))) {
					sender.sendMessage(ChatColor.RED + "Use /groupperm [grupo] [add/remove] [perm]");
					return true;
				}
				new BukkitRunnable() {

					@Override
					public void run() {

						Group groupo = null;
						try {
							groupo = Group.valueOf(args[0].toUpperCase());
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Esse grupo nao existe");
							return;
						}
						if (addremove.equalsIgnoreCase("add")) {
							try {
								manager.alterarPermissaoGrupo(groupo.toString().toLowerCase(), args[2], Alterar.ADICIONAR);
								sender.sendMessage(ChatColor.YELLOW + "Você adicionou: '" + args[2] + "' "
										+ "ao grupo: " + args[0].toUpperCase());
								return;
							} catch (SQLException e) {
								e.printStackTrace();
								manager.getLogger().info("Erro ao adicionar a perm");
								sender.sendMessage(ChatColor.RED + "Erro ao adicionar a perm");
								return;
							}
						} else if (addremove.equalsIgnoreCase("remove")) {
							try {
								manager.alterarPermissaoGrupo(groupo.toString().toLowerCase(), args[2], Alterar.REMOVER);
								sender.sendMessage(ChatColor.YELLOW + "Você removeu: '" + args[2] + "' "
										+ "do grupo: " + args[0].toUpperCase());
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
