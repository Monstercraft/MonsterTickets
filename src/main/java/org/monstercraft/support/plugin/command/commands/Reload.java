package org.monstercraft.support.plugin.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.command.GameCommand;

public class Reload extends GameCommand {

    private static MonsterTickets instance;

    public Reload(final MonsterTickets instance) {
        Reload.instance = instance;
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split.length > 1 && split[0].equalsIgnoreCase("mt")
                && split[1].equalsIgnoreCase("reload");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        Reload.instance.getSettingsManager().load();
        sender.sendMessage(ChatColor.GREEN
                + "Successfully reloaded plugin settings!");
        return true;
    }

    @Override
    public String getPermission() {
        return "monstertickets.reload";
    }

}
