package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import Services.createUser;

public class RegisterServlet extends HttpServlet implements Servlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		//Verifications 
		String login=req.getParameter("login");
		String passWord=req.getParameter("pass");
		String email=req.getParameter("email");
		String prenom=req.getParameter("prenom");
		String nom=req.getParameter("nom");
		
		
		//Traitement
		JSONObject jb=createUser.create(login, passWord, email,nom,prenom);
		//Reponse
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}
}