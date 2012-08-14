package org.monstercraft.support.plugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AdminChatEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private String player;

	private String message;

	public AdminChatEvent(String sender, String message) {
		this.player = sender;
		this.message = message;
	}

	public String getSender() {
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
