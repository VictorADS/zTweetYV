package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class createUser {
	public static JSONObject create(String login, String mdp, String mail,String nom,String prenom) {
		JSONObject reponse = new JSONObject();
		Connection c = null;
		ResultSet r = null,r2=null;
		Statement st = null;

		// VERIFICATIONS
		if (login == null || mdp == null || mail == null || nom==null || prenom==null ) {
			reponse = ServiceTools.serviceRefused("", -1);
			return reponse;
		}
		if (mdp.length() < 6) {
			reponse = ServiceTools.serviceRefused("Mot de passe trop court", -1);
			return reponse;
		} 

		try{
			// INITIALISATION BD
			c = DBStatic.getMyConnection();
			st = DBStatic.connexionBD(c);

			// TEST SI LOGIN EXISTANT ?
			r = RequeteStatic.getUserWithLogin(login, st);
			if (r.first()){
				reponse = ServiceTools.serviceRefused("Ce login est deja utilise", 1000);
				return reponse;
			}
			
			// TEST SI MAIL EXISTANT ?
			r2=RequeteStatic.GetMail(mail, st);
			if(r2.first()){
				reponse = ServiceTools.serviceRefused("Un compte avec ce mail existe deja", 1000);
				return reponse;
			}
			
			RequeteStatic.putUser(st,login,mdp,mail,prenom,nom);
			SendEmail.sendMessageBienvenue(mail,nom,prenom,mdp,login);
			reponse = ServiceTools.serviceAccepted("User created");

		} catch (Exception e) {
			reponse=ServiceTools.GestionDesErreur(e);
		} finally {
			try {
				DBStatic.deconnectionBD(st, c, r);
				if(r2!=null)
					r2.close();
			} catch (SQLException e) {
				reponse=ServiceTools.GestionDesErreur(e);
			}
		}
		return reponse;
	}
}
