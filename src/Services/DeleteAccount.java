package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class DeleteAccount {
	public static JSONObject supp(String login) {
		JSONObject jb=new JSONObject();
		Connection c = null;
		ResultSet r = null;
		Statement st = null;
		if(login==null || login==""){
			jb=ServiceTools.serviceRefused("Login non fourni", -1);
		}else{
			try{
				
				c = DBStatic.getMyConnection();
				st = DBStatic.connexionBD(c);

				// TEST SI LOGIN EXISTANT ?
				r = RequeteStatic.getUserWithLogin(login, st);
				if(r.first()){
					st.executeUpdate("DELETE FROM user WHERE login='"+login+"'");
					DBCollection col=DBStatic.getMongoConnection();
					BasicDBObject query=new BasicDBObject();
					query.put("auteur.login",login);
					col.remove(query);
					jb=ServiceTools.serviceAccepted("Suppresion reussi");
				}else{
					jb=ServiceTools.serviceRefused("Utilisateur n'existe pas", -1);
				}
			}catch (Exception e) {
				jb=ServiceTools.GestionDesErreur(e);
			} finally {
				try {
					DBStatic.deconnectionBD(st, c, r);

				} catch (SQLException e) {
					jb=ServiceTools.GestionDesErreur(e);
				}
			}
		}
		return jb;
		
	}

}
