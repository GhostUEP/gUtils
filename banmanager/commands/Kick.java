package me.ghost.banmanager.commands;

import me.ghost.banmanager.BanManagement;
import me.ghost.permissions.PermissionsManager;
import me.ghost.permissions.Enum.Group;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kick implements CommandExecutor {

	private BanManagement manager;

	public Kick(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kick")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(player, Group.TRIAL)) {
					sender.sendMessage(ChatColor.RED + "Sem permissão!");
					return true;
				}
			}
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Use: /kick [player] [motivo]");
				return true;
			}
	
			Player target = manager.getServer().getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "O player nao esta online");
				return true;
			}
			String kickMessage = ChatColor.WHITE + "Voce foi kickado do servidor por " + sender.getName() + "!";
			StringBuilder builder = new StringBuilder();
			boolean temmotivo = false;
			if (args.length > 1) {
				temmotivo = true;
				for (int i = 1; i < args.length; i++) {
					String espaco = " ";
					if (i >= args.length - 1)
						espaco = "";
					builder.append(args[i] + espaco);
				}
				kickMessage = kickMessage + " Motivo: " + ChatColor.AQUA + builder.toString();
			}
			if (temmotivo)
				sender.sendMessage(ChatColor.WHITE + "O Jogador " + target.getName() + " foi kickado. Motivo: " + ChatColor.RED + builder.toString());
			else
				sender.sendMessage(ChatColor.WHITE + "O Jogador " + target.getName() + " foi kickado. Sem motivo");

			for (Player player : manager.getServer().getOnlinePlayers()) {
				if (player == sender)
					continue;
				if (!PermissionsManager.hasGroupPermission(player, Group.TRIAL))
					continue;
				if (temmotivo)
					player.sendMessage(ChatColor.WHITE + target.getName() + " foi kickado do servidor por " + sender.getName() + "! Motivo: " + ChatColor.RED + builder.toString());
				else
					player.sendMessage(ChatColor.WHITE + target.getName() + " foi kickado do servidor por " + sender.getName() + "! Sem motivo");
			}
			target.kickPlayer(kickMessage);
		}
		return false;
	}
}
