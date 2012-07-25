package org.monstercraft.support.plugin.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.command.GameCommand;

public class List extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("modlist");
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
		int i = 1;
		sender.sendMessage(ChatColor.RED + "Listing all online Mods");
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (MonsterTickets.getPermissionsHandler().hasNode(p,
					"monstertickets.mod")) {
				sender.sendMessage(ChatColor.GREEN + "" + i + ". "
						+ p.getName());
				i++;
			}
		}
		return true;
	}

	@Override
	public String getPermission() {
		return "monstertickets.list";
	}

}
