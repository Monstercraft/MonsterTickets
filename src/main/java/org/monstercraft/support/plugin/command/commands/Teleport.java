package org.monstercraft.support.plugin.command.commands;

import javax.net.ssl.SSLEngineResult.Status;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.Configuration;
import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.command.GameCommand;
import org.monstercraft.support.plugin.wrappers.HelpTicket;

public class Teleport extends GameCommand {

    private static MonsterTickets instance;

    public Teleport(final MonsterTickets instance) {
        Teleport.instance = instance;
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].equalsIgnoreCase("tp-ticket");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to teleport");
            return true;
        }
        if (split.length < 2) {
            sender.sendMessage(ChatColor.GREEN + "Invalid command usage!");
            return true;
        }
        if (!Configuration.canParse(split[1])) {
            sender.sendMessage(ChatColor.GREEN + "Invalid number!");
            return true;
        }
        final int max = Teleport.instance.getNextTicketID();
        if (Integer.parseInt(split[1]) >= max || 1 > Integer.parseInt(split[1])) {
            sender.sendMessage(ChatColor.GREEN
                    + "No ticket exists with that number!");
            return true;
        }
        for (final HelpTicket t : Variables.tickets) {
            if (t.getID() == Integer.parseInt(split[1])) {
                if (t.getStatus().equals(Status.CLOSED)) {
                    sender.sendMessage(ChatColor.RED + "That ticket is closed.");
                    return true;
                }
                ((Player) sender).teleport(new Location(Bukkit.getWorld(t
                        .getWorldName()), t.getX(), t.getY(), t.getZ()));
                sender.sendMessage(ChatColor.GREEN
                        + "Teleporting to ticket location!");
            }
        }
        return true;
    }

    @Override
    public String getPermission() {
        return "monstertickets.tp";
    }

}
