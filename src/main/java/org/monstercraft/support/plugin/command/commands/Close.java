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

    public static void close(final CommandSender mod, final int id) {
        for (final HelpTicket t : Variables.tickets) {
            if (t.getID() == id) {
                if (t.getStatus().equals(Status.CLOSED)) {
                    mod.sendMessage(ChatColor.GREEN
                            + "That ticket is already closed!");
                    return;
                }
                if (t.getStatus().equals(Status.OPEN)) {
                    t.Claim(mod.getName());
                }
                final Player p = Bukkit.getPlayer(t.getNoobName());
                t.close();
                Close.sendToDB(t);
                if (p != null) {
                    p.sendMessage(ChatColor.GREEN
                            + "Your support ticket has been closed.");
                }
                mod.sendMessage(ChatColor.GREEN + "Ticket " + id
                        + " sucessfully closed.");
                for (final Player pl : Bukkit.getOnlinePlayers()) {
                    if (Close.instance.getPermissionsHandler().hasNode(pl,
                            "monstertickets.mod")) {
                        pl.sendMessage(ChatColor.GREEN + mod.getName()
                                + " closed ticket " + id);
                    }
                }
                return;
            }
        }
        mod.sendMessage(ChatColor.GREEN + "No ticket with that ID exists!");
    }

    public static void close(final String modname) {
        final Player mod = Bukkit.getPlayer(modname);
        for (final HelpTicket t : Variables.tickets) {
            if (t.getStatus().equals(Status.CLOSED)) {
                continue;
            }
            if (t.getModName().equalsIgnoreCase(modname)) {
                final Player p = Bukkit.getPlayer(t.getNoobName());
                final int id = t.getID();
                t.close();
                Close.sendToDB(t);
                if (p != null) {
                    p.sendMessage(ChatColor.GREEN
                            + "Your support ticket has been closed.");
                }
                if (mod != null) {
                    mod.sendMessage(ChatColor.GREEN + "Ticket " + id
                            + " sucessfully closed.");
                    for (final Player pl : Bukkit.getOnlinePlayers()) {
                        if (Close.instance.getPermissionsHandler().hasNode(pl,
                                "monstertickets.mod")) {
                            pl.sendMessage(ChatColor.GREEN + mod.getName()
                                    + " closed ticket " + id);
                        }
                    }
                }
                return;
            }
        }
        mod.sendMessage(ChatColor.GREEN
                + "You are not currently supporting a ticket!");
    }

    public static void closeAll(final CommandSender sender) {
        for (final HelpTicket t : Variables.tickets) {
            if (!t.getStatus().equals(Status.CLOSED)) {
                if (t.getStatus().equals(Status.OPEN)) {
                    t.Claim(sender.getName());
                }
                final Player p = Bukkit.getPlayer(t.getNoobName());
                t.close();
                Close.sendToDB(t);
                if (p != null) {
                    p.sendMessage(ChatColor.GREEN
                            + "Your support ticket has been forced closed, if this was a mistake please create a new ticket.");
                }
            }
        }
        for (final Player pl : Bukkit.getOnlinePlayers()) {
            if (Close.instance.getPermissionsHandler().hasNode(pl,
                    "monstertickets.mod")) {
                pl.sendMessage(ChatColor.GREEN
                        + "All support tickets have been closed by "
                        + sender.getName() + ".");
            }
        }
    }

    public static void closeClaimed() {
        for (final HelpTicket t : Variables.tickets) {
            if (t.getStatus().equals(Status.CLAIMED)) {
                t.close();
                Close.sendToDB(t);
            }
        }
    }

    private static void sendToDB(final HelpTicket t) {
        if (Variables.useMYSQLBackend) {
            try {
                Close.instance.getMySQL().closeTicket(t);
                Variables.tickets.remove(t);
            } catch (final SQLException e) {
                Configuration.debug(e);
            }
        }
    }

    private static MonsterTickets instance;

    public Close(final MonsterTickets instance) {
        Close.instance = instance;
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].equalsIgnoreCase("close");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        if (split.length == 2) {
            if (split[1].equalsIgnoreCase("all")) {
                Close.closeAll(sender);
                sender.sendMessage(ChatColor.GREEN
                        + "Successfully closed all open and claimed tickets!");
                return true;
            } else if (Configuration.canParse(split[1])) {
                Close.close(sender, Integer.parseInt(split[1]));
                return true;
            } else {
                sender.sendMessage(ChatColor.GREEN + "Invalid command usage.");
                return true;
            }
        }
        if (sender instanceof Player) {
            Close.close(((Player) sender).getName());
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

}
