//package BdTools;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.Timestamp;
//import java.util.Date;
//
//import org.json.JSONObject;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//
//import Services.ServiceTools;
//
//public class RequeteStatic {
//	
//	public static Boolean IsSuccess(ResultSet r ) throws SQLException{
//		return r.first();
//	}
//	
//	public static void putUser(Statement st,String login,String mdp,String mail,String prenom,String nom) throws SQLException{
//		st.executeUpdate("insert into user(`login`, `passwd`, `email`, `prenom`, `nom`) values('"+login+"','"+mdp+"','"+mail+"','"+prenom+"','"+nom+"')");
//	}
//	
//	
//	public static ResultSet GetMail(String mail, Statement st) throws SQLException{
//		return st.executeQuery("SELECT * from user u where u.email=\""+mail+"\"");
//	}
//	
//	public static ResultSet verifUserKey(String key,Statement st) throws SQLException{
//		return st.executeQuery("SELECT * from session s where s.clef=\""+key+"\"");
//	}
//
//	public static ResultSet findLogin(String login,Statement st) throws SQLException{ 
//		login=ServiceTools.cleanString(login);
//		return st.executeQuery("SELECT * from user u where u.login=\""+login+"\"");
//	}
//	public static ResultSet findMail(String mail,Statement st) throws SQLException{ 
//		return st.executeQuery("SELECT * from user u where u.email=\""+mail+"\"");
//	}
//	public static ResultSet VerifySessionOpen(String log,String pwd,Statement st)throws SQLException{
//		//Prend le login et le mpd et verifie qu'il existe une session active avec cet utilisateur
//		return st.executeQuery("SELECT * from session s,user u"+ " where u.login=\""+log+"\" and u.passwd=\""+pwd+"\" and s.login=u.login");
//	}
//	public static ResultSet SearchFriend(String friend,String me,Statement st) throws SQLException{
//		return st.executeQuery("SELECT * from friend f where f.login_u=\""+me+"\" and f.login_f=\""+friend+"\"");
//	}
//	
//	public static ResultSet LookForFriends(String me,Statement st) throws SQLException{
//		return st.executeQuery("SELECT * from friend f where f.login_u=\""+me+"\"");
//	}
//	
//	public static Boolean UserExist(String friend,Statement st) throws SQLException{
//		ResultSet r= st.executeQuery("SELECT * from user u where u.login=\""+friend+"\"");
//		return r.first();
//	}
//	public static ResultSet VerifyOwner(Statement st, String log, String pwd) throws SQLException{
//		return st.executeQuery("SELECT * from user where user.login=\""+log+"\" and user.passwd=\""+pwd+"\"");
//	}
//
//	public static long isKeyTimeValide(ResultSet r) throws SQLException{
//		Date now=new Date();
//		if(r.first()){
//			Timestamp dateDb=r.getTimestamp("date_exp");
//			if(dateDb.after(now)){
//				return 0;	//Cle valide
//			}else{
//				return 1;	// Cle perime
//			}
//		}else{
//			return -1;	 // Cle qui nexiste pas
//		}
//	}
//
//
//public static JSONObject addTweet(DBCollection col,String text,String auteur,int idauteur){
//	Date now=new Date();
//	BasicDBObject doublon=new BasicDBObject();
//	doublon.put("texte",text);
//	doublon.put("date", now.getTime());
//	BasicDBObject aut=new BasicDBObject();
//	aut.put("idauteur",idauteur);
//	aut.put("login",auteur);
//	aut.put("contact", false);
//	doublon.put("auteur",aut );
//	if(col.findOne(doublon)!=null){
//		return ServiceTools.serviceRefused("Tweet deja envoyé",1000);					
//	}
//
//	col.insert(doublon);
//	return ServiceTools.serviceAccepted("Commentaire ajouté");
//}
//}
package BdTools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import Services.ServiceTools;

public class RequeteStatic {
	
	
	public static Boolean IsFriend(Statement st, String me, String other) throws SQLException{
		ResultSet sza=st.executeQuery("SELECT * from friend u where u.login_u='"+me+"' AND u.login_f='"+other+"'");
		return sza.first();
	}
	
