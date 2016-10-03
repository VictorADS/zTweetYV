package BdTools;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class DBStatic {
	private static final String mysql_host = "132.227.201.129:33306";
	private static final String mysql_db = "gr1_dasi_bouk";
	private static final String mysql_username = "gr1_dasi_bouk";
	private static final String mysql_password = "gr1_dasi_bouk$";
	private static final boolean mysql_pooling = false;
	private static DataBase database;

	public static Connection getMyConnection() throws SQLException,InstantiationException,IllegalAccessException,ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		if (!DBStatic.mysql_pooling) {
			return (DriverManager.getConnection("jdbc:mysql://"+DBStatic.mysql_host+"/"+DBStatic.mysql_db,DBStatic.mysql_username,DBStatic.mysql_password));
		} else {
			if (database == null) {
				database = new DataBase("jdbc/db");
			}
			return (database.getConnection());
		}

	}
	public static Statement connexionBD(Connection c) throws InstantiationException,
	IllegalAccessException, ClassNotFoundException, SQLException {
		return  c.createStatement();
	}
	
	
	public static DBCollection getMongoConnection() throws UnknownHostException, MongoException{
		Mongo m=new Mongo("132.227.201.129",27130);
		DB db=m.getDB("dasi_bouk");
		return db.getCollection("Comments");
	}
	
	public static void add2logs(String adrip,String service){
		Mongo m;
		try {
			m = new Mongo("132.227.201.129",27130);
			DB db=m.getDB("dasi_bouk");
			DBCollection col=db.getCollection("Logs");
			BasicDBObject doublon=new BasicDBObject();
			Date now=new Date();
			doublon.put("Log", "Lip "+adrip+" accede au service "+service+" a "+now );
			col.insert(doublon);
		} catch (Exception e){
		}
	}
	public static void deconnectionBD(Statement st, Connection c,ResultSet r)throws SQLException {
		if (st != null)
			st.close();
		if (c != null)
			c.close();
		if(r!=null)
			r.close();
	}

}
