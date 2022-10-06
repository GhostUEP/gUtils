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

public class Unmute implements CommandExecutor {

	private BanManagement manager;

	public Unmute(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("unmute")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(player, Group.ADMIN)) {
					sender.sendMessage(ChatColor.RED + "Sem permissão!");
					return true;
				}
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Use: /unmute [player]");
				return true;
			}

			final Player target = manager.getServer().getPlayer(args[0]);
			final String[] argss = args;
			final CommandSender senderr = sender;
			new Thread(new Runnable() {
				@Override
				public void run() {
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
					if (!manager.isMuted(uuid)) {
						senderr.sendMessage(ChatColor.RED
								+ "O player nao esta mutado");
						return;
					}
					senderr.sendMessage(ChatColor.WHITE + "O Jogador "
							+ argss[0] + " foi desmutado!");
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == senderr)
							continue;
						if (!PermissionsManager.hasGroupPermission(player,
								Group.TRIAL))
							continue;
						player.sendMessage(ChatColor.WHITE + argss[0]
								+ " foi desmutado do servidor por "
								+ senderr.getName() + "!");
					}
					if (target != null) {
						target.sendMessage(ChatColor.WHITE
								+ "Voce foi desmutado do servidor por "
								+ senderr.getName()
								+ "! Agora voce ja pode falar");
					}
					try {
						manager.unmute(uuid);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		return false;
	}
}
