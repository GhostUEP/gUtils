package me.ghost.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.Server;

public abstract class Management {
	private Main main;

	public Management(Main main) {
		this.main = main;
	}

	public abstract void onEnable();

	public abstract void onDisable();

	public Server getServer() {
		return main.getServer();
	}

	public Main getPlugin() {
		return main;
	}

	public Logger getLogger() {
		return main.getLogger();
	}

	public Connection getMySQL() {
		try {
			if (main.mainConnection == null || main.mainConnection.isClosed())
				main.mainConnection = main.connect.trySQLConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return main.mainConnection;
	}
}
