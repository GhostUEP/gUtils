package me.ghost.pagamento.commands;

import java.sql.SQLException;
import java.util.UUID;

import me.ghost.pagamento.BuyManager;
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

public class Givevip implements CommandExecutor {

	private BuyManager manager;

	public Givevip(BuyManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equals("givevip")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!PermissionsManager.hasGroupPermission(player, Group.ADMIN)) {
					sender.sendMessage(ChatColor.RED + "Sem permissão!");
					return true;
				}
			}
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED
						+ "Use: /givevip [player] [tempo] [grupo]");
				return true;
			}
			final String[] argss = args;
			final CommandSender senderr = sender;
			new BukkitRunnable() {
				@Override
				public void run() {
					final Player target = manager.getServer().getPlayer(
							argss[0]);
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
									+ "Jogador não existe!");
							return;
						}
					}
					if (uuid == null) {
						senderr.sendMessage(ChatColor.RED
								+ "Parece que o player nao existe!");
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
					Group grupo = null;
					try {
						grupo = Group.valueOf(argss[2].toUpperCase());
					} catch (Exception e) {
						senderr.sendMessage(ChatColor.RED
								+ "Esse grupo nao existe!");
						return;
					}
					if (grupo.ordinal() > 3 || grupo == Group.NORMAL) {
						senderr.sendMessage(ChatColor.RED
								+ "Voce nao pode usar este grupo!");
						return;
					}
					permManager.setPlayerGroup(uuid, grupo);
					try {
						permManager.savePlayerGroup(uuid, grupo);
					} catch (SQLException e1) {
						e1.printStackTrace();
						return;
					}
					try {
						manager.addExpire(uuid, grupo, expiresCheck);
					} catch (Exception e) {
						senderr.sendMessage(ChatColor.RED
								+ "Erro ao tentar adicionar VIP : "
								+ e.getCause().toString());
						return;
					}
					senderr.sendMessage(ChatColor.WHITE + "Vip setado para: "
							+ ChatColor.GREEN + argss[0] + ChatColor.WHITE
							+ " por " + tempo + ChatColor.GREEN + ""
							+ ChatColor.BOLD + " SUCESSO!");
					if (target != null) {
						target.sendMessage(ChatColor.GOLD
								+ "Seu pagamento foi aceito e seu vip "
								+ grupo.toString() + " foi setado!");
					}
				}
			}.runTaskAsynchronously(manager.getPlugin());
		}
		return false;
	}

}
