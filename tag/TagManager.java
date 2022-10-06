package me.ghost.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import me.ghost.main.Main;
import me.ghost.main.Management;
import me.ghost.permissions.PermissionsManager;
import me.ghost.tag.api.TagAPI;
import me.ghost.tag.commands.TagCMD;
import me.ghost.tag.listeners.JoinListener;
import net.minecraft.server.v1_7_R4.ChatSerializer;

public class TagManager extends Management {
	public HashMap<UUID, String> tag = new HashMap<>();
	public ArrayList<TagAPI> tags = new ArrayList<>();

	public TagManager(Main main) {
		super(main);
	}

	public void addTags() {
		tags.add(new TagAPI("", "§4§l", "§4§lDONO§4 ", "dono", "a", 10));
		tags.add(new TagAPI("", "§c§l", "§c§lADMIN§c ", "admin", "b", 9));
		tags.add(new TagAPI("", "§5§l", "§5§lMOD§5 ", "mod", "c", 8));
		tags.add(new TagAPI("", "§e§l", "§5§lTRIAL§5 ", "trial", "d", 7));
		tags.add(new TagAPI("", "§3§l", "§3§lYT+§3 ", "ytplus", "e", 6));
		tags.add(new TagAPI("", "§b§l", "§b§lYT§b ", "yt", "h", 5));
		tags.add(new TagAPI("", "§6§l", "§6§lPRO§6 ", "pro", "j", 4));
		tags.add(new TagAPI("", "§9§l", "§9§lMVP§9 ", "mvp", "k", 3));
		tags.add(new TagAPI("", "§7", "§7", "normal", "m", 2));
		tags.add(new TagAPI("", "§f", "§f", "", "n", 1));
	}

	public void clearTags() {
		tags.clear();
	}

	@Override
	public void onEnable() {
		addTags();
		getPlugin().getCommand("tag").setExecutor(new TagCMD(this));
		getServer().getPluginManager().registerEvents(new JoinListener(this), getPlugin());
	}

	@Override
	public void onDisable() {
		clearTags();
	}

	@SuppressWarnings("deprecation")
	public void update(final Scoreboard score) {
		for (Player p : Bukkit._INVALID_getOnlinePlayers()) {
			String group2;
			if (tag.get(p.getUniqueId()) == null) {
				group2 = "normal";
			} else {
				group2 = tag.get(p.getUniqueId());
			}
			for (final TagAPI tags2 : tags) {
				if (group2.equalsIgnoreCase(tags2.getRank())) {
					group2 = tags2.getTab();
				}
			}
			checkTeams(score);
			if (score.getTeam(group2) != null && !score.getTeam(group2).hasPlayer((OfflinePlayer) p)) {
				score.getTeam(group2).addPlayer((OfflinePlayer) p);
			}
		}
	}

	public void sendTags(final Player p) {
		final StringBuilder sb = new StringBuilder();
		sb.append("[\"\",{\"text\":\"Suas tags são: \",\"color\":\"aqua\"}");
		for (final TagAPI tags2 : tags) {
			if (PermissionsManager.hasPermission(p.getUniqueId(), "tag." + tags2.getRank())) {
				sb.append(",{\"text\":\"" + tags2.getRank().toUpperCase()
						+ "\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tag "
						+ tags2.getRank() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Exemplo: "
						+ tags2.getCorNick() + p.getName() + "\"}}");
				sb.append(",{\"text\":\" | \",\"color\":\"red\"}");
			}
		}
		sb.deleteCharAt(sb.toString().lastIndexOf(" | "));
		sb.append("]");
		((CraftPlayer) p).getHandle().sendMessage(ChatSerializer.a(sb.toString()));
	}

	public void checkTeams(final Scoreboard sb) {
		try {
			for (final TagAPI tags2 : tags) {
				if (sb.getTeam(tags2.getTab()) == null) {
					sb.registerNewTeam(tags2.getTab());
					sb.getTeam(tags2.getTab()).setPrefix(tags2.getCorNick());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String Group2Prefix(final String grupo) {
		for (final TagAPI tags2 : tags) {
			if (tags2.getRank().equalsIgnoreCase(grupo)) {
				return String.valueOf(tags2.getCor()) + tags2.getNome();
			}
		}
		return "§7";
	}

	public void setMaxTag(final Player p) {
		boolean setou = false;
		for (final TagAPI tags2 : tags) {
			if (PermissionsManager.hasPermission(p.getUniqueId(), "tag." + tags2.getRank())) {
				tag.put(p.getUniqueId(), tags2.getRank());
				p.setDisplayName(String.valueOf(tags2.getCor()) + tags2.getNome() + tags2.getCorNick() + p.getName());
				setou = true;
				break;
			}
		}
		if (!setou) {
			tag.put(p.getUniqueId(), "normal");
		}
	}

	public String Prefix2Group(final String grupo) {
		for (final TagAPI tags2 : tags) {
			if ((String.valueOf(tags2.getCor()) + tags2.getNome()).equalsIgnoreCase(grupo)) {
				return tags2.getRank();
			}
		}
		return "default";
	}

	public TagAPI getMaxTag(final Player p) {
		final boolean setou = false;
		for (final TagAPI tags2 : tags) {
			if (PermissionsManager.hasPermission(p.getUniqueId(), "tag." + tags2.getRank())) {
				return tags2;
			}
		}
		if (!setou) {
			return tags.get(15);
		}
		return null;
	}

	public TagAPI Prefix2Tag(final String grupo) {
		for (final TagAPI tags2 : tags) {
			if ((String.valueOf(tags2.getCor()) + tags2.getRank()).equalsIgnoreCase(grupo)) {
				return tags2;
			}
		}
		return null;
	}
}
