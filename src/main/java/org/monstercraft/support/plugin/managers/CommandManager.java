package org.monstercraft.support.plugin.managers;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.command.GameCommand;
import org.monstercraft.support.plugin.command.commands.Check;
import org.monstercraft.support.plugin.command.commands.Claim;
import org.monstercraft.support.plugin.command.commands.Close;
import org.monstercraft.support.plugin.command.commands.List;
import org.monstercraft.support.plugin.command.commands.Open;
import org.monstercraft.support.plugin.command.commands.Reload;
import org.monstercraft.support.plugin.command.commands.Teleport;

/**
 * This class manages all of the plugins commands.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class CommandManager {

	private LinkedList<GameCommand> gameCommands = new LinkedList<GameCommand>();

	private final MonsterTickets instance;

	public CommandManager(MonsterTickets instance) {
		this.instance = instance;
		try {
			gameCommands.add(new Reload(instance));
			gameCommands.add(new List(instance));
			gameCommands.add(new Check(instance));
			gameCommands.add(new Open(instance));
			gameCommands.add(new Close(instance));
			gameCommands.add(new Claim(instance));
			gameCommands.add(new Teleport(instance));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executes a command that was ran in game or through the console.
	 * 
	 * @param sender
	 *            The command sender.
	 * @param command
	 *            The command.
	 * @param label
	 *            The commands label.
	 * @param args
	 *            The arguments in the command.
	 * @return True if the command executed successfully; Otherwise false.
	 */
	public boolean onGameCommand(final CommandSender sender,
			final Command command, final String label, final String[] args) {
		String[] split = new String[args.length + 1];
		split[0] = label;
		for (int i = 0; i < args.length; i++) {
			split[i + 1] = args[i];
		}
		for (GameCommand c : gameCommands) {
			if (c.canExecute(sender, split)) {
				if (hasPerms(sender, c)) {
					try {
						c.execute(sender, split);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					break;
				}
			}
		}
		return true;
	}

	private boolean hasPerms(CommandSender sender, GameCommand command) {
		if (sender instanceof Player) {
			if (!instance.getPermissionsHandler().hasCommandPerms(
					((Player) sender), command)) {
				sender.sendMessage(ChatColor.RED
						+ "You don't have permission to perform this command.");
				return false;
			}
		}
		return true;
	}
}