package BdTools;

import java.sql.SQLException;
import java.sql.Connection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;



//creation du driver pour la base de donnés 
public class DataBase {
	private DataSource dataSource;
	
	public DataBase(String jndiname) throws SQLException {
		try {
			dataSource = (DataSource) new InitialContext().lookup("java:comp/env/"+jndiname);
		} catch (NamingException e) {
			// Handle error that it’s not configured in JNDI.
			throw new SQLException(jndiname + " is missing in JNDI! : "+e.getMessage());
		}
	}
	//recuperation de la connection via DBStaticGetMySqlConnection
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}



}
