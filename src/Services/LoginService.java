package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import org.json.JSONObject;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class LoginService {
	public static JSONObject login(String log,String pwd){
		Connection c=null;
		ResultSet r=null;
		Statement st=null;
		long iskeyexpired=0;
		int idlogin=0;
		String clef=ServiceTools.generate();
		Timestamp newtime=ServiceTools.updateKeytime();
		JSONObject jb=new JSONObject();
		if(log==null || pwd==null){
			jb=ServiceTools.serviceRefused("", -1);
		}else{
			try {
				//INIT
				c=DBStatic.getMyConnection();
				st=DBStatic.connexionBD(c);
				
				//VERIF VALIDITE
				r=RequeteStatic.VerifySessionOpen(log, pwd, st);
				iskeyexpired=RequeteStatic.isKeyTimeValide(r);
				if(iskeyexpired==0){
					jb= ServiceTools.serviceAccepted("User "+log+" is already connected");
					idlogin=r.getInt("idlogin");
					clef=r.getString("clef");
					jb.put("key",clef);
					jb.put("id",idlogin);
					jb.put("login",log);
				}else if(iskeyexpired==1){
					idlogin=r.getInt("idlogin");
					st.executeUpdate("update session set date_exp='"+newtime+"', clef='"+clef+"' where login='"+log+"'");
					jb=ServiceTools.serviceAccepted("User session time has been updated");
					jb.put("key",clef);
					jb.put("id",idlogin);
					jb.put("login",log);
				}else{
					r.close();		//VERIF  PROPRIO
					r=RequeteStatic.VerifyOwner(st, log, pwd);
					if(r.first()){
						jb=ServiceTools.serviceAccepted("You are logged in for 30mn");
						idlogin=r.getInt("id");
						jb.put("key",clef);
						jb.put("id",idlogin);
						jb.put("login",log);
						st.executeUpdate("insert into session values('"+log+"','"+newtime+"','"+clef+"','"+idlogin+"')");
					}else{
						jb=ServiceTools.serviceRefused("Verifiez votre mot de passe ou votre login", -1);
					}
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
