package org.monstercraft.support.plugin.managers.handlers;

import org.bukkit.entity.Player;
import org.monstercraft.support.plugin.command.GameCommand;

/**
 * This handles all of the plugins permissions.
 *
 * @author fletch_to_99 <fletchto99@hotmail.com>
 *
 */
public class PermissionsHandler {

    /**
     * Checks if the player has access to the command.
     *
     * @param player
     *            The player executing the command.
     * @param command
     *            The command being executed.
     * @return True if the player has permission; otherwise false.
     */
    public boolean hasCommandPerms(final Player player,
            final GameCommand command) {
        return this.hasNode(player, "monstertickets.mod")
                || this.hasNode(player, command.getPermission());
    }

    public boolean hasNode(final Player player, final String node) {
        return player.isOp() || player.hasPermission(node);
    }
}
