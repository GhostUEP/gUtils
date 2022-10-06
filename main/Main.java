package me.ghost.main;

import java.sql.Connection;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import me.ghost.banmanager.BanManagement;
import me.ghost.mysql.Connect;
import me.ghost.pagamento.BuyManager;
import me.ghost.permissions.PermissionsManager;
import me.ghost.tag.TagManager;
import me.ghost.utils.config.Config;
import me.ghost.utils.config.ConfigEnum;

public class Main extends JavaPlugin {
	public boolean sql = true;
	public String host = "";
	public String database = "";
	public String password = "";
	public String user = "";
	public String port = "3306";
	public Connect connect;
	public Connection mainConnection;
	private static Main instance;
	private PermissionsManager permissionsManager;
	private BuyManager buyManager;
	private BanManagement banManager;
	private TagManager tagManager;
	public Config config = new Config(this);
	public static Main plugin;
	public String site = "likekits.com.br";
	
	@EventHandler
	public void onLoad() {
		config.loadConfig();
	}
	@Override
	public void onEnable() {
		plugin = this;
		prepareConfig();
		connect = new Connect(this);
		permissionsManager = new PermissionsManager(this);
		permissionsManager.onEnable();
		banManager = new BanManagement(this);
		banManager.onEnable();
		buyManager = new BuyManager(this);
		buyManager.onEnable();
		tagManager = new TagManager(this);
		tagManager.onEnable();
		mainConnection = connect.trySQLConnection();
	}

	@Override
	public void onDisable() {
		Connect.SQLdisconnect(mainConnection);
	}

	public BuyManager getBuyManager() {
		return buyManager;
	}

	public PermissionsManager getPermissionManager() {
		return permissionsManager;
	}

	public static Main getPlugin() {
		return instance;
	}
	private void prepareConfig() {
		FileConfiguration c = config.getConfig(ConfigEnum.CONFIG);
		host = c.getString("sql-host");
		database = c.getString("sql-db");
		password = c.getString("sql-pass");
		user = c.getString("sql-user");
		getLogger().info("Carregando Config!");
	}

}
