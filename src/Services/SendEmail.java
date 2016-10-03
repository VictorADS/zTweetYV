package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

import BdTools.DBStatic;

public class SendEmail {

	public static void sendMessageBienvenue(String mail,String nom,String prenom,String password,String log){

		try {
			Properties properties=new Properties();
			Session session = Session.getInstance(properties);
			Message message=new MimeMessage(session);
			InternetAddress destinataire=new InternetAddress(mail);
			message.setRecipient(Message.RecipientType.TO, destinataire);
			message.setSubject("Bienvenue sur TalkMe");
			message.setText("Bonjour "+prenom+" "+nom+",\nNous sommes tres heureux de vous voir rejoindre TalkMe.\nNous vous rappelons que vos identifiants sont \n	Login : "+log+"\n	Mot de passe : "+password+"\nTachez de ne pas les perdres. A bientot.");
			Transport.send(message);
		}catch (MessagingException e) {
			e.printStackTrace();
		}

	}
	public static JSONObject sendMessage(String mail){
		Connection c=null;
		ResultSet r=null;
		Statement st=null;
		JSONObject jb=new JSONObject();
		if(mail==null)
			jb=ServiceTools.serviceRefused(" mail non renseigne", -1);
		try {
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			r=st.executeQuery("SELECT * from user u where u.email=\""+mail+"\"");
			if(r.first()){
				String mdp=r.getString("passwd");
				Properties properties=new Properties();
				Session session = Session.getInstance(properties);
				Message message=new MimeMessage(session);
				InternetAddress destinataire=new InternetAddress(mail);
				message.setRecipient(Message.RecipientType.TO, destinataire);
				message.setSubject("Mot de passe Oublié - TalkMe");
				message.setText("Bonjour,\nVoici votre mot de passe : "+mdp+"\nEssayer de ne plus l'oublie. Au plaisir de vous revoir sur TalkMe");
				Transport.send(message);
				jb=ServiceTools.serviceAccepted("Mail de recuperation envoye");
			}else{
				jb=ServiceTools.serviceRefused("Vous n'etes pas inscrit sur Talkme",-1);
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

		return jb;
	}
}
