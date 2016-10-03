package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import Services.AddFriend;
 public class AddFriendServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String loginfriend=req.getParameter("loginf");
		String cle=req.getParameter("cle");
		JSONObject jb;
		//traitement
		jb=AddFriend.add(loginfriend, cle);
		//reponse
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}

}