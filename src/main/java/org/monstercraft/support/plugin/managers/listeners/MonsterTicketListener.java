package org.monstercraft.support.plugin.managers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.Configuration;
import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.command.commands.Close;
import org.monstercraft.support.plugin.util.Status;
import org.monstercraft.support.plugin.wrappers.HelpTicket;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class MonsterTicketListener implements Listener {

	/**
	 * 
	 * @param plugin
	 *            The parent plugin for the listener.
	 */
	public MonsterTicketListener() {
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(PlayerChatEvent event) {
		for (HelpTicket t : Variables.tickets) {
			if (t.getMod() == null || t.getNoob() == null
					|| t.getStatus().equals(Status.CLOSED)) {
				Configuration.log(t.getID() + "" + t.getStatus().toString());
				continue;
			}
			if (t.getStatus().equals(Status.CLAIMED)) {
				if (t.getNoob().equals(event.getPlayer())
						|| t.getMod().equals(event.getPlayer())) {
					t.getNoob().sendMessage(
							ChatColor.RED + "[Support] "
									+ event.getPlayer().getDisplayName() + ": "
									+ ChatColor.WHITE + event.getMessage());
					t.getMod().sendMessage(
							ChatColor.RED + "[Support] "
									+ event.getPlayer().getDisplayName() + ": "
									+ ChatColor.WHITE + event.getMessage());
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (MonsterTickets.getPermissionsHandler().hasNode(pl,
								"monstertickets.mod.spy")
								&& pl != t.getMod() && pl != t.getNoob()) {
							pl.sendMessage(ChatColor.DARK_BLUE + "[Spy]"
									+ ChatColor.RED + "[Support] "
									+ event.getPlayer().getDisplayName() + ": "
									+ ChatColor.WHITE + event.getMessage());
						}
					}
				}
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Variables.overridehelp) {
			event.getPlayer().sendMessage(
					ChatColor.RED + "To open a support request type:"
							+ ChatColor.GREEN + " /help (issue)");
		} else {
			event.getPlayer().sendMessage(
					ChatColor.RED + "To open a support request type:"
							+ ChatColor.GREEN + " /request (issue)");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String msg = event.getMessage();
		String[] message = msg.split("\\s+");
		if (msg.startsWith("/help") && msg.length() > 5
				&& Variables.overridehelp) {
			if (message.length == 2 && Configuration.canParse(message[1])) {
				return;
			}
			msg = "/request " + msg.substring(6);
			event.setMessage(msg);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (HelpTicket t : Variables.tickets) {
			if (t.getMod().equals(event.getPlayer())
					|| t.getNoob().equals(event.getPlayer())) {
				if (t.getStatus().equals(Status.CLAIMED)) {
					Close.close(t.getMod());
					return;
				}
			}
		}
	}
}