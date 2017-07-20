package utd.edu.ir;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public class RankDoc {
	
	    int name;
	   public String url;
	   public  String title;
	    double score = 0;
	    double pageRank;
	    double topicPageRank;
	    double hub = 1;
	    double authority = 1;
	    Set<RankDoc> outDocs = new HashSet<>();
	    Set<RankDoc> inDocs = new HashSet<>();
	    public RankDoc(IndexSearcher searcher, ScoreDoc doc) throws Exception {
	        score = doc.score;
	        Document d = searcher.doc(doc.doc);
	        String path = d.get("path");
	        int length = path.length();
	        this.name = Integer.valueOf(path.substring(length - 12, length - 5));
	        this.url = d.get("url").trim();
	        this.title = d.get("title").trim();
	        this.pageRank = Double.valueOf(d.get("pageRank"));
	        this.topicPageRank = Double.valueOf(d.get("topicPageRank"));
	    }
	    
	    public RankDoc(int name) {
	        this.name = name;
	    }
	    
	    public boolean equals(Object other) {
	        RankDoc doc = (RankDoc) other;
	        return this.name == doc.name;
	    }
	

}
