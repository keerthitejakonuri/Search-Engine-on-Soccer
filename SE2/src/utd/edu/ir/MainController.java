package utd.edu.ir;


import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MainController")
public class MainController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	static boolean flag=false;
	public void doPost(HttpServletRequest request, HttpServletResponse response)  
			throws ServletException, IOException {  
		String query=request.getParameter("query");  
		System.out.println(query);

		request.setAttribute("resp", query);

		 if (flag==false){		 
			 try {
					SearchIndex.initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				flag=true;
		}

		 try {
				SearchIndex.search(query, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 
		 System.out.println("pass 1");
			List <RankDoc> rankDoc=SearchIndex.rankDoc;
			List <RankDoc> rankPage=SearchIndex.rankPage;
			List<RankDoc> rankTopicPage=SearchIndex.rankTopicPage;
			List<RankDoc> rankHub=SearchIndex.rankHub;
			List<RankDoc> rankAuthority=SearchIndex.rankAuthority;
			List<RankDoc> rankQuery =SearchIndex.rankQuery;
			List<String> clusterResult=SearchIndex.clusterResult;
			String expandedQuery = SearchIndex.expandedQuery;
					
			request.setAttribute("rankDoc", rankDoc);	
			
			System.out.println("pass 2");
			request.setAttribute("expandedQuery", expandedQuery);
			request.setAttribute("rankPage", rankPage);	
			request.setAttribute("rankTopicPage", rankTopicPage);	
			request.setAttribute("rankHub", rankHub);	
			request.setAttribute("rankAuthority", rankAuthority);	
			request.setAttribute("rankQuery", rankQuery);	
			request.setAttribute("clusterResult", clusterResult);	
			System.out.println(rankQuery.size());
			System.out.println(clusterResult.size());
			
			ArrayList<String> googleSearchList = GoogleSearchAPI.googleSearch(query);
			request.setAttribute("googleSearchList", googleSearchList);

			 System.out.println("pass 3");
			ArrayList<String> bingList = BingSearchAPI.bingSearch(query);
			request.setAttribute("bingList", bingList);
		
		request.getRequestDispatcher("2.jsp").forward(request, response);
	}
}
