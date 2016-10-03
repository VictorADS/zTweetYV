package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import Services.VerifParam;
 public class ChangeParamServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String login=req.getParameter("login");
		String mail=req.getParameter("mail");
		String mdp=req.getParameter("mdp");
		String loginuser=req.getParameter("user");

		JSONObject jb;
		//traitement
		jb=VerifParam.verif(login, mail, mdp,loginuser);
		//reponse
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}

}