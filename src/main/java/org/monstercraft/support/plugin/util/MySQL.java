package org.monstercraft.support.plugin.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.monstercraft.support.plugin.Configuration.Variables;
import org.monstercraft.support.plugin.wrappers.HelpTicket;

public class MySQL {

	Connection connection;

	public MySQL() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://"
				+ Variables.db_host + ":3306/" + Variables.db_name
				+ "?autoReconnect=true&user=" + Variables.db_username
				+ "&password=" + Variables.db_password);
		final ResultSet tableExists = connection.getMetaData().getTables(null,
				null, "tickets", null);
		if (!tableExists.first()) {
			String tableCreation = "CREATE TABLE IF NOT EXISTS `tickets` ("
					+ "  `id` int(11) NOT NULL AUTO_INCREMENT,"
					+ "  `noob` varchar(16) NOT NULL,"
					+ "  `description` text NOT NULL,"
					+ "  `mod` varchar(16) NOT NULL,"
					+ "  `status` tinyint(1) NOT NULL,"
					+ "  PRIMARY KEY (`id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;";
			this.connection.createStatement().executeUpdate(tableCreation);
		}
	}
	
	public ArrayList<HelpTicket> readTickets(boolean returnClosed) throws SQLException {
		PreparedStatement ps = this.connection
				.prepareStatement("SELECT * FROM tickets WHERE status != 3");
		if(returnClosed){
		ps = this.connection
				.prepareStatement("SELECT * FROM tickets");
		}
		ResultSet rs = ps.executeQuery();
		ArrayList<HelpTicket> tickets = new ArrayList<HelpTicket>();
		while (rs.next()) {
			String noob = rs.getString("noob");
			String description = rs.getString("description");
			int status = rs.getInt("status");
			int id = rs.getInt("id");
			HelpTicket h = new HelpTicket(id, noob, description);
			h.setStatus(status);
			tickets.add(h);
		}
		return tickets;
	}

	public void createTicket(HelpTicket ticket) throws SQLException {
		PreparedStatement ps = this.connection
				.prepareStatement("INSERT INTO `tickets` (noob, description, status) VALUES (?,?,?)");
		ps.setString(1, ticket.getNoobName());
		ps.setString(2, ticket.getDescription());
		ps.setInt(3, ticket.getStatus().toInt());
		ps.executeUpdate();
	}
	
	public void claimTicket(HelpTicket ticket) throws SQLException {
		PreparedStatement ps = this.connection
				.prepareStatement("UPDATE `tickets` SET mod=?,status=? WHERE id=?");
		ps.setString(1, ticket.getModName());
		ps.setInt(2, ticket.getStatus().toInt());
		ps.setInt(3, ticket.getID());
		ps.executeUpdate();
	}
	
	public void closeTicket(HelpTicket ticket) throws SQLException {
		PreparedStatement ps = this.connection
				.prepareStatement("UPDATE `tickets` SET status=? WHERE id=?");
		ps.setInt(1, ticket.getStatus().toInt());
		ps.setInt(2, ticket.getID());
		ps.executeUpdate();
	}
}
