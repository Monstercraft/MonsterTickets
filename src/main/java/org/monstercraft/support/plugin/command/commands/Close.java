package org.monstercraft.support.plugin.command.commands;

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

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("close");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (sender instanceof Player) {
				if (!MonsterTickets.getPermissionsHandler().hasCommandPerms(
						((Player) sender), this)) {
					sender.sendMessage("You don't have permission to preform this command.");
					return true;
				}
			}
		}
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
			close((Player) sender);
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

	public static void close(Player mod) {
		if (mod == null) {
			return;
		}
		for (HelpTicket t : Variables.tickets) {
			if (t.getStatus().equals(Status.CLOSED)) {
				continue;
			}
			if (t.getMod() == mod) {
				t.close();
				Player p = t.getNoob();
				if (p != null) {
					p.sendMessage(ChatColor.GREEN
							+ "Your support ticket has been closed.");
				}
				mod.sendMessage(ChatColor.GREEN + "Ticket " + t.getID()
						+ " sucessfully closed.");
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (MonsterTickets.getPermissionsHandler().hasNode(pl,
							"monstertickets.mod")) {
						pl.sendMessage(ChatColor.GREEN + mod.getName()
								+ " closed ticket " + t.getID());
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
				t.close();
				Player p = t.getNoob();
				if (p != null) {
					p.sendMessage(ChatColor.GREEN
							+ "Your support ticket has been closed.");
				}
				mod.sendMessage(ChatColor.GREEN + "Ticket " + t.getID()
						+ " sucessfully closed.");
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (MonsterTickets.getPermissionsHandler().hasNode(pl,
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
			t.close();
			Player p = t.getNoob();
			if (p != null) {
				p.sendMessage(ChatColor.GREEN
						+ "Your support ticket has been forced closed, if this was a mistake please create a new ticket.");
			}
		}
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (MonsterTickets.getPermissionsHandler().hasNode(pl,
					"monstertickets.mod")) {
				pl.sendMessage(ChatColor.GREEN
						+ "All support tickets have been closed by "
						+ sender.getName() + ".");
			}
		}
	}

}
