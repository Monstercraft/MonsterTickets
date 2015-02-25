package org.monstercraft.support.plugin.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.monstercraft.support.MonsterTickets;
import org.monstercraft.support.plugin.Configuration;
import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.command.commands.Close;
import org.monstercraft.support.plugin.util.Status;
import org.monstercraft.support.plugin.wrappers.HelpTicket;

/**
 * This class contains all of the plugins settings.
 *
 * @author fletch_to_99 <fletchto99@hotmail.com>
 *
 */
public class SettingsManager {
    private MonsterTickets plugin = null;

    private String SETTINGS_PATH = null;

    private final String SETTINGS_FILE = "Config.yml";

    /**
     * Creates an instance of the Settings class.
     *
     * @param plugin
     *            The parent plugin.
     * @throws InvalidConfigurationException
     * @throws IOException
     * @throws FileNotFoundException
     */
    public SettingsManager(final MonsterTickets plugin) {
        this.plugin = plugin;
        SETTINGS_PATH = plugin.getDataFolder().getAbsolutePath();
        this.load();
    }

    /**
     * This method loads the plugins configuration file.
     */
    public void load() {
        final FileConfiguration config = plugin.getConfig();
        final File CONFIGURATION_FILE = new File(SETTINGS_PATH, SETTINGS_FILE);
        final boolean exists = CONFIGURATION_FILE.exists();
        if (exists) {
            try {
                Configuration.log("Loading settings!");
                config.options()
                        .header("MonsterTickets configuration file, refer to our DBO page for help.");
                config.load(CONFIGURATION_FILE);
            } catch (final Exception e) {
                Configuration.debug(e);
            }
        } else {
            Configuration.log("Loading default settings!");
            config.options()
                    .header("MonsterTickets configuration file, refer to our DBO page for help.");
            config.options().copyDefaults(true);
        }
        try {
            Variables.overridehelp = config.getBoolean(
                    "MONSTERTICKETS.OPTIONS.OVERRIDE_HELP_COMMAND",
                    Variables.overridehelp);
            Variables.useMYSQLBackend = config.getBoolean(
                    "MONSTERTICKETS.OPTIONS.USE_MYSQL_BACKEND",
                    Variables.useMYSQLBackend);
            Variables.db_host = config.getString("MONSTERTICKETS.DB.HOST");
            Variables.db_username = config
                    .getString("MONSTERTICKETS.DB.USERNAME");
            Variables.db_password = config
                    .getString("MONSTERTICKETS.DB.PASSWORD");
            Variables.db_name = config.getString("MONSTERTICKETS.DB.DATABASE");
            this.save(config, CONFIGURATION_FILE);
        } catch (final Exception e) {
            Configuration.debug(e);
        }
    }

