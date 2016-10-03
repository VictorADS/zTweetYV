package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;
import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class LogoutService {
	public static JSONObject logOut(String cle){
		Connection c=null;
		ResultSet r=null;
		Statement st=null;
		JSONObject jb=new JSONObject();
		if(cle ==null)
			return ServiceTools.serviceRefused("cle non valide", -1);
		else{
			//INIT
			try{
				c=DBStatic.getMyConnection();
				st=DBStatic.connexionBD(c);
				r=RequeteStatic.getUserWithKey(cle, st);
				if(r.first()){
					st.executeUpdate("DELETE FROM session where clef='"+cle+"'");
					jb=ServiceTools.serviceAccepted("User is now disconnected");

				}else{
					jb=ServiceTools.serviceRefused("Cle perime",1000);
				}
			}catch (Exception e) {
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