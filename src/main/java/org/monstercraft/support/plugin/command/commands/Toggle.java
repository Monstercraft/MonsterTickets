package org.monstercraft.support.plugin.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.command.GameCommand;

public class Toggle extends GameCommand {

    private static MonsterTickets instance;

    public Toggle(final MonsterTickets instance) {
        Toggle.instance = instance;
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split.length > 1 && split[0].equalsIgnoreCase("mt")
                && split[1].equalsIgnoreCase("toggle");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to modify toggles.");
        }
        if (split.length < 3) {
            sender.sendMessage(ChatColor.GREEN
                    + "You must add an option to toggle. View the bukkit dev page for a list of toggles.");
            return true;
        }
        if (split[2].equalsIgnoreCase("spy")) {
            if (!Toggle.instance.getPermissionsHandler().hasNode(
                    (Player) sender, "monstertickets.mod.spy")) {
                sender.sendMessage(ChatColor.RED
                        + "You do not have permission to modify that toggle!");
                return true;
            }
            if (Variables.nospy.contains(sender)) {
                Variables.nospy.remove(sender);
                sender.sendMessage(ChatColor.GREEN
                        + "Successfully enabled spy mode!");
            } else {
                Variables.nospy.add((Player) sender);
                sender.sendMessage(ChatColor.GREEN
                        + "Successfully disabled spy mode!");
            }
            return true;
        }
        if (split[2].equalsIgnoreCase("adminchat")
                || split[2].equalsIgnoreCase("ac")) {
            if (!Toggle.instance.getPermissionsHandler().hasNode(
                    (Player) sender, "monstertickets.adminchat")) {
                sender.sendMessage(ChatColor.RED
                        + "You do not have permission to modify that toggle!");
                return true;
            }
            if (Variables.adminchat.contains(sender)) {
                Variables.adminchat.remove(sender);
                sender.sendMessage(ChatColor.GREEN
                        + "Your messages will now be sent to game!");
            } else {
                Variables.adminchat.add((Player) sender);
                sender.sendMessage(ChatColor.GREEN
                        + "Your messages will now be sent to adminchat");
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "No such toggle exists!");
        return true;
    }

    @Override
    public String getPermission() {
        return "monstertickets.toggle";
    }

}
