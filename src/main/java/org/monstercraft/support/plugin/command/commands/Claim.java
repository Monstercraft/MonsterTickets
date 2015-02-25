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

public class Claim extends GameCommand {

    private static MonsterTickets instance;

    public Claim(final MonsterTickets instance) {
        Claim.instance = instance;
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].equalsIgnoreCase("claim");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be ingame to claim a ticket!");
            return true;
        }
        if (split.length < 2) {
            sender.sendMessage(ChatColor.RED + "Invalid command usage!");
            return true;
        }
        if (!Configuration.canParse(split[1])) {
            sender.sendMessage(ChatColor.GREEN + "Invalid number!");
            return true;
        }
        for (final HelpTicket t : Variables.tickets) {
            if (t.getStatus().equals(Status.CLAIMED)) {
                if (t.getModName()
                        .equalsIgnoreCase(((Player) sender).getName())) {
                    sender.sendMessage(ChatColor.GREEN
                            + "You are already supporting someone!");
                    return true;
                }
            }
        }
        if (Integer.parseInt(split[1]) >= Claim.instance.getNextTicketID()
                || 1 > Integer.parseInt(split[1])) {
            sender.sendMessage(ChatColor.GREEN
                    + "No ticket exists with that number!");
            return true;
        }
        for (final HelpTicket t : Variables.tickets) {
            if (!t.getStatus().equals(Status.OPEN)) {
                continue;
            }
            if (t.getID() == Integer.parseInt(split[1])) {
                final Player noob = Bukkit.getPlayer(t.getNoobName());
                if (noob == null) {
                    sender.sendMessage(ChatColor.RED
                            + "Player not online, unable to claim the ticket");
                    return true;
                }
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (Claim.instance.getPermissionsHandler().hasNode(p,
                            "monstertickets.mod")) {
                        p.sendMessage(ChatColor.GREEN + sender.getName()
                                + " is now handeling ticket " + t.getID());
                    }
                }
                t.Claim(((Player) sender).getName());
                if (Variables.useMYSQLBackend) {
                    try {
                        Claim.instance.getMySQL().claimTicket(t);
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                sender.sendMessage(ChatColor.GREEN + "Ticket " + t.getID()
                        + " sucessfully claimed.");
                noob.sendMessage(ChatColor.GREEN
                        + "******************************************************");
                noob.sendMessage(ChatColor.RED
                        + "Your support ticket request has been accepted!");
                noob.sendMessage(ChatColor.RED
                        + "Start chatting with the mod assisting you.");
                noob.sendMessage(ChatColor.GREEN
                        + "******************************************************");
                noob.sendMessage("");
                noob.sendMessage(ChatColor.RED + "[Support]" + sender.getName()
                        + ": " + ChatColor.WHITE + " Hello, my name is "
                        + ChatColor.GOLD + sender.getName() + ChatColor.WHITE
                        + " how can I help you?");
                sender.sendMessage(ChatColor.RED + "[Support]"
                        + sender.getName() + ": " + ChatColor.WHITE
                        + " Hello, my name is " + ChatColor.GOLD
                        + sender.getName() + ChatColor.WHITE
                        + " how can I help you?");
                return true;
            }
        }
        sender.sendMessage(ChatColor.GREEN
                + "The ticket ID you specified is not an open ticket, check to make sure it isn't closed or claimed.");
        return true;
    }

    @Override
    public String getPermission() {
        return "monstertickets.claim";
    }

}
