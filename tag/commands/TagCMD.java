package me.ghost.tag.commands;

import me.ghost.permissions.PermissionsManager;
import me.ghost.tag.TagManager;
import me.ghost.tag.api.TagAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagCMD implements CommandExecutor {
	private TagManager manager;

	public TagCMD(TagManager manager) {
		this.manager = manager;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Voce não é um player");
		}
		final Player p = (Player) sender;
		if (label.equalsIgnoreCase("tag")) {
			if (args.length == 0) {
				p.sendMessage(" ");
				manager.sendTags(p);
				p.sendMessage(" ");
				return false;
			}
			boolean achou = false;
			for (final TagAPI tagList : manager.tags) {
				if (tagList.getRank().equalsIgnoreCase(args[0].toLowerCase())) {
					if (PermissionsManager.hasPermission(p.getUniqueId(), "tag." + tagList.getRank())) {
						achou = true;
						manager.tag.put(p.getUniqueId(), args[0]);
						for (Player on : Bukkit._INVALID_getOnlinePlayers()) {
							manager.update(on.getScoreboard());
						}
						p.setDisplayName(String.valueOf(tagList.getCor()) + tagList.getNome() + tagList.getCorNick()
								+ p.getName());
						p.sendMessage("§aVoce alterou sua tag!");
					} else {
						p.sendMessage("§cVoce nao tem permissao para a tag!");
					}
				}
			}
			if (!achou) {
				p.sendMessage("§cEssa tag nao existe!");
			}
		}
		return false;
	}
}
