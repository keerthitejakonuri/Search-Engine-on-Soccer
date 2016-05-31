# Search-Engine-on-Soccer
A Dynamic Search Engine on Soccer (Java, Crawler4j &amp; Apache Lucene, Net Beans IDE 8.0)

    i) Crawling
    How you gathered the web pages?
Crawler4j—an open-source multi-threaded web crawler based on Java, due to its stability and fast speed is used to crawl the web pages. 
 In the crawling process, the crawler program will record two things on hard drive: one is the html content with javascript and css removed; the other contains all html links of a web page. The html file and html links file have the same file name, which is the integer id assigned by the crawler4j plus 100,000. The reason to add 1,000,000 is that we do not know how many files it could collect(The crawl depth is set to 3, we are not sure how many pages it can get in deeper level). In worst case, if we gather over 1 million pages, the file name can still be 7 digits.  If we do not add certain number, then our filename format could be like “12”, “1012” or “10012” which have different digits, thus not good for future processing.

How you passed the collection to the index creation?
The html links file contains all html links of a html page, those of which also have corresponding links files. After the crawling, the link files are processed so that the html link in the link file is replaced by its corresponding id or filename. The new file is called a map file and it is used to generate web graph for student who does indexing. The format of map file (over 200 Mb) is like this:
1000032:1000011 1000104 1000013 1000025 1000026 1000117 1000032 
1000040:1000049 1000048 1000001 
1000033:1000011 1000103 1000104 1000026 1000032 1000033 1000038 1000040

How many web pages you were able to crawl?
With crawler4j, I collected over 350K pages related to football. Although there is a filter to filter out those links with facebook, youtube, twitter or casino (of course, soccer is always related to online gambling), there are many pages unnecessary crawled like online store Adidas.com. So after crawling the pages are filtered again, and finally we get about 300k pages for indexing.

How you make sure you did not have duplication in your crawl?
In crawler4j, the id will only be assigned to those html pages with unique urls. However, some different urls have the same content cause the web crawler won’t check the html content. To avoid this issue, in the above after-crawling filtering, we use the hashcode of the html file to remove duplicated html files.

Sources for your crawls?
There are 8 seed sites, all of which are top soccer sites on Alexa
http://footballdatabase.com/
http://uefa.com/
http://www.espnfc.us/
http://fifa.com/
http://www.foxsports.com/soccer/
http://www.goal.com/en-us/
http://worldsoccer.about.com/
http://www.skysports.com/football/

What I have learned?
1.	In developing a crawler, there are many aspects to be involved. If you just want a simple one, you can code easily in several hours. But stability and speed would be an issue. Also, the protocol the crawling robot needs to follow is of vital importance, which is the fundament of our Internet life.
2.	The search engine like Google is far more advanced than I thought before. For instance, the Google needs to keep up-to-date information; it also needs to save huge amount of data in its servers; in addition, it has to provide fast response considering this project needs a few seconds to respond a simple query. All these pose tremendous challenges to an excellent search engine.

What was you experience? what were the difficulties you faced and how you resolved them.
Firstly, a pure python-based web crawler was developed by myself but it proved to be slow—only 6 seconds for one page on average. After changing to Crawler4j, the speed can be several pages per second due to its multi-threaded mechanism, which proves the advantage of Java over Python in large projects.
Also, the huge amount of small files makes the file transfer pretty slow. When I coped my files to the student who does indexing, there is always error report in the file transfer. I had to zip and pack the html and link files, taking me 5 hours to generate 2 zip files. The student who does indexing used a few minutes to copy the zip file but spent more than 8 hours to unzip it. So I seriously believe the distributed system is imperative for commercial search engine.

  ii) Indexing and Relevance Model
  
  I have used Lucene and Tika as the tools to build my index. The first step is using tika to parse the htmls files into text and extract text and title information from every file. In second step, I use the web graph from crawling to calculate the page ranking and topic based page ranking. The third step is to create lucene documents which contain all these information and add it into the index. Lucene will store all these information and build the index.

WebGraph:
The web graph was received from crawling part and the file format is written as the document name followed by a list of documents it connecting to.  This file was then read using java program and used to calculate page rank and HIT Score. The statistics of this web graph are:
Number of Nodes: 356028 
Number of Links: 35051009
Largest Number of Incoming Links: 81802 (1001705) www.goal.com/en-us/
Largest Number of Outgoing Links: 1682 (1000598) http://www.uefa.com/sitemap/index.html
Number of pages which are relative to soccer:  274272
From the web graph, I calculated the page ranking and topic based page ranking and combine them with the html content into the index.

