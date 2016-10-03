package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import BdTools.DBStatic;
import Services.LogoutService;

public class LogOutServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String cle=req.getParameter("cle");
		JSONObject jb;
		/*
		 * Le mec une fois connecté à une clée
		 * il suffit dans la bdd de detacher la clée du compte client.
		 */
		//traitement
		jb=LogoutService.logOut(cle);
		DBStatic.add2logs(req.getRemoteAddr(), "Logout");

		//reponse
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}

}