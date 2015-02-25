package org.monstercraft.support.plugin.command.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.Configuration;
import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.command.GameCommand;
import org.monstercraft.support.plugin.util.Status;
import org.monstercraft.support.plugin.wrappers.HelpTicket;

public class Check extends GameCommand {

    private static MonsterTickets instance;

    ArrayList<HelpTicket> tickets = new ArrayList<HelpTicket>();

    private boolean show_closed;

    public Check(final MonsterTickets instance) {
        Check.instance = instance;
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].equalsIgnoreCase("check");
    }

    private void displayPage(final int page, final CommandSender sender) {
        int numPages = tickets.size() / 15;
        if (numPages == 0) {
            numPages = 1;
        }
        final int start = (page - 1) * 15;
        final int end = start + 15;
        int c = 0;
        for (final HelpTicket t : tickets) {
            if (c < start) {
                c++;
                continue;
            }
            if (c > end) {
                break;
            }
            if (t.getStatus().equals(Status.CLOSED) && !show_closed) {
                continue;
            }
            this.displayTicket(t, sender);
            c++;
        }
        sender.sendMessage(ChatColor.RED + "-----------[ " + page + " of "
                + (tickets.size() / 15 + 1) + " ]-------------");
    }

    private void displayTicket(final HelpTicket t, final CommandSender sender) {
        if (t.getStatus().equals(Status.OPEN)) {
            sender.sendMessage(ChatColor.GREEN + "" + t.getID()
                    + ChatColor.GOLD + " - " + t.getNoobName()
                    + ChatColor.GREEN + " - " + t.getDescription()
                    + ChatColor.GREEN + " - X:" + t.getX() + " Y:" + t.getY()
                    + " Z:" + t.getZ() + " World:" + t.getWorldName());
        } else if (t.getStatus().equals(Status.CLAIMED)) {
            sender.sendMessage(ChatColor.BLUE + "" + t.getID() + ChatColor.GOLD
                    + " - " + t.getNoobName() + ChatColor.BLUE + " - "
                    + t.getDescription() + ChatColor.GREEN + " - X:" + t.getX()
                    + " Y:" + t.getY() + " Z:" + t.getZ() + " World:"
                    + t.getWorldName());
        } else if (t.getStatus().equals(Status.CLOSED) && show_closed) {
            sender.sendMessage(ChatColor.DARK_RED + "" + t.getID()
                    + ChatColor.GOLD + " - " + t.getNoobName()
                    + ChatColor.DARK_RED + " - " + t.getDescription()
                    + ChatColor.GREEN + " - X:" + t.getX() + " Y:" + t.getY()
                    + " Z:" + t.getZ() + " World:" + t.getWorldName());
        } else {
            sender.sendMessage(ChatColor.RED
                    + "That ticket is closed. If you wish to check a closed ticket add -closed to the end of the command.");
        }
    }

    @Override
    public boolean execute(final CommandSender sender, String[] split) {
        boolean match = false;
        show_closed = false;
        for (final String s : split) {
            if (s.equalsIgnoreCase("-closed")) {
                show_closed = true;
                break;
            }
        }
        if (show_closed) {
            final String[] temp = new String[split.length - 1];
            int i = 0;
            for (final String s : split) {
                if (s.equalsIgnoreCase("-closed")) {
                    continue;
                }
                temp[i] = s;
                i++;
            }
            split = temp;
        }
        tickets = this.getTickets();
        sender.sendMessage(ChatColor.RED + "Listing support tickets.");
        sender.sendMessage(ChatColor.GREEN + "Green" + ChatColor.RED
                + " are open support tickets!");
        sender.sendMessage(ChatColor.BLUE + "Blue" + ChatColor.RED
                + " are claimed support tickets that are not closed!");
        if (show_closed) {
            sender.sendMessage(ChatColor.DARK_RED + "Red" + ChatColor.RED
                    + " are tickets that have been closed!");
        }
        sender.sendMessage(ChatColor.RED
                + "-----------------------------------------------");
        if (split.length == 1) {
            match = true;
            this.displayPage(1, sender);
        } else if (split.length == 3) {
            match = true;
            if (split[1].equalsIgnoreCase("page")) {
                if (!Configuration.canParse(split[2])) {
                    sender.sendMessage(ChatColor.RED + "Invalid page number!");
                    return true;
                }
                if (tickets.size() / 15 + 1 < Integer.parseInt(split[2])) {
                    sender.sendMessage(ChatColor.RED + "Invalid page number!");
                    return true;
                }
                if (Integer.parseInt(split[2]) < 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid page number!");
                    return true;
                }
                final int pg = Integer.parseInt(split[2]);
                this.displayPage(pg, sender);
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid command usage!");
                return true;
            }
        } else if (split.length == 2) {
            if (!Configuration.canParse(split[1])) {
                sender.sendMessage(ChatColor.RED + "Invalid number!");
                return true;
            }
            final int id = Check.instance.getNextTicketID();
            if (id <= Integer.parseInt(split[1])
                    || Integer.parseInt(split[1]) < 1) {
                sender.sendMessage(ChatColor.RED + "Invalid ticket number!");
                return true;
            }
            final int ticketID = Integer.parseInt(split[1]);
            for (final HelpTicket t : tickets) {
                if (t.getID() == ticketID) {
                    this.displayTicket(t, sender);
                    match = true;
                    break;
                }
            }
        }
        if (!match) {
            sender.sendMessage(ChatColor.RED
                    + "That ticket is closed. If you wish to check a closed ticket add -closed to the end of the command.");
        }
        tickets.clear();
        return true;
    }

    @Override
    public String getPermission() {
        return "monstertickets.check";
    }

    private ArrayList<HelpTicket> getTickets() {
        final ArrayList<HelpTicket> tickets = new ArrayList<HelpTicket>();
        if (!Variables.useMYSQLBackend) {
            tickets.addAll(Variables.tickets);
            return tickets;
        } else {
            try {
                tickets.addAll(Check.instance.getMySQL().readTickets(1));
                tickets.addAll(Check.instance.getMySQL().readTickets(2));
                if (show_closed) {
                    tickets.addAll(Check.instance.getMySQL().readTickets(3));
                }
            } catch (final SQLException e) {
                tickets.addAll(Variables.tickets);
                return tickets;
            }
            Collections.sort(tickets, (t1, t2) -> t1.getID() - t2.getID());
            return tickets;
        }
    }

}
