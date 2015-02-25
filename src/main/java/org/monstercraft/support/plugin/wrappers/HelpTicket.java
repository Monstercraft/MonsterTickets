package org.monstercraft.support.plugin.wrappers;

import org.bukkit.Location;
import org.monstercraft.support.plugin.util.Status;

public class HelpTicket {

    private final int id;

    private Status status;

    private final String description;

    private final String noobname;

    private String modname;

    private final int x;

    private final int y;

    private final int z;

    private final String worldname;

    public HelpTicket(final int id, final String description,
            final String player, final int x, final int y, final int z,
            final String worldname) {
        this.id = id;
        this.description = description;
        status = Status.OPEN;
        noobname = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldname = worldname;
        modname = "";
    }

    public HelpTicket(final int id, final String description,
            final String player, final Location location) {
        this.id = id;
        this.description = description;
        status = Status.OPEN;
        noobname = player;
        x = (int) location.getX();
        y = (int) location.getY();
        z = (int) location.getZ();
        worldname = location.getWorld().getName();
        modname = "";
    }

    public void Claim(final String modname) {
        this.modname = modname;
        status = Status.CLAIMED;
    }

    public void close() {
        status = Status.CLOSED;
    }

    public String getDescription() {
        return description.trim();
    }

    public int getID() {
        return id;
    }

    public String getModName() {
        return modname.trim();
    }

    public String getNoobName() {
        return noobname.trim();
    }

    public Status getStatus() {
        return status;
    }

    public String getWorldName() {
        return worldname.trim();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
