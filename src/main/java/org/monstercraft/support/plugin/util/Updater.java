package org.monstercraft.support.plugin.util;

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.monstercraft.support.plugin.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Updater implements Runnable {
    private final String version;

    public Updater(final String version) {
        this.version = version;
    }

    @Override
    public void run() {
        try {
            final URL url = new URL(
                    "http://dev.bukkit.org/server-mods/monstertickets/files.rss");
            final Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            final NodeList nodes = doc.getElementsByTagName("item");
            final Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                final Element firstElement = (Element) firstNode;
                final NodeList firstElementTagName = firstElement
                        .getElementsByTagName("title");
                final Element firstNameElement = (Element) firstElementTagName
                        .item(0);
                final NodeList firstNodes = firstNameElement.getChildNodes();
                final String cur = firstNodes.item(0).getNodeValue();
                if (cur.contains(version)) {
                    Configuration
                            .log("You are using the latest version of MonsterTickets");
                } else {
                    Configuration.log(cur + " is out! You are running "
                            + version);
                    Configuration
                            .log("Update MonsterMOTD at: http://dev.bukkit.org/server-mods/monstermotd");
                }
            }
        } catch (final Exception e) {
            Configuration.debug(e);
        }

    }
}