Relevance Model:
The first relevance model using document vector is done by using lucene API. According to Lucene document, it indexes the document in such a way that the inverted lists with tf-idf values are stored. During search time, it uses a combination of Vector Space Model (VSM) of Information Retrieval and the Boolean model.  Then it outputs the top documents with high scores. The second model I am using is with the page ranking. Lucene provide an easy user API that I can create an expression like “_score + pagerank”.  It will sort the document using the total value of score and page rank. The key point here is that I have to store the page ranking information in the index otherwise it cannot retrieve the information.

Topic based Page Rank:
The Web Page with highest topic based page ranking is www.uefa.com  which has 0.2081. The topic based page ranking is calculated in following way: all webpage which are relative to soccer will be assigned a biased value 1/(total number of relative pages), the other pages will be assigned a biased value 0. Then the topic based page rank is calculated using iteration method.

HITS Algorithm:
Because HITS algorithm is query based, hub and authority scores were calculated in the run time. Since I need to use the web graph to retrieve documents those are connecting to the search results, I need to load the whole web graph on the server when the search engine is running. Once the search returned the top documents from the index, I used top 200 as a root set, and add all documents are pointed by root set, also add up to 200 documents which are pointing to each documents in the root set. All documents together give the base set which are then running using HITS algorithm. Then the documents which have high rankings will be output. Here are two calculation resultes for HITS algorithm.
For query “uefa”, the webpage with highest Hub score is:
http://www.uefa.com/uefaeuro/index.html :0.699
the webpage with highest Authority score is same:
http://www.uefa.com/uefaeuro/index.html : 0.006
For query “”, the webpages with highest Hub scores is:
http://www.fifa.com/associations/index.html : 0.493
The webpage with highest Authority score is:
http://www.fifa.com/development/index.html : 0.0115
The reason for the authority score is much smaller compared with hub score is that I have limited the pages that pointing to each page according to the algorithm.

Collaborate with UI:
The controller layer get the query from UI and send the request to the back end, the search API then search the result from the Index, implementing HITS algorithm and return the result to front end.

Query selection and discussion:
I have tested around 20 queries to test the search engine. The query was generated from several aspects: famous player name like “Messi”, “Ronaldo” and “Neymar”, famous club team like “Barcelona” and “AC Milan”, famous soccer organization like “FIFA” and “UEFA”. The search engine will then return the results for each relevance models. For the ones without any page ranking or hits score, the webpages returned are the ones with the words but may not that relative to soccer. The results from page ranking and topic based page ranking will move more relevant pages to front. The results from HITS algorithms will put the web pages with more links to front. All results shown by using page ranking and HITS, the search results can be improved. But which one is better depends on which query we are trying to search.

  iii) Clustering
  
  Clustering: 
The process of grouping a set of objects into classes of similar objects.
	1. Documents within a cluster should be similar.
	2. Documents from different clusters should be dissimilar.

Algorithms
1.	K-Means (Flat clustering, Hard clustering)
2.	Agglomerative (Hierarchical clustering)
3.	EM Algorithm (Flat clustering, Soft clustering)

And in this project I will mainly use the K-Means algorithm. So below will focus on this.
Basic K-Means Algorithm
1.	Choose k number of clusters to be determined
2.	Choose k objects randomly as the initial cluster center
3.	Repeat  
3.1. Assign each object to their closest cluster
3.2. Compute new clusters, i.e. Calculate mean points.
      
4. Until 
        	4.1. No changes on cluster centers (i.e. Centroids do not change location any more) OR No object changes its cluster (We may define stopping criteria as well) 

II. Diving into the project
We crawled 300,000+ html page from the internet, which is very huge account such that my computer can’t handle. So I chose the front 20,000+ pages and their link content for my clustering work. Even though, the final result shows my method works.
And the tool I used is Weka, which is an open source machining learning software.

Below is the detailed process:
(1) Document Representation
Each document is represented as a vector using the vector space model. 
In weka process module, choose StringToWordVector.

