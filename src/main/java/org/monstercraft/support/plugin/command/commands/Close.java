package org.monstercraft.support.plugin.command.commands;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.Configuration;
import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.command.GameCommand;
import org.monstercraft.support.plugin.util.Status;
import org.monstercraft.support.plugin.wrappers.HelpTicket;

public class Close extends GameCommand {

	private static MonsterTickets instance;

	public Close(MonsterTickets instance) {
		Close.instance = instance;
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("close");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (split.length == 2) {
			if (split[1].equalsIgnoreCase("all")) {
				closeAll(sender);
				sender.sendMessage(ChatColor.GREEN
						+ "Successfully closed all open and claimed tickets!");
				return true;
			} else if (Configuration.canParse(split[1])) {
				close(sender, Integer.parseInt(split[1]));
				return true;
			} else {
				sender.sendMessage(ChatColor.GREEN + "Invalid command usage.");
				return true;
			}
		}
		if (sender instanceof Player) {
			close(((Player) sender).getName());
			return true;
		}
		sender.sendMessage(ChatColor.GREEN
				+ "You must be ingame and supporting a ticket to close a ticket.");
		return true;
	}

	@Override
	public String getPermission() {
		return "monstertickets.close";
	}

	public static void close(String modname) {
		Player mod = Bukkit.getPlayer(modname);
		for (HelpTicket t : Variables.tickets) {
			if (t.getStatus().equals(Status.CLOSED)) {
				continue;
			}
			if (t.getModName().equalsIgnoreCase(modname)) {
				t.close();
				sendToDB(t);
				Player p = Bukkit.getPlayer(t.getNoobName());
				if (p != null) {
					p.sendMessage(ChatColor.GREEN
							+ "Your support ticket has been closed.");
				}
				if (mod != null) {
					mod.sendMessage(ChatColor.GREEN + "Ticket " + t.getID()
							+ " sucessfully closed.");
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (instance.getPermissionsHandler().hasNode(pl,
								"monstertickets.mod")) {
							pl.sendMessage(ChatColor.GREEN + mod.getName()
									+ " closed ticket " + t.getID());
						}
					}
				}
				return;
			}
		}
		mod.sendMessage(ChatColor.GREEN
				+ "You are not currently supporting a ticket!");
	}

	public static void close(CommandSender mod, int id) {
		for (HelpTicket t : Variables.tickets) {
			if (t.getID() == id) {
				if (t.getStatus().equals(Status.CLOSED)) {
					mod.sendMessage(ChatColor.GREEN
							+ "That ticket is already closed!");
					return;
				}
				if (t.getStatus().equals(Status.OPEN)) {
					t.Claim(mod.getName());
				}
				t.close();
				sendToDB(t);
				Player p = Bukkit.getPlayer(t.getNoobName());
				if (p != null) {
					p.sendMessage(ChatColor.GREEN
							+ "Your support ticket has been closed.");
				}
				mod.sendMessage(ChatColor.GREEN + "Ticket " + t.getID()
						+ " sucessfully closed.");
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (instance.getPermissionsHandler().hasNode(pl,
							"monstertickets.mod")) {
						pl.sendMessage(ChatColor.GREEN + mod.getName()
								+ " closed ticket " + t.getID());
					}
				}
				return;
			}
		}
		mod.sendMessage(ChatColor.GREEN + "No ticket with that ID exists!");
	}

	public static void closeAll(CommandSender sender) {
		for (HelpTicket t : Variables.tickets) {
			if (!t.getStatus().equals(Status.CLOSED)) {
				if (t.getStatus().equals(Status.OPEN)) {
					t.Claim(sender.getName());
				}
				t.close();
				sendToDB(t);
				Player p = Bukkit.getPlayer(t.getNoobName());
				if (p != null) {
					p.sendMessage(ChatColor.GREEN
							+ "Your support ticket has been forced closed, if this was a mistake please create a new ticket.");
				}
			}
		}
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (instance.getPermissionsHandler().hasNode(pl,
					"monstertickets.mod")) {
				pl.sendMessage(ChatColor.GREEN
						+ "All support tickets have been closed by "
						+ sender.getName() + ".");
			}
		}
	}

	public static void closeClaimed() {
		for (HelpTicket t : Variables.tickets) {
			if (t.getStatus().equals(Status.CLAIMED)) {
				t.close();
				sendToDB(t);
			}
		}
	}

	private static void sendToDB(HelpTicket t) {
		if (Variables.useMYSQLBackend) {
			try {
				instance.getMySQL().closeTicket(t);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
