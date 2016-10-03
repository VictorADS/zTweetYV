package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class DelFriend {

	public static JSONObject del(String logfriend,String cle){
		JSONObject jb=null;
		Connection c=null;
		ResultSet r=null;
		Statement st=null;
		if(logfriend==null ||cle==null){	
			jb=ServiceTools.serviceRefused("", -1);
		}else{
			try {
				//INIT
				c=DBStatic.getMyConnection();
				st=DBStatic.connexionBD(c);
				r=RequeteStatic.getUserWithKey(cle, st);
				long keyexpired=RequeteStatic.isKeyTimeValide(r);
				String logme=r.getString("login");

				if(keyexpired==-1){
					jb=ServiceTools.serviceRefused("Utilisateur deconnecte, veuillez vous reconnectez",1000);
					return jb;
				}

				if(keyexpired==1){
					jb=ServiceTools.serviceRefused("Cle perime", 1000);
					return jb;
				}

				if(!RequeteStatic.UserExist(logfriend, st)){
					jb=ServiceTools.serviceRefused("L'utilisateur que vous souhaitiez supprimer n'existe pas",-1);
				}

				r.close();
				r=RequeteStatic.SearchFriend(logfriend, logme, st);

				if(r.first()){
					st.executeUpdate("DELETE FROM friend where login_u='"+logme+"' and login_f='"+logfriend+"'");
					jb=ServiceTools.serviceAccepted("Vous n'etes plus amis avec "+logfriend);
				}else{
					jb=ServiceTools.serviceAccepted("Vous ne pouvez pas supprimer "+logfriend+" de vos ami(e) car vous ne l'etes pas");
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

		}
		return jb;

	}
}
