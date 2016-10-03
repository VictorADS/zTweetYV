package Services;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONObject;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;

import BdTools.BDMapReduce;
import BdTools.DBStatic;
import BdTools.RequeteStatic;

public class SearchService {
	public static JSONObject Search(String key,String query,int friends){
		JSONObject jb=null;
		Connection connection=null;
		ResultSet user=null;
		Statement st=null;
		long iskeyexpired=0;

		try{
			//Connection
			connection=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(connection);

			//On recupere l'user 
			user=RequeteStatic.getUserWithKey(key, st);				
			iskeyexpired=RequeteStatic.isKeyTimeValide(user);

			//On verifie la validité de la clée 
			if(iskeyexpired==1){
				jb=ServiceTools.serviceRefused("Cle perime, veuillez vous reconnecter", 1000);
			}

			if(iskeyexpired==-1){
				if(query==null || query=="")
					jb=SearchDeconnecte();		
				else{
					jb=SearchMapReduce("", query, 0);
				}//->
			}

			if(iskeyexpired==0){
				String login=user.getString("login");
				if((query=="" ||query==null) && friends==0){
					jb=SearchNoreq(login); //->
					return jb;
				}
				if((query=="" ||query==null) && friends==1){
					jb=SearchFriend(login); //->
					return jb;
				}
				if(query !=null){
					jb=SearchMapReduce(login,query,friends);
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
	return jb;
}


/******************************Tools*****************************/ 

public static JSONObject SearchDeconnecte(){
	ArrayList<BasicDBObject> list=new ArrayList<BasicDBObject>();
	DBCollection col=null;
	JSONObject jb=new JSONObject();

	try{
		col=DBStatic.getMongoConnection();
		BasicDBObject sorted=new BasicDBObject();
		sorted.put("date",-1);
		DBCursor cur=col.find().sort(sorted).limit(50);

		while(cur.hasNext()){
			BasicDBObject next=(BasicDBObject)cur.next();
			list.add(next);
		}

		jb.put("resultats",list);

	}catch(Exception e){
		jb=ServiceTools.serviceRefused(e.getMessage(), 1000);
	}

	return jb;
}
public static JSONObject SearchNoreq(String login){
	ArrayList<BasicDBObject> list=new ArrayList<BasicDBObject>();
	DBCollection col=null;
	JSONObject jb=new JSONObject();

	ArrayList <String> friends=new ArrayList<String>();

	try{
		friends=RequeteStatic.getFriendsOfUser(login);
		col=DBStatic.getMongoConnection();

		BasicDBObject sorted=new BasicDBObject();
		sorted.put("date",-1);
		DBCursor cur=col.find().sort(sorted).limit(50);

		while(cur.hasNext()){
			BasicDBObject next=(BasicDBObject)cur.next();
			BasicDBObject user=(BasicDBObject) next.get("auteur");
			//Si le message à été ecrit par un ami
			if(friends.contains(user.get("login"))){
				user.put("contact",true);
				next.put("auteur",user);
			}
			list.add(next);
		}
		jb.put("resultats",list);
	}catch(Exception e){
		jb=ServiceTools.serviceRefused(e.getMessage(), 1000);
	}
	return jb;
}
public static JSONObject SearchFriend(String login){
	JSONObject jb=new JSONObject();
	DBCollection col=null;
	ArrayList<BasicDBObject> list=new ArrayList<BasicDBObject>();
	ArrayList <String> friends=new ArrayList<String>();
	if(login==null)
		jb=ServiceTools.serviceRefused("Login non renseigne", -1);
	else{
		try {
			//INIT
			friends=RequeteStatic.getFriendsOfUser(login);
			col=DBStatic.getMongoConnection();
			BasicDBObject query=new BasicDBObject();
			query.put("auteur.login", new BasicDBObject("$in", friends));
			BasicDBObject sorted=new BasicDBObject();
			sorted.put("date",-1);
			DBCursor cur=col.find(query).sort(sorted);
			while(cur.hasNext()){
				BasicDBObject next=(BasicDBObject)cur.next();
				BasicDBObject user=(BasicDBObject) next.get("auteur");
				user.put("contact",true );
				next.put("auteur", user);		
				list.add(next);
			}
			jb.put("resultats", list);

		} catch (Exception e) {
			jb=ServiceTools.GestionDesErreur(e);

		}
	}

	return jb;


}
public static JSONObject SearchMapReduce(String login,String querywords,int friend){
	JSONObject jb=new JSONObject();
	String mot=querywords.replaceAll("[^\\w\\s]","");
	String words[]=mot.split(" ");
	String mdf="function(){"
			+ "var df=[]; var words=this.texte.match(/\\w+/g);"
			+"if(words==null){"
			+ "return;"
			+ "}"
			+ "for(var i=0;i<words.length;i++){"
			+ 	"if(df[words[i]]==null){"
			+ 		"df[words[i]]=1;"
			+   "}"
			+ "}for(ln in df){"
			+ "emit(ln,{df:1});"
			+ "}"
			+ "}";

	String rdf="function(key,values){"
			+ "var i=0;"
			+ "for(var it=0;it<values.length;it++){"
			+ "i+=values[it].df;"
			+ "}"
			+ "return({df:i});"
			+ "}";

	String mtf="function(){ var tf=[]; var words=this.texte.match(/\\w+/g);"
			+"if(words==null)return;"
			+ "for(var i=0;i<words.length;i++){"
			+ "if(tf[words[i]]==null){"
			+ "tf[words[i]]=1;"
			+ "}else{"
			+ "tf[words[i]]++;"
			+ "}}for(ln in tf){"
			+ "emit({id:this._id,key:ln},{tf:tf[ln]});}}";

	String rtf="function(key,values){"
			+ "var sum=0;for(var i=0;i<values.length;i++){"
			+ "sum+=values[i].tf;"
			+ "};return ({tftot:sum});"
			+ "}";

	try {
		BDMapReduce.viderBD();
		DBCollection col=DBStatic.getMongoConnection();
		BasicDBObject query=new BasicDBObject();
		ArrayList<String> friends=RequeteStatic.getFriendsOfUser(login);
		if(friend==1){
			query.put("auteur.login", new BasicDBObject("$in", friends));
		}
		MapReduceOutput outdf=col.mapReduce(mdf,rdf,null,MapReduceCommand.OutputType.INLINE,query);
		for(DBObject o: outdf.results()){
			BDMapReduce.addDF(o);
		}
		MapReduceOutput outtf=col.mapReduce(mtf,rtf,null,MapReduceCommand.OutputType.INLINE,query);
		for(DBObject o: outtf.results()){
			BDMapReduce.addTF(o);
		}
		ArrayList<DBObject> com=BDMapReduce.getPertinence(words,friends,friend);
		if(com.size()==0){
			jb=ServiceTools.serviceRefused("Pas de commentaires", 1000);
		}else{
			jb=ServiceTools.serviceAccepted("Trouvé");
			jb.put("resultats",com);
		}

	} catch (Exception e) {
		jb=ServiceTools.GestionDesErreur(e);
	}

	return jb;
}


}
