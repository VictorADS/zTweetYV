package BdTools;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class BDMapReduce {
	public static void viderBD() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Connection c=DBStatic.getMyConnection();
		Statement st=DBStatic.connexionBD(c);
		st.executeUpdate("DELETE FROM tfs");
		st.executeUpdate("DELETE FROM dfs");
	}
	public static void addDF(DBObject obj) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Connection c=DBStatic.getMyConnection();
		Statement st=DBStatic.connexionBD(c);
		String mot=obj.get("_id").toString();
		DBObject value=(DBObject) obj.get("value");
		String df=value.get("df").toString();
		String selectdf="SELECT * from dfs where mot=\'"+mot+"\'";
		ResultSet r=st.executeQuery(selectdf);
		if(r.first()){
			String updatedf="UPDATE `dfs` SET `occurence`=\'"+df+"\' WHERE mot=\'"+mot+"\'";
			st.executeUpdate(updatedf);
		}else{
			String insertdf="INSERT INTO `dfs`(`mot`, `occurence`) VALUES (\'"+mot+"\',"+df+")";
			st.executeUpdate(insertdf);
		}
	}
	public static void addTF(DBObject obj) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Connection c=DBStatic.getMyConnection();
		Statement st=DBStatic.connexionBD(c);
		DBObject _id=(DBObject) obj.get("_id");
		DBObject value=(DBObject) obj.get("value");
		String doc=_id.get("id").toString();
		String mot=_id.get("key").toString();
		String tf=value.get("tf").toString();	

		String selecttf="SELECT * from tfs where mot=\'"+mot+"\' and document=\'"+doc+"\'";
		ResultSet r=st.executeQuery(selecttf);
		if(r.first()){
			String updatetf="UPDATE `tfs` SET `occurence`="+tf+" WHERE mot=\'"+mot+"\' and document=\'"+doc+"\'";
			st.executeUpdate(updatetf);
		}else{
			String inserttf="INSERT INTO `tfs`(`mot`, `document`, `occurence`) VALUES (\'"+mot+"\',\'"+doc+"\',"+tf+")";
			st.executeUpdate(inserttf);
		}

	}
	public static double getDF(String mot,Statement st) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		String selectdf="SELECT LOG10((COUNT(DISTINCT (u.document))/df.occurence)) as res from tfs u, dfs df where df.mot=\'"+mot+"\'";
		ResultSet r=st.executeQuery(selectdf);
		if(r.first()){
			return r.getDouble("res");
		}else{
			return 0;
		}
	}
	public static double getTF(String mot,String document,Statement st) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		String selecttf="SELECT (tf.occurence/COUNT(tot.occurence)) as res from tfs tot,tfs tf where tot.document=\'"+document+"\' and tf.mot=\'"+mot+"\' and tf.document=\'"+document+"\'";
		ResultSet r=st.executeQuery(selecttf);
		if(r.first()){
			return r.getDouble("res");
		}else{
			return 0;
		}
	}

	public static Double getScoreInDoc(String mot,String doc,Statement st) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Double res=0.0;
		double tf=getTF(mot,doc,st);
		double df=getDF(mot,st);
		res=tf*df;
		return res;

	}
	public static ArrayList<DBObject> getPertinence(String[] mots,ArrayList<String> friends, int friend) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, UnknownHostException, MongoException{
		Connection c = DBStatic.getMyConnection();
		Statement st = DBStatic.connexionBD(c);
		BasicDBObject query=new BasicDBObject();
		if(friend==1){
			query.put("auteur.login", new BasicDBObject("$in", friends));
		}
		DBCollection col=DBStatic.getMongoConnection();
		ArrayList<DBObject> comments=(ArrayList<DBObject>) col.find(query).toArray();
		ArrayList<DBObject> toRemove=new ArrayList<DBObject>();
		for(DBObject db : comments){
			DBObject aut=(DBObject) db.get("auteur");
			
			if(friends.contains(aut.get("login"))){
				aut.put("contact",true );
				db.put("auteur", aut);
			}
			
			double score=0.0;
			for(String mot:mots){
				score+=getScoreInDoc(mot, db.get("_id").toString(),st);
			}
			if(score==0)
				toRemove.add(db);
			db.put("score", score);
		}
		for(DBObject obj: toRemove){
			comments.remove(obj);
		}
		Collections.sort(comments, new MyDBOComparator());
		return comments;
	}
}
class MyDBOComparator implements Comparator<DBObject>{

	@Override
	public int compare(DBObject o1, DBObject o2) {
		if((double)o1.get("score")>(double)o2.get("score")){
			return -1;
		}else{
			return 1;
		}
	}
}