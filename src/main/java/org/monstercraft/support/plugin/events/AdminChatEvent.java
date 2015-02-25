package org.monstercraft.support.plugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AdminChatEvent extends Event {

    public static HandlerList getHandlerList() {
        return AdminChatEvent.handlers;
    }

    private static final HandlerList handlers = new HandlerList();

    private final String player;

    private final String message;

    public AdminChatEvent(final String sender, final String message) {
        player = sender;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return AdminChatEvent.handlers;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return player;
    }

}
