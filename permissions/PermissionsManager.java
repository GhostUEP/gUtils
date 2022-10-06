package me.ghost.permissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.ghost.main.Main;
import me.ghost.main.Management;
import me.ghost.mysql.Connect;
import me.ghost.permissions.Enum.Alterar;
import me.ghost.permissions.Enum.Group;
import me.ghost.permissions.listeners.PlayerListener;
import me.ghost.permissoes.commands.GrouppermCMD;
import me.ghost.permissoes.commands.GroupsetCMD;
import me.ghost.permissoes.commands.GroupsetUUIDCMD;
import me.ghost.permissoes.commands.PermissaoCMD;
import me.ghost.permissoes.commands.PermissaoUUIDCMD;

public class PermissionsManager extends Management {
	public static HashMap<UUID, ArrayList<String>> playerPerms = new HashMap<>();
	public static HashMap<UUID, ArrayList<String>> groupPerms = new HashMap<>();
	private static HashMap<UUID, Group> playerGroups;

	public PermissionsManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		playerGroups = new HashMap<>();
		getServer().getPluginManager().registerEvents(new PlayerListener(this), getPlugin());
		getPlugin().getCommand("setperm").setExecutor(new PermissaoCMD(this));
		getPlugin().getCommand("groupset").setExecutor(new GroupsetCMD(this));
		getPlugin().getCommand("groupsetuuid").setExecutor(new GroupsetUUIDCMD(this));
		getPlugin().getCommand("groupperm").setExecutor(new GrouppermCMD(this));
		getPlugin().getCommand("setpermuuid").setExecutor(new PermissaoUUIDCMD(this));
		try {
			Connect.lock.lock();
			PreparedStatement stmt = null;
			ResultSet result = null;
			for (Player p : getServer().getOnlinePlayers()) {
				UUID uuid = p.getUniqueId();
				stmt = getMySQL().prepareStatement(
						"SELECT * FROM `playergroups` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
				result = stmt.executeQuery();
				if (result.next()) {
					Group grupo = Group.valueOf(result.getString("groups").toUpperCase());
					setPlayerGroup(uuid, grupo);
					result.close();
					stmt.close();
				}
			}
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Connect.lock.unlock();
		}
	}

	@Override
	public void onDisable() {

	}

