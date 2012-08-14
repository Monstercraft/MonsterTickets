package org.monstercraft.support;

import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.support.plugin.Configuration;
import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.command.commands.Close;
import org.monstercraft.support.plugin.managers.CommandManager;
import org.monstercraft.support.plugin.managers.SettingsManager;
import org.monstercraft.support.plugin.managers.handlers.PermissionsHandler;
import org.monstercraft.support.plugin.managers.listeners.MonsterTicketListener;
import org.monstercraft.support.plugin.util.Metrics;
import org.monstercraft.support.plugin.util.MySQL;
import org.monstercraft.support.plugin.util.Updater;

public final class MonsterTickets extends JavaPlugin {

	private CommandManager commandManager;
	private static PermissionsHandler perms;
	private SettingsManager settings;
	private MySQL mysql;

	public void onEnable() {
		settings = new SettingsManager(this);
		commandManager = new CommandManager(this);
		perms = new PermissionsHandler();
		if (Variables.useMYSQLBackend) {
			try {
				mysql = new MySQL();
				Variables.tickets.addAll(mysql.readTickets(1));
			} catch (Exception e) {
				Configuration.debug(e);
				Configuration
						.log(ChatColor.DARK_RED
								+ "Error connecting to database! Falling back to file backend!");
				Variables.useMYSQLBackend = false;
				settings.loadTickets();
			}
		} else {
			settings.loadTickets();
		}
		getServer().getPluginManager().registerEvents(
				new MonsterTicketListener(this), this);
		Configuration.log("MonsterTickets has been enabled!");
		Configuration.log("Setting up metrics!");
		try {
			new Metrics(this).start();
		} catch (IOException e) {
		}
		this.getServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(this,
						new Updater(this.getDescription().getVersion()));
	}

	public void onDisable() {
		Close.closeClaimed();
		settings.save();
		if (!Variables.useMYSQLBackend) {
			settings.saveTicketsConfig();
		}
		Configuration.log("MonsterTickets has been disabled.");
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return commandManager.onGameCommand(sender, command, label, args);
	}

	public PermissionsHandler getPermissionsHandler() {
		return perms;
	}

	public SettingsManager getSettingsManager() {
		return settings;
	}

	public MySQL getMySQL() {
		return mysql;
	}

	public int getNextTicketID() {
		if (Variables.useMYSQLBackend) {
			try {
				return mysql.readLastRowID() + 1;
			} catch (SQLException e) {
			}
		}
		if (!Variables.tickets.isEmpty()) {
			return Variables.tickets.getLast().getID() + 1;
		}
		return 1;
	}

	public static void sendAdminChatMessage(String player, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (perms.hasNode(p, "monstertickets.adminchat")) {
				p.sendMessage(ChatColor.RED + "[Admin Chat]" + player + ": "
						+ ChatColor.RESET + message);
			}
		}
	}
}