package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;
import BdTools.DBStatic;
import BdTools.RequeteStatic;
import com.mongodb.DBCollection;

public class AddComment {
	public static JSONObject add(String cle,String text){
		JSONObject jb=null;
		Connection c=null;
		ResultSet r=null;
		Statement st=null;
		DBCollection col=null;

		if(cle==null){
			jb=ServiceTools.serviceRefused("Cle non fournie", -1);
			return jb;
		}
		if(text.length()>140){
			jb=ServiceTools.serviceRefused("Tweet trop long (140 caracteres maximum)", -1);
			return jb;
		}
		if(text.length()<1){
			jb=ServiceTools.serviceRefused("Tweet trop court (1 caractere minimum)", -1);
			return jb;
		}


		//Traitement add comment
		try{
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			r=RequeteStatic.getUserWithKey(cle, st);
			long keyexpired=RequeteStatic.isKeyTimeValide(r);

			if(keyexpired==-1){
				jb=ServiceTools.serviceRefused("Utilisateur deconnecte, veuillez vous reconnectez", 1000);
			}
			if(keyexpired==1){
				jb=ServiceTools.serviceRefused("Session expire veuillez vous reconnectez", -1);
				return jb;
			}
			if(keyexpired==0){
				col=DBStatic.getMongoConnection();
				String auteur=r.getString("login");
				int idauteur=r.getInt("idlogin");

				return jb=RequeteStatic.addTweet(col, text, auteur,idauteur);
			}
		}catch(Exception e){
			jb=ServiceTools.GestionDesErreur(e);
		}finally{
			try {
				DBStatic.deconnectionBD(st, c, r);
			} catch (SQLException e) {
				jb=ServiceTools.GestionDesErreur(e);
			}
		}
		return jb;
	}
}