	public void carregarPerms(UUID uuid) throws SQLException {
		Connect.lock.lock();
		PreparedStatement sql = getMySQL().prepareStatement(
				"SELECT * FROM `permissoes` WHERE (`uuid`='" + uuid.toString().replace("-", "") + "');");
		ResultSet result = sql.executeQuery();
		while (result.next()) {
			String kit = result.getString("permissoes");
			addPlayerPerms(uuid, kit);
		}
		result.close();
		sql.close();
		sql = getMySQL().prepareStatement(
				"SELECT * FROM `playergroups` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
		result = sql.executeQuery();
		if (result.next()) {
			Group grupo = Group.valueOf(result.getString("groups").toUpperCase());
			setPlayerGroup(uuid, grupo);
			addPlayerPerms(uuid, "tag." + grupo.toString().toLowerCase());
			addPlayerPerms(uuid, "tag.normal");
			result.close();
			sql.close();
		} else {
			Group grupo = Group.NORMAL;
			setPlayerGroup(uuid, grupo);
			addPlayerPerms(uuid, "tag.normal");
		}
		result.close();
		sql.close();
		if (playerGroups.containsKey(uuid)) {
			if (playerGroups.get(uuid) != Group.NORMAL) {
				sql = getMySQL().prepareStatement("SELECT * FROM `groupperms` WHERE (`grupo`='"
						+ playerGroups.get(uuid).toString().toLowerCase() + "');");
				result = sql.executeQuery();
				while (result.next()) {
					String kit = result.getString("permissoes");
					addPlayerPerms(uuid, kit);
				}

			} else {
				result.close();
				sql.close();
				sql = getMySQL().prepareStatement("SELECT * FROM `groupperms` WHERE (`grupo`='normal');");
				result = sql.executeQuery();
				result = sql.executeQuery();
				while (result.next()) {
					String kit = result.getString("permissoes");
					addPlayerPerms(uuid, kit);
				}
				result.close();
				sql.close();

			}
		}
		result.close();
		sql.close();
		Connect.lock.unlock();

	}

	public void loadPlayerGroup(UUID uuid) throws SQLException {
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement(
				"SELECT * FROM `playergroups` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			Group grupo = Group.valueOf(result.getString("groups").toUpperCase());
			setPlayerGroup(uuid, grupo);
			result.close();
			stmt.close();
		} else {
			Group grupo = Group.NORMAL;
			setPlayerGroup(uuid, grupo);
		}
		Connect.lock.unlock();
	}

	public static boolean isGroup(Player player, Group group) {
		if (!playerGroups.containsKey(player.getUniqueId()))
			return false;
		return playerGroups.get(player.getUniqueId()) == group;
	}

	public List<String> carregarPermsOff(UUID uuid) throws SQLException {
		Connect.lock.lock();
		List<String> perms = new ArrayList<String>();
		PreparedStatement sql = getMySQL().prepareStatement(
				"SELECT * FROM `permissoes` WHERE (`uuid`='" + uuid.toString().replace("-", "") + "');");
		ResultSet result = sql.executeQuery();
		while (result.next()) {
			String kit = result.getString("permissoes");
			perms.add(kit);
		}
		result.close();
		sql.close();
		Connect.lock.unlock();
		return perms;
	}

	public List<String> carregarPermsGrupo(String grupo) throws SQLException {
		Connect.lock.lock();
		List<String> perms = new ArrayList<String>();
		PreparedStatement sql = getMySQL()
				.prepareStatement("SELECT * FROM `groupperms` WHERE (`grupo`='" + grupo + "');");
		ResultSet result = sql.executeQuery();
		while (result.next()) {
			String kit = result.getString("permissoes");
			perms.add(kit);
		}
		result.close();
		sql.close();
		Connect.lock.unlock();
		return perms;
	}

	public static boolean hasPermission(final UUID uuid, final String perm) {
		if (playerPerms.containsKey(uuid)) {
			try {
				String permissao = perm.toLowerCase();
				if (playerPerms.get(uuid).contains("*"))
					return true;
				if (playerPerms.get(uuid).contains(permissao)) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean hasGroupPermission(UUID uuid, Group group) {
		if (!playerGroups.containsKey(uuid))
			return false;
		Group playerGroup = playerGroups.get(uuid);
		return playerGroup.ordinal() >= group.ordinal();
	}

	public static boolean hasGroupPermission(Player player, Group group) {
		return hasGroupPermission(player.getUniqueId(), group);
	}

	public Group getPlayerGroup(UUID uuid) {
		if (!playerGroups.containsKey(uuid))
			return Group.NORMAL;
		return playerGroups.get(uuid);
	}

	public Group getPlayerGroup(Player player) {
		if (!playerGroups.containsKey(player.getUniqueId()))
			return Group.NORMAL;
		return playerGroups.get(player.getUniqueId());
	}

	public void setPlayerGroup(UUID uuid, Group group) {
		playerGroups.put(uuid, group);
	}

	public void removePlayerGroup(UUID uuid) {
		playerGroups.remove(uuid);
	}

	public void removePlayer(UUID uuid) throws SQLException {
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement(
				"SELECT * FROM `playergroups` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			stmt.execute("DELETE FROM `playergroups` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		}
		result.close();
		stmt.close();
		Connect.lock.unlock();
	}

	public void savePlayerGroup(UUID uuid, Group group) throws SQLException {
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement(
				"SELECT * FROM `playergroups` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			stmt.execute("UPDATE `playergroups` SET groups='" + group.toString().toLowerCase() + "' WHERE uuid='"
					+ uuid.toString().replace("-", "") + "';");
		} else {
			stmt.execute("INSERT INTO `playergroups`(`uuid`, `groups`) VALUES ('" + uuid.toString().replace("-", "")
					+ "', '" + group.toString().toLowerCase() + "');");
		}
		result.close();
		stmt.close();
		Connect.lock.unlock();
	}

	public void alterarPermissao(final UUID uuid, final String permissao, Alterar tipo) throws SQLException {
		Connect.lock.lock();
		if (tipo == Alterar.ADICIONAR) {
			Statement sql = getMySQL().createStatement();
			sql.executeUpdate("INSERT INTO `permissoes`(`uuid`, `permissoes`) VALUES ('"
					+ uuid.toString().replace("-", "") + "','" + permissao.toLowerCase() + "');");
			sql.close();
		} else if (tipo == Alterar.REMOVER) {
			PreparedStatement stmt = getMySQL().prepareStatement(
					"SELECT `permissoes` FROM `permissoes` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("DELETE FROM `permissoes` WHERE `uuid`='" + uuid.toString().replace("-", "")
						+ "' AND `permissoes`='" + permissao.toLowerCase() + "';");
			}
			stmt.close();
			result.close();
		}
		Connect.lock.unlock();
	}

	public void alterarPermissaoGrupo(final String grupo, final String permissao, Alterar tipo) throws SQLException {
		Connect.lock.lock();
		if (tipo == Alterar.ADICIONAR) {
			Statement sql = getMySQL().createStatement();
			sql.executeUpdate("INSERT INTO `groupperms`(`grupo`, `permissoes`) VALUES ('" + grupo + "','"
					+ permissao.toLowerCase() + "');");
			sql.close();
		} else if (tipo == Alterar.REMOVER) {
			PreparedStatement stmt = getMySQL()
					.prepareStatement("SELECT `permissoes` FROM `groupperms` WHERE `grupo` = '" + grupo + "';");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("DELETE FROM `groupperms` WHERE `grupo`='" + grupo + "' AND `permissoes`='"
						+ permissao.toLowerCase() + "';");
			}
			stmt.close();
			result.close();
		}
		Connect.lock.unlock();
	}

	public void addPlayerPerms(UUID uuid, String perm) {
		if (playerPerms.containsKey(uuid)) {
			ArrayList<String> perms = playerPerms.get(uuid);
			if (!perms.contains(perm)) {
				perms.add(perm);
				playerPerms.put(uuid, perms);
			}
		} else {
			ArrayList<String> perms = new ArrayList<>();
			perms.add(perm);
			playerPerms.put(uuid, perms);
		}
	}

	public void addGroupPerms(UUID uuid, String perm) {
		if (groupPerms.containsKey(uuid)) {
			ArrayList<String> perms = groupPerms.get(uuid);
			if (!perms.contains(perm)) {
				perms.add(perm);
				groupPerms.put(uuid, perms);
			}
		} else {
			ArrayList<String> perms = new ArrayList<>();
			perms.add(perm);
			groupPerms.put(uuid, perms);
		}
	}

}