    public void loadTickets() {
        final File TICKETS_FILE = new File(SETTINGS_PATH, "Tickets.dat");
        if (!TICKETS_FILE.exists()) {
            return;
        }
        List<String> tickets = new ArrayList<String>();
        final FileConfiguration config = new YamlConfiguration();
        try {
            config.load(TICKETS_FILE);
        } catch (final Exception e) {
            Configuration.log("Error loading cached tickets!");
            final File back = new File(SETTINGS_PATH, "OLD_Tickets.dat");
            if (back.exists()) {
                back.delete();// prevent errors when saving later
            }
            TICKETS_FILE.renameTo(back); // prevent errors when saving
            return;
        }
        config.options().header("DO NOT MODIFY");
        tickets = config.getStringList("TICKETS");
        if (!tickets.isEmpty()) {
            int valid = 0;
            for (final String str : tickets) {
                if (str.contains("|")) {
                    int count = 0;
                    for (final char c : str.toCharArray()) {
                        if (c == '|') {
                            count++;
                        }
                    }
                    if (count == 7) {
                        valid++;
                        final int idx1 = str.indexOf("|");
                        final int idx2 = str.indexOf("|", idx1 + 1);
                        final int idx3 = str.indexOf("|", idx2 + 1);
                        final int idx4 = str.indexOf("|", idx3 + 1);
                        final int idx5 = str.indexOf("|", idx4 + 1);
                        final int idx6 = str.indexOf("|", idx5 + 1);
                        final int idx7 = str.indexOf("|", idx6 + 1);
                        final int id = Integer.parseInt(str.substring(0, idx1));
                        final String player = str.substring(idx1 + 1, idx2);
                        final String description = str
                                .substring(idx2 + 1, idx3);
                        final String modname = str.substring(idx3 + 1, idx4);
                        final int x = Integer.parseInt(str.substring(idx4 + 1,
                                idx5));
                        final int y = Integer.parseInt(str.substring(idx5 + 1,
                                idx6));
                        final int z = Integer.parseInt(str.substring(idx6 + 1,
                                idx7));
                        final String world = str.substring(idx7 + 1).trim();
                        final HelpTicket t = new HelpTicket(id, description,
                                player, x, y, z, world);
                        Variables.tickets.add(t);
                        if (!modname.equalsIgnoreCase("nullnullnullnullnull")) {
                            Variables.tickets.getLast().Claim(modname);
                            Variables.tickets.getLast().close();
                        }
                    }
                }
            }
            if (valid == 0) {
                final File OLD_TICKETS_FILE = new File(SETTINGS_PATH,
                        "LEGACY_Tickets.txt");
                TICKETS_FILE.renameTo(OLD_TICKETS_FILE); // old file
            }
        }
    }

    /**
     * This method loads the plugins configuration file.
     */
    public void save() {
        final FileConfiguration config = plugin.getConfig();
        final File CONFIGURATION_FILE = new File(SETTINGS_PATH + File.separator
                + SETTINGS_FILE);
        try {
            config.set("MONSTERTICKETS.OPTIONS.OVERRIDE_HELP_COMMAND",
                    Variables.overridehelp);
            config.set("MONSTERTICKETS.OPTIONS.USE_MYSQL_BACKEND",
                    Variables.useMYSQLBackend);
            config.set("MONSTERTICKETS.DB.HOST", Variables.db_host);
            config.set("MONSTERTICKETS.DB.USERNAME", Variables.db_username);
            config.set("MONSTERTICKETS.DB.PASSWORD", Variables.db_password);
            config.set("MONSTERTICKETS.DB.DATABASE", Variables.db_name);
            this.save(config, CONFIGURATION_FILE);
        } catch (final Exception e) {
            Configuration.debug(e);
        }
    }

    private void save(final FileConfiguration config, final File file) {
        try {
            config.save(file);
        } catch (final IOException e) {
            Configuration.debug(e);
        }
    }

    public void saveTicketsConfig() {
        final File TICKETS_FILE = new File(SETTINGS_PATH + File.separator
                + "Tickets.dat");
        TICKETS_FILE.delete();
        final ArrayList<String> tickets = new ArrayList<String>();
        final FileConfiguration config = new YamlConfiguration();
        config.options().header("DO NOT MODIFY");
        for (final HelpTicket t : Variables.tickets) {
            if (t.getStatus().equals(Status.CLAIMED)) {
                Close.close(t.getModName());
            }
            tickets.add(t.getID()
                    + "|"
                    + t.getNoobName()
                    + "|"
                    + t.getDescription()
                    + "|"
                    + (t.getStatus().equals(Status.CLOSED) ? t.getModName()
                            : "nullnullnullnullnull") + "|" + t.getX() + "|"
                    + t.getY() + "|" + t.getZ() + "|" + t.getWorldName());

        }
        if (!tickets.isEmpty()) {
            config.set("TICKETS", tickets);
        } else {
            return; // prevent creating an enpty file. Not needed
        }
        this.save(config, TICKETS_FILE);
    }
}