(2) TF-IDF
TF-IDF stands for term frequency-inverse document frequency.The number of times a term occurs in a document is called its term frequency. We can calculate the term frequency for a word as the ratio of number of times the word occurs in the document to the total number of words in the document.
The inverse document frequency is a measure of whether the term is common or rare across all documents. It is obtained by dividing the total number of documents by the number of documents containing the term, and then taking the logarithm of that quotient.
The tf*idf of term t in document d is calculated as:
 
In weka StringToWordVector options, choose IDFTransform-True and TFTransform-True. Then click Apply button.

(3) Implement K-Means algorithm
3.1. In Cluster module, choose K-Means, then set the number of clusters to 10. Click Start. Then get the result.
3.2. According to the result, I got output1.txt, which is the map indicating which cluster one document belongs to. 
3.3. Meantime, the result also includes the centroid vector. So I used them to compute the distance between one document and the centroid of its cluster which the document belongs to.
3.4. Then, I choose top 5 closest documents as the returning result for its cluster. And this mapping information is in the output2.txt file.

    iv) Query Expansion
    
    Query expansion (QE) is the process of reformulating a user’s query to improve retrieval performance in information retrieval operations. Query expansion is a methodology studied in the field of computer science, particularly within the realm of natural language processing and information retrieval.
Algorithms
1)	Metric Clustering
2)	Association Clustering
3)	Scalar Clustering
4)	Rocchio Algorithm

And in this project I mainly used the Metric clustering and Association Clustering Algorithms for pseudo relevance feedback. So I will focus mainly on Metric and Association Clustering.
Pseudo-relevance feedback automates the manual part of true relevance feedback.
Pseudo-relevance algorithm:
•	Retrieve a ranked list of hits for the user’s query.
•	Assume that the top k documents are relevant.
•	Do pseudo relevance feedback (Metric/Association clustering).

Association clusters are based on the frequency of co-occurrence of pairs of terms in documents and do not take into account where the terms occur in a document.
Since two terms which occur in the same sentence seem more correlated than two terms which occur far apart in a document, it might be worthwhile to factor in the distance between two terms in the computation of their correlation factor. Metric clusters are based on this idea.
I took Initial Query and Top 10 ranked Documents as input and reformulated a new Expanded Query using Metric and Association Clustering. During demo we displayed it using Metric Clustering as I felt Metric clustering is more relevant. 

The architecture of query expansion part is as follows:
 

Architecture of Query Expansion using Pseudo Relevance Feedback







And in this project I mainly used Metric Clustering algorithm.
1.	Form a list of vocabulary and the corresponding stem terms used in each document.
2.	For each term in the query 
2.1 Find the distance between the terms formed from a stem in the document and the                term from query.  
2.2 If distinct terms formed from a stem appear more than once in a document calculate their distances individually and add them to get a single value.
       3.   Calculate the inverse of the distances calculated from step 2.
       4.    Add the top 3 terms with highest correlated values (inverse of distance) to the query          
               entered by the user. Which makes the expanded query.  
II. Diving into the project
We crawled 100,000 html page from the internet, for which indexing was applied. After page ranking and Hits algorithm was applied, I took top 10 relevant documents for performing query expansion. 

Below is the detailed process:
1.	Representing Documents as a list of vocabulary and stem 
 Individual terms from the documents are collected. Then calculated the stems for each term in the vocabulary. 

2.	Correlation Matrix and Cluster value calculation
Metric cluster: A metric cluster defines the correlation factors cu,v as a function of their distances in documents. Correlation matrix is a table containing the inverse of distance from each term in the stem list to all the terms in the vocabulary list. The correlation factor is calculated using the function given below:


 

V (Su) is the list of terms formed for a particular stem
Ki is a function that returns the nth occurrence of term k in document di 
After calculating the correlation factors for all the terms in the stem list, the metric clusters are computed using the function given below:

 
|V (Su) |is the number of terms formed from a stem
Cu,v  is the correlation factor calculated fffrom above
But in this project we used un-normalized clustering.
Association cluster: An association cluster is computed from a local correlation matrix Cl
We re-define the correlation factors cu,v between any pair of terms k u and k v, as follows:

 

fsu,j  is the frequency of the stem in document j
 fsv,j is the frequency of term in the vocabulary list in document j

The association clusters are computed using the following function:
 	
Cu,v is the value of correlation vectors calculated above.

3. Implementation of Metric clustering:



