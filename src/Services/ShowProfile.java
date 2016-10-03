package Services;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONObject;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
public class ShowProfile {
	public static JSONObject get(String loginOtherGuy,String loginMe){
		JSONObject jb=new JSONObject();
		Connection c=null;
		Statement st=null;
		DBCollection col=null;
		ResultSet userFromDatabase=null;
		if(loginOtherGuy==null){
			jb=ServiceTools.serviceRefused("Utilisateur non renseigne", -1);
			return jb;
		}
		try{
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			userFromDatabase=RequeteStatic.getUserWithLogin(loginOtherGuy, st);
			col=DBStatic.getMongoConnection();
			if(userFromDatabase.first()){
				//Recuperer les infos user (Login/Nom/Prenom/email)
				String auteur=userFromDatabase.getString("login");
				String name=userFromDatabase.getString("nom");
				String prenom=userFromDatabase.getString("prenom");
				String email=userFromDatabase.getString("email");
				BasicDBObject profile=new BasicDBObject();
				profile.put("login",auteur);
				if(RequeteStatic.IsFriend(st, loginMe, loginOtherGuy)){
					profile.put("contact", true);
				}else{
					profile.put("contact", false);
				}
				profile.put("email",email);
				profile.put("prenom", prenom);
				profile.put("name",name);
				jb.put("InfoProfil",profile);
				
				//Recuperer le nombre de messages 
				BasicDBObject query=new BasicDBObject();
				query.put("auteur.login",auteur);
				BasicDBObject sorted=new BasicDBObject();
				sorted.put("date",-1);
				DBCursor cur=col.find(query).sort(sorted);
				int nbMessages=cur.count();
				jb.put("nbMessages",nbMessages);
				//Recuperer tout les messages
				ArrayList<String> friends=RequeteStatic.getFriendsOfUser(loginMe);
				ArrayList<BasicDBObject> listeTweet= new ArrayList<BasicDBObject>();
				while(cur.hasNext()){
					BasicDBObject next=(BasicDBObject)cur.next();
					BasicDBObject user=(BasicDBObject) next.get("auteur");
					if(friends.contains(user.get("login"))){
						user.put("contact",true);
						next.put("auteur",user);
					}
					listeTweet.add(next);
				}
				jb.put("tweet",listeTweet);
				//Recupere la liste des amis 
				ResultSet friendsOfUser=RequeteStatic.LookForFriends(auteur,st);
				ArrayList<String> listeDamis= new ArrayList<String>();
				while(friendsOfUser.next()){
					listeDamis.add(friendsOfUser.getString("login_f"));
				}
				jb.put("Amis",listeDamis);
				jb.put("nbAmis",listeDamis.size());
			}else{
				jb=ServiceTools.serviceRefused("L'utilisateur cherche nexiste pas", 1000);
			}
			return jb; 
		}catch(Exception e){
			jb=ServiceTools.GestionDesErreur(e);
		}finally{
			try {
				DBStatic.deconnectionBD(st, c, userFromDatabase);
			} catch (SQLException e) {
				jb=ServiceTools.GestionDesErreur(e);
			}
		}
		return jb;
	}
}