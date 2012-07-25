package org.monstercraft.support;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.support.plugin.Configuration;
import org.monstercraft.support.plugin.managers.CommandManager;
import org.monstercraft.support.plugin.managers.SettingsManager;
import org.monstercraft.support.plugin.managers.handlers.PermissionsHandler;
import org.monstercraft.support.plugin.managers.listeners.MonsterTicketListener;
import org.monstercraft.support.plugin.util.Metrics;

public class MonsterTickets extends JavaPlugin implements Runnable {

	private CommandManager commandManager;
	private static PermissionsHandler perms = new PermissionsHandler();
	private static SettingsManager settings = null;

	public void onEnable() {
		settings = new SettingsManager(this);
		commandManager = new CommandManager();
		getServer().getPluginManager().registerEvents(
				new MonsterTicketListener(), this);
		Configuration.log("MonsterTickets has been enabled!");
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	public void onDisable() {
		settings.save();
		settings.saveTicketsConfig();
		Configuration.log("MonsterTickets has been disabled.");
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return commandManager.onGameCommand(sender, command, label, args);
	}

	public static PermissionsHandler getPermissionsHandler() {
		return perms;
	}

	public static SettingsManager getSettingsManager() {
		return settings;
	}

	public void run() {
		try {
			Configuration.log("Setting up metrics!");
			Metrics metrics = new Metrics(this);
			metrics.start();
			String newVersion = Configuration.checkForUpdates(this,
					Configuration.URLS.UPDATE_URL);
			if (!newVersion.contains(Configuration.getCurrentVerison(this))) {
				Configuration.log(newVersion + " is out! You are running "
						+ Configuration.getCurrentVerison(this));
				Configuration
						.log("Update MonsterTickets at: http://dev.bukkit.org/server-mods/monstertickets");
			} else {
				Configuration
						.log("You are using the latest version of MonsterTickets.");
			}
		} catch (Exception e) {
			Configuration.debug(e);
		}
	}

}