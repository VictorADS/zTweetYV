package Services;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import javax.mail.MessagingException;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceTools {
	public static JSONObject serviceRefused(String message,int codeErreur){
		JSONObject j=new JSONObject();
		try{
			switch(codeErreur){
			case(-1) :
				j.put("Erreur",message);
			break;
			case(100) :
				j.put("Erreur",message);
			break;
			case(1000) :
				j.put("Erreur",message);
				break;
			case(10000) :
				j.put("Erreur","Erreur serveur mail");
			break;

			default:
				j.put("Erreur", "Erreur inconnue, contactez l'administrateur du service.");
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		return j;
	}
	public static JSONObject serviceAccepted(String mess){
		JSONObject j=new JSONObject();
		try{
			j.put("Accepted",mess);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return j;
	}
	public static String generate() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; 
		StringBuilder pass = new StringBuilder(10);
		Random r=new Random();
		for (int x = 0; x < 10; x++) {
			int i=r.nextInt(chars.length());
			pass.append(chars.charAt(i));
		}
		return pass.toString();
	}
	public static Timestamp updateKeytime(){
		Date now=new Date();
		long timeofkey=30*60*1000;
		return new Timestamp(now.getTime()+timeofkey);
	}
	public static JSONObject GestionDesErreur(Exception e){
		JSONObject jb=null;
		if(e instanceof MessagingException){
			jb=ServiceTools.serviceRefused(""+e.toString(),10000);
		}else{
			if(e instanceof InstantiationException || e instanceof IllegalAccessException || e instanceof ClassNotFoundException || e instanceof SQLException){
				jb=ServiceTools.serviceRefused(""+e.toString(),1000);
			}else{
				if(e instanceof JSONException){
					jb=ServiceTools.serviceRefused(""+e.toString(),100);
				}
			}
		}	
		jb=ServiceTools.serviceRefused(""+e.toString(),100);
		return jb;
	}
	public static String cleanString(String s){
		int size=s.length();
		for(int i=0;i<size;i++){
			if(s.charAt(i)==' ' || s.charAt(i)=='\\' ||s.charAt(i)=='"' || s.charAt(i)=='\''){
				String[] split=s.split(""+s.charAt(i));
				s="";
				for(String x : split){
					s=s.concat(x);
				}
				size=s.length();
			}
		}
		return s;
	}
}
