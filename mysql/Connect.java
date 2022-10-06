package me.ghost.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import me.ghost.main.Main;

public class Connect {
	private Main m;
	public static ReentrantLock lock = new ReentrantLock(true);

	public Connect(Main m) {
		this.m = m;
	}

	public synchronized Connection trySQLConnection() {
		if (!m.sql) {
			m.getLogger().info("MySQL Desativado!");
			return null;
		}
		lock.lock();
		try {
			m.getLogger().info("Conectando ao MySQL");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String conn = "jdbc:mysql://" + m.host + ":" + m.port + "/" + m.database;
			lock.unlock();
			return DriverManager.getConnection(conn, m.user, m.password);
		} catch (ClassNotFoundException ex) {
			m.getLogger().warning("MySQL Driver nao encontrado!");
			m.sql = false;
		} catch (SQLException ex) {
			m.getLogger().warning("Erro enquanto tentava conectar ao Mysql!");
			ex.printStackTrace();
			m.sql = false;
		} catch (Exception ex) {
			m.getLogger().warning("Erro desconhecido enquanto tentava conectar ao MySQL.");
			m.sql = false;
		}
		lock.unlock();
		return null;
	}

	public void prepareSQL(Connection con) {
		if (m.sql) {
			SQLQuery("CREATE TABLE IF NOT EXISTS `Torneios` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `Inicio` datetime NOT NULL, `Fim` datetime, `Vencedor` int(10), PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;", con);
			SQLQuery("CREATE TABLE IF NOT EXISTS `Jogadores` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `Nome` varchar(255) NOT NULL, PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;", con);
			SQLQuery("CREATE TABLE IF NOT EXISTS `Jogadas` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `Player` int(10), `Jogo` int(10), `Kit` varchar(255), `Morte` datetime, `Killer` int(10), PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;", con);
			SQLQuery("CREATE TABLE IF NOT EXISTS `Rank` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `Player` varchar(255) NOT NULL, `Rank` varchar(255) NOT NULL, PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;", con);
			SQLQuery("CREATE TABLE IF NOT EXISTS `Kits` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `Player` varchar(255) NOT NULL, `Kits` varchar(255) NOT NULL, PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;", con);
			m.getLogger().info("Criando Tabelas no SQL");
		}
	}

	public static void SQLdisconnect(Connection con) {
		lock.lock();
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		lock.unlock();
	}

	public void SQLQuery(final String sql) {
		if (!m.sql)
			return;
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(new Thread(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				try {
					Statement stmt = m.mainConnection.createStatement();
					stmt.executeUpdate(sql);
					stmt.close();
				} catch (SQLException e) {
					m.getLogger().info("Erro ao tentar executar Query");
					m.getLogger().info(sql);
					m.getLogger().info(e.getMessage());
				}
				lock.unlock();
			}
		}));
		executor.shutdown();
	}

	public void SQLQuery(String sql, Connection con) {
		if (!m.sql)
			return;
		lock.lock();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			m.getLogger().info("Erro ao tentar executar Query");
			m.getLogger().info(e.getMessage());
		}
		lock.unlock();
	}
}