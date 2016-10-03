package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class AddFriend {

	public static JSONObject add(String logfriend,String cle){
		JSONObject jb=null;
		Connection c=null;
		ResultSet r=null;
		Statement st=null;

		if(logfriend==null){	
			jb=ServiceTools.serviceRefused(" Ami non fourni", -1);
			return jb;
		}
		if(cle==null){
			jb=ServiceTools.serviceRefused(" Clee non fournie", -1);
			return jb;
		}
		try {
			//INIT
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			r=RequeteStatic.getUserWithKey(cle, st);
			long keyexpired=RequeteStatic.isKeyTimeValide(r);
			if(keyexpired==-1){
				jb=ServiceTools.serviceRefused("Utilisateur deconnecte, veuillez vous reconnectez",1000);
				return jb;
			}
			if (keyexpired == 1){
				jb=ServiceTools.serviceRefused("Session expire veuillez vous reconnectez", -1000);
				return jb;
			}
			String logme=r.getString("login");
			if(keyexpired==0){
				if(RequeteStatic.UserExist(logfriend, st)){
					r.close();
					r=RequeteStatic.SearchFriend(logfriend, logme, st);
					if(r.first()){
						jb=ServiceTools.serviceRefused("You are already friend with that person",1000);
					}else{

						st.executeUpdate("insert into friend values('"+logme+"','"+logfriend+"')");
						jb=ServiceTools.serviceAccepted("Vous etes a present ami avec "+logfriend);
					}
				}else{
					jb=ServiceTools.serviceRefused(""+logfriend+" ne correspond a aucun utilisateur",-1);
				}
			}

		} catch (Exception e) {
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
