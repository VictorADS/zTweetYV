package Services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import BdTools.DBStatic;
import BdTools.RequeteStatic;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class SuppComment {
	public static JSONObject supp(String cle,String id){
		JSONObject jb=null;
		Connection c=null;
		ResultSet r=null;
		Statement st=null;
		DBCollection col=null;

		if(cle==null){
			jb=ServiceTools.serviceRefused("Cle non fournie", -1);
			return jb;
		}
		try{
			c=DBStatic.getMyConnection();
			st=DBStatic.connexionBD(c);
			r=RequeteStatic.getUserWithKey(cle, st);
			long keyexpired=RequeteStatic.isKeyTimeValide(r);

			if(keyexpired==-1){
				jb=ServiceTools.serviceRefused("Utilisateur deconnecte, veuillez vous reconnectez", 1000);
			}
			if(keyexpired==1){
				jb=ServiceTools.serviceRefused("Cle perime veuillez vous reconnectez", -1);
			}
			if(keyexpired==0){
				col=DBStatic.getMongoConnection();
				BasicDBObject query=new BasicDBObject();
				query.put("_id",new ObjectId(id));
				query.put("auteur.login", r.getString("login"));
				BasicDBObject find=(BasicDBObject) col.findOne(query);
				if(find!=null){
					col.remove(find);
					jb=ServiceTools.serviceAccepted("Message supprime");
				}else{
					jb=ServiceTools.serviceRefused("Message doesnt exist ", 1000);
				}
			}
		}catch(Exception e){
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
