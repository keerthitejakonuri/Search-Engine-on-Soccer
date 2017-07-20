package utd.edu.ir;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleSearchAPI {
	
	public static ArrayList<String> googleSearch (String search) {
		ArrayList<String> list = new ArrayList<String>();
		
		try{
		
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		int page=0;	
		String xx=google + URLEncoder.encode(search, charset);		
		for(;page<5;page++){         // set how many 1 for 10 results
			String addition="#q="+URLEncoder.encode(search, charset)+"&start="+(10*page);
			Elements links = Jsoup.connect(xx+addition).userAgent("Mozilla").get().select("h3.r > a");
			
			for (Element link : links) {
			    String title = link.text();
			    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
		
			    String data=url+" "+title;
			    list.add(data);
			}
		}		
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	//	System.out.println(list.size());		
		return list;
		
	}
	
	public static void main(String[] args) {
		
		googleSearch("indian restaurants in dallas");
	}

}


