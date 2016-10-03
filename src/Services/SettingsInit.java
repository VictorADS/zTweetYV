package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class SettingsInit {
	public static JSONObject getInformation(String key){
		JSONObject jb=null;
		Connection connection=null;
		ResultSet user=null;
		Statement st=null;
		long iskeyexpired=0;
		if(key=="" || key==null){
			return ServiceTools.serviceRefused("Cle non fournie", -1);
		}else{
			try{
				//Connection
				connection=DBStatic.getMyConnection();
				st=DBStatic.connexionBD(connection);

				//On recupere l'user 
				user=RequeteStatic.getUserWithKey(key,st);
				iskeyexpired=RequeteStatic.isKeyTimeValide(user);
				if(iskeyexpired==1){
					return ServiceTools.serviceRefused("Cle perime, veuillez vous reconnecter", 1000);
				}
				
				if(iskeyexpired==-1){
					return ServiceTools.serviceRefused("Utilisateur non connecte", 1000);					 //->
				}
				if(iskeyexpired==0){
					String login=user.getString("login");
					user.close();
					user=RequeteStatic.getUserWithLogin(login, st);
					if(user.first()){
						String mail=user.getString("email");
						int id=user.getInt("id");
						jb=ServiceTools.serviceAccepted("Reussite");
						jb.put("mail",mail);
						jb.put("login",login);
						jb.put("key",key);
						jb.put("id",id);
					}else{
						return ServiceTools.serviceRefused("Utilisateur n'existe pas", 1000);
					}
				}
				
			}catch (Exception e) {
				jb=ServiceTools.GestionDesErreur(e);
			}finally{
				try {
					DBStatic.deconnectionBD(st, connection, user);
				} catch (SQLException e) {
					jb=ServiceTools.GestionDesErreur(e);
				}
			}
		}
		return jb;
	}
}