Implementation Steps
1.	To get text from a given URL I used the following code
import java.net.*;
import java.io.*;
public class URLReader {
public static void main(String[] args) throws Exception {
URL oracle = new URL("http://www.oracle.com/");
BufferedReader in = new BufferedReader(
new InputStreamReader(oracle.openStream()));
String inputLine;
while ((inputLine = in.readLine()) != null)
System.out.println(inputLine);
in.close();
}
}

2.	After getting the text from URL I created a HTML file for each URL.
3.	To parse a real time html file I used the Apache Tika jar file and implemented in the following manner:

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
public class HtmlParse {
   public static void main(final String[] args) throws IOException,SAXException, TikaException {
      //detecting the file type
      BodyContentHandler handler = new BodyContentHandler();
      Metadata metadata = new Metadata();
FileInputStream inputstream = new FileInputStream(new File("K:\\Master's\\2ndSEM\\Information Retrieval\\Projects\\FinalProject\\crawledPages\\1.html"));
      ParseContext pcontext = new ParseContext();
      
      //Html parser 
      HtmlParser htmlparser = new HtmlParser();
      htmlparser.parse(inputstream, handler, metadata,pcontext);
      System.out.println("Contents of the document:" + handler.toString());
      System.out.println("Metadata of the document:");
      String[] metadataNames = metadata.names();
            for(String name : metadataNames) {
         System.out.println(name + ":   " + metadata.get(name));  
      }
   }
}
4.	After parsing the file I stored the text into a Tree Map Data structure (docs) and deleted the file.

5.	From this data structure I retrieved vocabulary and their corresponding stems.

6.	Later I calculated the inverse distance of each term in a document to the query term.

7.	Now I computed total correlation of every word with query term by adding the above calculated correlation values.

8.	Finally I retrieved top 3 correlated values and stored in a datastructure.

9.	For each term now I have top 3 related words. Using these words now I can expand the query.

10.	Exception Handling 
  •	Mainly there are two types of Exceptions IO Exception and Maximum Limit Reached Exception.
•	IO Exception is occurred when we try to read a web page that has expired now and exists at the time of crawling. I handled it by taking top 10 ranked documents and if there is any expired link there will be an IO Exception then I considered the next best link to cluster.
•	The other Exception is WriterLimitReached Exception this occurs when a web page has more key words to read than 100000 characters
org.apache.tika.sax.WriteOutContentHandler$WriteLimitReachedException: Your document contained more than 100000 characters, and so your requested limit has been reached. To receive the full text of the document, increase your limit. 
	at org.apache.tika.sax.WriteOutContentHandler.characters(WriteOutContentHandler.java:141)
	at org.apache.tika.sax.ContentHandlerDecorator.characters(ContentHandlerDecorator.java:146)
	at org.apache.tika.sax.xpath.MatchingContentHandler.characters(MatchingContentHandler.java:85)
	at org.apache.tika.sax.ContentHandlerDecorator.characters(ContentHandlerDecorator.java:146)
	at org.apache.tika.sax.ContentHandlerDecorator.characters(ContentHandlerDecorator.java:146)
	at org.apache.tika.sax.SafeContentHandler.access$001(SafeContentHandler.java:46)
	at org.apache.tika.sax.SafeContentHandler$1.write(SafeContentHandler.java:82)
	at org.apache.tika.sax.SafeContentHandler.filter(SafeContentHandler.java:140)
	at org.apache.tika.sax.SafeContentHandler.characters(SafeContentHandler.java:287)
	at org.apache.tika.sax.XHTMLContentHandler.characters(XHTMLContentHandler.java:278)
	at org.apache.tika.sax.TextContentHandler.characters(TextContentHandler.java:55)
	at org.apache.tika.parser.html.HtmlHandler.characters(HtmlHandler.java:255)
	at org.apache.tika.sax.ContentHandlerDecorator.characters(ContentHandlerDecorator.java:146)
	at org.ccil.cowan.tagsoup.Parser.pcdata(Parser.java:994)
	at org.ccil.cowan.tagsoup.HTMLScanner.scan(HTMLScanner.java:582)
	at org.ccil.cowan.tagsoup.Parser.parse(Parser.java:449)
	at org.apache.tika.parser.html.HtmlParser.parse(HtmlParser.java:122)
	at QueryExpand.urlReader(QueryExpand.java:156)
	at QueryExpand.main(QueryExpand.java:88)

