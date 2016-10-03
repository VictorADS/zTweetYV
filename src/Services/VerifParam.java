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

public class VerifParam {
	public static JSONObject verif(String login,String mail,String mdp,String loginuser){
		JSONObject jb=null;
		if(login==null && mdp==null && mail !=null){
			jb=verifMail(mail);
		}
		if(mail==null && mdp==null && login!=null){
			jb=verifLogin(login);
		}
		if(mail !=null && mdp!=null && login !=null){
			jb=updateParam(mail,login,mdp,loginuser);
		}
		return jb;
	}

	public static JSONObject verifMail(String mail){
		JSONObject jb=new JSONObject();
		Connection c=null;
		ResultSet r=null;
		Statement st=null;

		try{
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			r=RequeteStatic.GetMail(mail, st);
			if(r.first()){
				return ServiceTools.serviceRefused("Mail existe deja", -1);
			}else{
				return ServiceTools.serviceAccepted("Valide");
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

	public static JSONObject verifLogin(String login){
		JSONObject jb=new JSONObject();
		Connection c=null;
		ResultSet r=null;
		Statement st=null;

		try{
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			r=RequeteStatic.getUserWithLogin(login, st);
			if(r.first()){
				return ServiceTools.serviceRefused("Login existe deja", -1);
			}else{
				return ServiceTools.serviceAccepted("Valide");
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

	public static JSONObject updateParam(String mail,String login,String mdp,String loginuser){
		JSONObject jb=new JSONObject();
		Connection c=null;
		ResultSet rmain=null;

		Statement st=null;

		try{
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			rmain=RequeteStatic.getUserWithLogin(loginuser, st);
			if(rmain.first()){
				String lastmail=rmain.getString("email");
				String lastmdp=rmain.getString("passwd");
				String query="UPDATE user set ";
				if(login!=loginuser){
					query+="login='"+login+"'";
					DBCollection col=DBStatic.getMongoConnection();
					BasicDBObject search=new BasicDBObject();
					search.put("auteur.login",loginuser);
					BasicDBObject replace=new BasicDBObject();
					replace.put("$set",new BasicDBObject("auteur.login",login));
					col.update(search, replace,false,true);
				}
				if(login !=loginuser && mail !=lastmail){
					query+=",";
				}
				if(mail!=lastmail){
					query+="email='"+mail+"'";
				}
				if(mdp.length()>=6){
					if((mail!=lastmail && lastmdp!=mdp) || (lastmdp!=mdp && login!=loginuser)){
						query+=",";
					}
					if(lastmdp!=mdp){
						query+="passwd='"+mdp+"'";
					}
				}
				query+=" where login='"+loginuser+"'";
				st.executeUpdate(query);

				jb=ServiceTools.serviceAccepted("Reussi");
			}else{
				jb=ServiceTools.serviceRefused("Utilisateur nexiste pas", -1);
			}
		} catch (Exception e) {
			jb=ServiceTools.GestionDesErreur(e);

		}finally{
			try {
				DBStatic.deconnectionBD(st, c, rmain);
			} catch (SQLException e) {
				jb=ServiceTools.GestionDesErreur(e);
			}
		}
		return jb;
	}
}
