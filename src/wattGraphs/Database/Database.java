package wattGraphs.Database;

import java.sql.*;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project java-seriele-poort
 * @since 15-05-15
 */
public class Database {
	private Connection connection = null;

	public Database(String host, String username, String password, String database) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?user=" + username + "&password=" + password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int insert(String sql) throws SQLException{
		PreparedStatement pst =  connection.prepareStatement(sql);
		return pst.executeUpdate(sql);
	}
}