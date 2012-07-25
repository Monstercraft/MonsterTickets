package org.monstercraft.support.plugin.command;

import org.bukkit.command.CommandSender;

public abstract class GameCommand {

	public abstract boolean canExecute(CommandSender sender, String[] split);

	public abstract boolean execute(CommandSender sender, String[] split);

	public abstract String getPermission();
}