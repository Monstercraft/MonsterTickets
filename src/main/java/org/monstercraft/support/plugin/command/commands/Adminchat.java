package org.monstercraft.support.plugin.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.command.GameCommand;
import org.monstercraft.support.plugin.events.AdminChatEvent;

public class Adminchat extends GameCommand {

    private static MonsterTickets instance;

    public Adminchat(final MonsterTickets instance) {
        Adminchat.instance = instance;
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split.length > 1
                && (split[0].equalsIgnoreCase("adminchat") || split[0]
                        .equalsIgnoreCase("ac"));
    }

    @Override
    public boolean execute(final CommandSender sender, String[] split) {
        final String[] temp = new String[split.length - 1];
        for (int i = 1; i < split.length; i++) {
            temp[i - 1] = split[i];
        }
        split = temp;
        final StringBuilder sb = new StringBuilder();
        for (final String s : split) {
            sb.append(s);
            sb.append(" ");
        }
        Bukkit.getPluginManager().callEvent(
                new AdminChatEvent(sender.getName(), sb.toString().trim()));
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (Adminchat.instance.getPermissionsHandler().hasNode(p,
                    "monstertickets.adminchat")) {
                p.sendMessage(ChatColor.RED + "[Admin Chat]" + sender.getName()
                        + ": " + ChatColor.RESET + sb.toString().trim());
            }
        }
        return true;
    }

    @Override
    public String getPermission() {
        return "monstertickets.adminchat";
    }
}