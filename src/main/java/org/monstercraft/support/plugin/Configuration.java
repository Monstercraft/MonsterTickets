package org.monstercraft.support.plugin;

import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.monstercraft.support.plugin.wrappers.HelpTicket;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class holds all of the configuration data used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Configuration {

	public static String getCurrentVerison(final Plugin plugin) {
		return plugin.getDescription().getVersion();
	}

	/**
	 * Checks to see if the plugin is the latest version. Thanks to vault for
	 * letting me use their code.
	 * 
	 * @param currentVersion
	 *            The version that is currently running.
	 * @return The latest version
	 */
	public static String checkForUpdates(final Plugin plugin, final String site) {
		String currentVersion = getCurrentVerison(plugin);
		try {
			URL url = new URL(site);
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement
						.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName
						.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				return firstNodes.item(0).getNodeValue();
			}
		} catch (Exception e) {
			debug(e);
		}
		return currentVersion;
	}

	public static String shortenString(String str) {
		if (str.length() > 20) {
			str = str.substring(0, 20);
			str = str + "...";
		}
		return str;
	}

	/**
	 * Checks if a string is a valid integer.
	 * 
	 * @param message
	 *            The string to parse.
	 * @return True if the string is a valid number; otherwise false.
	 */
	public static boolean canParse(String message) {
		try {
			Integer.parseInt(message);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Logs a message to the console.
	 * 
	 * @param msg
	 *            The message to print.
	 */
	public static void log(String msg) {
		Bukkit.getLogger().log(Level.INFO, "[MonsterTickets] " + msg);
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	public static void debug(Exception error) {
		Bukkit.getLogger().log(Level.WARNING,
				"[MonsterTickets - Critical error detected!]");
		error.printStackTrace();
	}

	public static class URLS {
		public static String UPDATE_URL = "http://dev.bukkit.org/server-mods/monstertickets/files.rss";
	}

	public static class Variables {

		public static LinkedList<HelpTicket> tickets = new LinkedList<HelpTicket>();
		public static boolean overridehelp = false;
		public static boolean useFileBackend = true;
		public static String db_host;
		public static String db_username;
		public static String db_password;
		public static String db_name;

	}

}
