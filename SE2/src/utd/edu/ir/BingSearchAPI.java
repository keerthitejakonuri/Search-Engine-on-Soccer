package utd.edu.ir;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

public class BingSearchAPI {
	/*public static void main(String[] args) {
        AzureSearchWebQuery aq = new AzureSearchWebQuery();
        aq.setAppid("iUvZEHhruQpXTykdqoY76duA4/OwPYks2tOUZn7+G4I");
        aq.setQuery("mexican restaurants in dallas");
 // The results are paged. You can get 50 results per page max.
 // This example gets 150 results
        for (int i=1; i<=3 ; i++) {
           aq.setPage(i);
           aq.doQuery();
           AzureSearchResultSet<AzureSearchWebResult> ars = aq.getQueryResult();
           for (AzureSearchWebResult anr : ars) {
            System.out.println(anr.getTitle());
            System.out.println(anr.getUrl());
            System.out.println(anr.getDisplayUrl());
            System.out.println(anr.getDescription());
           }
        }
   }*/
		
	public static ArrayList<String> bingSearch (String find) {		
		ArrayList<String> bingList = new ArrayList<String>();
		try{
        final String accountKey = "iUvZEHhruQpXTykdqoY76duA4/OwPYks2tOUZn7+G4I";
        final String bingUrlPattern = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%%27%s%%27&$format=JSON";
        final String query = URLEncoder.encode(find, Charset.defaultCharset().name());
        final String bingUrl = String.format(bingUrlPattern, query);
        final String accountKeyEnc = Base64.getEncoder().encodeToString((accountKey + ":" + accountKey).getBytes());
        final URL url = new URL(bingUrl);
        final URLConnection connection = url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);       
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            final JSONObject json = new JSONObject(response.toString());
            final JSONObject d = json.getJSONObject("d");
            final JSONArray results = d.getJSONArray("results");
            final int resultsLength = results.length();
            final int rL=Math.min(resultsLength, 50);                 // set how many results;
            for (int i = 0; i < rL; i++) {
                final JSONObject aResult = results.getJSONObject(i);
         //       System.out.println(aResult.get("Url"));
         //       System.out.println(aResult.get("Title"));
                bingList.add(aResult.get("Url")+" "+aResult.get("Title"));
            }
        }
		}
		catch(Exception e){
			e.printStackTrace();
		}
        return bingList;
    }
	public static void main(String [] args){
		 System.out.println(bingSearch ("Chinese Dallas").size());
	}
}
