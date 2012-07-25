package org.monstercraft.support.plugin.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.support.plugin.util.Status;

public class HelpTicket {

	private final int id;

	private Status status;

	private final String description;

	private final String noobname;

	private String modname;

	public HelpTicket(final int id, final String description,
			final String player) {
		this.id = id;
		this.description = description;
		this.status = Status.OPEN;
		this.noobname = player;
	}

	public String getDescription() {
		return description;
	}

	public int getID() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	public Player getNoob() {
		return Bukkit.getPlayer(getNoobName());
	}

	public String getNoobName() {
		return noobname;
	}

	public Player getMod() {
		return Bukkit.getPlayer(getModName());
	}

	public String getModName() {
		return modname;
	}
	
	public void setStatus(int id) {
		for(Status status : Status.values()) {
			if(status.toInt() == id) {
				this.status = status;
				return;
			}
		}
	}

	public void Claim(String modname) {
		this.modname = modname;
		this.status = Status.CLAIMED;
	}

	public void close() {
		this.status = Status.CLOSED;
	}

}
