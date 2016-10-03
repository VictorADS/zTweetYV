package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import Services.ShowProfile;

public class ShowProfileServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		//verifications 
		String LoginMe=req.getParameter("MyLogin");
		String LoginOtherGuy=req.getParameter("OtherLogin");


		
		JSONObject jb;

		//traitement
		jb=ShowProfile.get(LoginOtherGuy,LoginMe);
		
		//reponse
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}
}