I handled this exception by passing -1 to disable the limit to the BodyContentHandler constructor
BodyContentHandler
public BodyContentHandler(int writeLimit)
Creates a content handler that writes XHTML body character events to an internal string buffer. The contents of the buffer can be retrieved using the ContentHandlerDecorator.toString() method.
The internal string buffer is bounded at the given number of characters. If this write limit is reached, then a SAXException is thrown.
Parameters:
writeLimit - maximum number of characters to include in the string, or -1 to disable the write limit
Since:
Apache Tika 0.7


11.	Finally returning the Expanded Query. 

    v) User interface and Comparisons with Google and Bing

Describe how you have designed the interface. 
I design search interface for two logical parts: Web UI and Controller Layer.
Fig1 shows soccer web search UI, and I use related skills like JSP, bootstrap, html, JQuery, etc to design it. Type query keywords in the text area and click “search” button, then request information shall be passed to controller layer. The controller layer which is designed by HttpServlet, and it is designed to communicate with UI  and backend model as a medium layer. For example, when controller receives query from UI, it will use the request info to call backend API to build indexing, get ranked search results, get clustering results and query expansion. This search web application is designed in popular MVC mode, and I choose Tomcat 8 as the web server.
 
                                Fig 1. 
How you have worked with the student that has generated the index – how you have accessed the relevance models to provide the results in you user interface. 
We sit and worked together two days to integrate related index parts with interface parts. We only initialize and build indexing once, then application can quickly access other relevance results. All the results are saved and encapsulated in some List collections. What needs to do is just call relevance APIs in HttpServlet (Control Layer) and set attributes for them. When Controller forwards the response information to UI, if the attributes are not null, JSP will automatically parse and display results in one table. 

Elaborate on the number of queries you have used for testing the search engine. 
How many were used in collaboration with the student that built the relevance models and how many did you generate on your own. 
I used many queries to test the search engine. The number is about 100. Also, use these queries tested in collaboration with student that built the relevance models. I list some of them: 
Queries: Messi, Barcelona, Brazil,  LA,  David Beckham,  Transfer,  Ronaldo, AC Milan, Red Bulls..
   
How did you collaborate with the student that produced clusters – to use the clustering information for relevance and presentation on the interface? 
We sit and worked together two days to integrate related clusters parts with interface parts. All clustering results related to queries are saved and encapsulated in one List collection called “clusterResult”. What needs to do is just call relevance APIs in HttpServlet (Control Layer) and set attribute for it. When Controller forwards the response information to UI, if the attribute of “clusterResult” is not null, JSP will automatically parse and display results in table.  
Here, I show codes in the HttpServlet:
------------------------------------------------------------------
SearchIndex.search(query, true);
List<String> clusterResult=SearchIndex.clusterResult;
request.setAttribute("clusterResult", clusterResult);	
request.getRequestDispatcher("2.jsp").forward(request, response);
--------------------------------------------------------------------

How do you think you search engine compares to Google and Bing. Explain your judgments. 
I try many queries and return Top 50 results for each to compare with results from Google and Bing. Most of them work quite well. The search results are very similar to results from Google and Bing. In some cases, my queries contain three or more keywords; the SE sometimes can’t return all the right results. This is because we only crawled 300,000 web pages as document collection. Compared with Bing or Google, the size of collection is too small, when more keywords need to be searched, there are not enough right results can be shown to end users.

If your search engine had a clustering component, how did you use the results of clustering in presenting the results of your search engine in the user interface? 
We sit and worked together two days to integrate related clusters parts with interface parts. In this case, we implement clustering component using simple K-means method, which K is valued as 10. Servlet(Controller layer ) calls APIs and get collection of all the related documents for each cluster. We choose 5 most close to centriod documents in each cluster and they are saved together and forwarded to SE web UI. Then JSP displays them to end users.

Discuss how you have decided to select the queries for the demonstration of your search engine. 

We decided the queries from different perspectives. Such as famous soccer players, different country names, soccer organizations, important soccer leagues, soccer team names, soccer history/ rule, and also some related player’s transfer information. 
Queries: Messi, Barcelona, Brazil, LA, David Beckham, Transfer, Ronaldo, AC Milan, Red Bulls, Super League, Word Cup, UEFA
