package org.monstercraft.support.plugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AdminChatEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;

	private String message;

	public AdminChatEvent(Player player, String message) {
		this.player = player;
		this.message = message;
	}

	public Player getSender() {
		return player;
	}

	public String getMessage() {
		return message;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