	public static void putUser(Statement st,String login,String mdp,String mail,String prenom,String nom) throws SQLException{
		st.executeUpdate("insert into user(`login`, `passwd`, `email`, `prenom`, `nom`) values('"+login+"','"+mdp+"','"+mail+"','"+prenom+"','"+nom+"')");
	}
	
	public static ResultSet GetMail(String mail, Statement st) throws SQLException{
		return st.executeQuery("SELECT * from user u where u.email=\""+mail+"\"");
	}
	
	public static ResultSet getUserWithKey(String key,Statement st) throws SQLException{
		return st.executeQuery("SELECT * from session s where s.clef=\""+key+"\"");
	}
	public static ResultSet getUserWithId(String id,Statement st) throws SQLException{
		return st.executeQuery("SELECT * from user u where u.id=\""+id+"\"");
	}
	

	public static ResultSet getUserWithLogin(String login,Statement st) throws SQLException{ 
		login=ServiceTools.cleanString(login);
		return st.executeQuery("SELECT * from user u where u.login=\""+login+"\"");
	}
	
	public static ResultSet VerifySessionOpen(String log,String pwd,Statement st)throws SQLException{
		//Prend le login et le mpd et verifie qu'il existe une session active avec cet utilisateur
		return st.executeQuery("SELECT * from session s,user u"+ " where u.login=\""+log+"\" and u.passwd=\""+pwd+"\" and s.login=u.login");
	}
	public static ResultSet SearchFriend(String friend,String me,Statement st) throws SQLException{
		return st.executeQuery("SELECT * from friend f where f.login_u=\""+me+"\" and f.login_f=\""+friend+"\"");
	}
	
	public static ResultSet LookForFriends(String me,Statement st) throws SQLException{
		return st.executeQuery("SELECT * from friend f where f.login_u=\""+me+"\"");
	}
	
	public static Boolean UserExist(String friend,Statement st) throws SQLException{
		ResultSet r= st.executeQuery("SELECT * from user u where u.login=\""+friend+"\"");
		return r.first();
	}
	public static ResultSet VerifyOwner(Statement st, String log, String pwd) throws SQLException{
		return st.executeQuery("SELECT * from user where user.login=\""+log+"\" and user.passwd=\""+pwd+"\"");
	}

	public static long isKeyTimeValide(ResultSet r) throws SQLException{
		Date now=new Date();
		if(r.first()){
			Timestamp dateDb=r.getTimestamp("date_exp");
			if(dateDb.after(now)){
				return 0;	//Cle valide
			}else{
				return 1;	// Cle perime
			}
		}else{
			return -1;	 // Cle qui nexiste pas
		}
	}
public static ArrayList<String> getFriendsOfUser(String loginuser) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	ArrayList <String> friends=new ArrayList<String>();
	java.sql.Connection c=DBStatic.getMyConnection();
	Statement st=DBStatic.connexionBD(c);
	ResultSet r=RequeteStatic.LookForFriends(loginuser, st);
	while(r.next()){
		friends.add(r.getString("login_f"));
	}
	DBStatic.deconnectionBD(st, c, r);
	return friends;
}

public static JSONObject addTweet(DBCollection col,String text,String auteur,int idauteur) throws JSONException{
	Date now=new Date();
	BasicDBObject tweet=new BasicDBObject();
	tweet.put("texte",text);
	BasicDBObject author=new BasicDBObject();
	author.put("idauteur",idauteur);
	author.put("login",auteur);
	author.put("contact", false);
	tweet.put("auteur",author);
	if(objectAlreadyExist(col,tweet)){
		return ServiceTools.serviceRefused("Tweet deja envoye",1000);					
	}
	tweet.put("date", now.getTime());

	col.insert(tweet);
	JSONObject jb=ServiceTools.serviceAccepted("Commentaire ajoute");
	jb.put("idobject",tweet.getObjectId("_id").toString());
	jb.put("wtf is that ","texte : "+text+" et length : "+text.length());
	return jb;
}

public static boolean objectAlreadyExist(DBCollection col, BasicDBObject obj){
	return col.findOne(obj)!=null;
}

}