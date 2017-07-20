
package utd.edu.ir;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WebGraph {
    
    Map<Integer, Page> pages = new TreeMap<>();
    int relativePages = 274272;
    String maxInDoc = " ";
    int maxInLinks = 0;
    String maxOutDoc = "";
    int maxOutLinks = 0;
    long totalLinks = 0;
    double largestPageRank = 0;
    int largestPage = 0;
    double largestTopicPageRank = 0;
    int largestTopicPage = 0;
    

    public void calculatePageRank() {
        int iteration = 40;
        double factor = 1.5 / pages.size();
        while (iteration > 0) {
            if (iteration % 2 == 0) {
                for (Page page : pages.values()) {
                    double otherRank = 0;
                    for (int incoming : page.inEdges) {
                        Page other = pages.get(incoming);
                        otherRank += other.pageRank / other.outEdges.size();
                    }
                    page.prevPageRank = factor + 0.85 * otherRank;
                    if (page.prevPageRank > largestPageRank) {
                        largestPageRank = page.prevPageRank;
                        largestPage = page.name;
                    }
                }
                System.out.println("pageRank of web page 1000001 is " +pages.get(1000001).url + pages.get(1000001).prevPageRank);
            } else {
                for (Page page : pages.values()) {
                    double otherRank = 0;
                    for (int incoming : page.inEdges) {
                        Page other = pages.get(incoming);
                        otherRank += other.prevPageRank / other.outEdges.size();
                    }
                    page.pageRank = factor + 0.85 * otherRank;
                    if (page.pageRank > largestPageRank) {
                        largestPageRank = page.pageRank;
                        largestPage = page.name;
                    }
                }
//                System.out.println("pageRank of web page 1000001 is " +pages.get(1000001).pageRank);
            }
            iteration--;
        }
        System.out.println("largest Page Rank is: " + largestPage + " " + largestPageRank);
    }
    
    public void calculateTopicPageRank() {
        int iteration = 40;
        while (iteration > 0) {
            if (iteration % 2 == 0) {
                for (Page page : pages.values()) {
                    double otherRank = 0;
                    for (int incoming : page.inEdges) {
                        Page other = pages.get(incoming);
                        otherRank += other.topicPageRank / other.outEdges.size();
                    }
                    page.prevTopicPageRank = 0.15 * page.topicFactor + 0.85 * otherRank;
                    if (page.prevTopicPageRank > largestTopicPageRank) {
                        largestTopicPageRank = page.prevTopicPageRank;
                        largestTopicPage = page.name;
                    }
                }
//                System.out.println("pageRank of web page 1000001 is " +pages.get(1000001).prevTopicPageRank);
            } else {
                for (Page page : pages.values()) {
                    double otherRank = 0;
                    for (int incoming : page.inEdges) {
                        Page other = pages.get(incoming);
                        otherRank += other.prevTopicPageRank / other.outEdges.size();
                    }
                    page.topicPageRank = 0.15 * page.topicFactor + 0.85 * otherRank;
                    if (page.topicPageRank > largestTopicPageRank) {
                        largestTopicPageRank = page.topicPageRank;
                        largestTopicPage = page.name;
                    }
                }
                System.out.println("pageRank of web page 1000001 is " +pages.get(1000001).topicPageRank);
            }
            iteration--;
        }
        System.out.println("largest Topic Page Rank is: " + largestTopicPage + " " + largestTopicPageRank);
    }
    
    public void readPages(String file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] info = line.split("\\s+");
                int name = Integer.valueOf(info[0]);
                String url = info[1];
                if (!url.matches("(.*store\\..*)|(.*shop\\..*)|(.*disney.*)|(.*live-shows.*)|(.*audioboom.*)|(.*tantek.*)|(.*gmpg\\..*)|(.*bkms-system.*)")) {
                    Page page = new Page(name, url);
                    pages.put(name, page);
                    if (url.matches("(.*uefa.*)|(.*football.*)|(.*soccer.*)|(.*fifa.*)|(.*espnfc.*)|(.*goal\\.com.*)")) {
                        page.topicFactor = 10.0 /relativePages;
                    }
                }
            }
        }
    }

    public void readGraph(String file) throws FileNotFoundException, IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] pageInfo = line.split(":");
                Page page = pages.get(Integer.valueOf(pageInfo[0]));
                if (page != null) {
                    page.pageRank = 10.0 / pages.size();
                    if (pageInfo.length == 2) {
                        String[] links = pageInfo[1].split("\\s+");
                        for (String p : links) {
                            Integer name = Integer.valueOf(p);
                            if (pages.containsKey(name)) {
                                page.outEdges.add(name);
                                Page temp = pages.get(name);
                                temp.inEdges.add(page.name);
                                if (temp.inEdges.size() > maxInLinks) {
                                    maxInLinks = temp.inEdges.size();
                                    maxInDoc = p;
                                }
                            }
                        }
                        int outEdge = page.outEdges.size();
                        totalLinks += outEdge;
                        if (outEdge > maxOutLinks) {
                            maxOutLinks = outEdge;
                            maxOutDoc = pageInfo[0];
                        }
                    }
                    count++;
                }
                if (count % 100000 == 0) {
                    System.out.println("Start reading webpage: " + count);;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        String file = "linkInfo";
        WebGraph webGraph = new WebGraph();
        webGraph.readPages(file);
        System.out.println("the number of nodes are: " + webGraph.pages.size());
        System.out.println("the number of relatvie pages is: " + webGraph.relativePages);
        file = "resultMap.txt";
        webGraph.readGraph(file);
        System.out.println("the total number of links in the graph is: " + webGraph.totalLinks);
        System.out.println("the max inlink document is " + webGraph.maxInDoc + " with " + webGraph.maxInLinks + " links");
        System.out.println("the max outlink document is " + webGraph.maxOutDoc + " with " + webGraph.maxOutLinks + " links");
        webGraph.calculatePageRank();
        webGraph.calculateTopicPageRank();
        System.out.println("the time cost is: " + (System.currentTimeMillis() - start) / 1000);

    }

}

class Page {
    int name;
    String url;
    double topicFactor;
    double pageRank = 0.15;
    double prevPageRank = 0.15;
    double topicPageRank = 0;
    double prevTopicPageRank = 0;
    List<Integer> inEdges = new ArrayList<>();
    List<Integer> outEdges = new ArrayList<>();
    
    public Page(int name, String url) {
        this.name = name;
        this.url = url;
    }
    
    public Page(int name, String url, double topicFactor) {
        this.name = name;
        this.url = url;
        this.topicFactor = topicFactor;
    }
    
    public int hashCode() {
        return this.name;
    }
    
    public boolean equals(Object other) {
        Page page = (Page) other;
        return this.name == page.name;
    }
}
