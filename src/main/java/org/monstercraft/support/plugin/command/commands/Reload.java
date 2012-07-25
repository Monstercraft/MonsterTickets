package org.monstercraft.support.plugin.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.command.GameCommand;

public class Reload extends GameCommand {
	
	private static MonsterTickets instance;

	public Reload(MonsterTickets instance) {
		Reload.instance = instance;
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("mtreload");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		instance.getSettingsManager().load();
		sender.sendMessage(ChatColor.GREEN
				+ "Successfully reloaded plugin settings!");
		return true;
	}

	@Override
	public String getPermission() {
		return "monstertickets.reload";
	}

}
