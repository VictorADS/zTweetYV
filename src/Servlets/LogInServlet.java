package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import BdTools.DBStatic;
import Services.LoginService;



public class LogInServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
	//verifications 
		String login=req.getParameter("login");
		String passWord=req.getParameter("pass");
		JSONObject jb;

	//traitement
		jb=LoginService.login(login, passWord);
		DBStatic.add2logs(req.getRemoteAddr(), "Login");

	//reponse
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}

}
