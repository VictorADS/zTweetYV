package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import Services.SearchService;

public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String key=req.getParameter("key");
		int friends=Integer.parseInt(req.getParameter("friends"));
		String query=req.getParameter("query");
		JSONObject jb=SearchService.Search(key,query,friends);
		PrintWriter writer=resp.getWriter();
		resp.setContentType("text/plain");
		writer.print(jb.toString());
	}

}
