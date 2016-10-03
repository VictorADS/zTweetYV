package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import Services.SendEmail;

public class PasswordForgotServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String mail=req.getParameter("mail");
		JSONObject jb;
		//traitement
		jb=SendEmail.sendMessage(mail);
		//reponse
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}

}