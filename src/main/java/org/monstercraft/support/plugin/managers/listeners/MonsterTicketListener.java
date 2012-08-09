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
import org.monstercraft.support.plugin.command.commands.Check;
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

	private final MonsterTickets plugin;

	/**
	 * 
	 * @param plugin
	 *            The parent plugin for the listener.
	 */
	public MonsterTicketListener(MonsterTickets plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(PlayerChatEvent event) {
		for (HelpTicket t : Variables.tickets) {
			Player mod = Bukkit.getPlayer(t.getModName());
			Player noob = Bukkit.getPlayer(t.getNoobName());
			if (mod == null || noob == null
					|| t.getStatus().equals(Status.CLOSED)) {
				continue;
			}
			if (t.getStatus().equals(Status.CLAIMED)) {
				if (noob.equals(event.getPlayer())
						|| mod.equals(event.getPlayer())) {
					noob.sendMessage(ChatColor.RED + "[Support] "
							+ event.getPlayer().getDisplayName() + ": "
							+ ChatColor.WHITE + event.getMessage());
					mod.sendMessage(ChatColor.RED + "[Support] "
							+ event.getPlayer().getDisplayName() + ": "
							+ ChatColor.WHITE + event.getMessage());
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (plugin.getPermissionsHandler().hasNode(pl,
								"monstertickets.mod.spy")
								&& pl != mod && pl != noob) {
							if (Variables.nospy.contains(pl)) {
								continue;
							}
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
		if (Variables.adminchat.contains(event.getPlayer())) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (plugin.getPermissionsHandler().hasNode(p,
						"monstertickets.adminchat")) {
					p.sendMessage(ChatColor.RED + "[Admin Chat]"
							+ event.getPlayer().getName() + ": "
							+ ChatColor.RESET + event.getMessage());
				}
			}
			event.setCancelled(true);
			return;
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
		if (plugin.getPermissionsHandler().hasCommandPerms(event.getPlayer(),
				new Check(plugin))) {
			int count = 0;
			for (HelpTicket t : Variables.tickets) {
				if (!t.getStatus().equals(Status.OPEN)) {
					continue;
				}
				count++;
			}
			event.getPlayer().sendMessage(
					ChatColor.GREEN + "There are " + count
							+ " open support tickets.");
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
		if (msg.startsWith("/ac") && msg.length() < 4) {
			event.setMessage("/mt toggle ac");
		}

		if (msg.startsWith("/adminchat") && msg.length() < 11) {
			event.setMessage("/mt toggle ac");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (Variables.nospy.contains(event.getPlayer())) {
			Variables.nospy.remove(event.getPlayer());
		}
		if (Variables.adminchat.contains(event.getPlayer())) {
			Variables.adminchat.remove(event.getPlayer());
		}
		for (HelpTicket t : Variables.tickets) {
			if (t.getModName().equalsIgnoreCase(event.getPlayer().getName())
					|| t.getNoobName().equalsIgnoreCase(
							event.getPlayer().getName())) {
				if (t.getStatus().equals(Status.CLAIMED)) {
					Close.close(t.getModName());
					return;
				}
			}
		}
	}
}