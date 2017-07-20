package utd.edu.ir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class SearchIndex {
    static String nonEng = ".*//((jp)|(de)|(ar)|(fr)|(it)|(pt))\\..*";
    static String index = "C:/Users/nali/workspace/IRProject/indexWithPageRank";
    static String content = "contents";
    static String title = "title";
    static boolean raw = false;
    static int hitsPerPage = 10;
    static int numPresent = 50;
    static IndexReader reader;
    static IndexSearcher searcher;
    static Analyzer analyzer;
    static MultiFieldQueryParser parser;
    static Map<Integer, Page> pages;
    static RankDoc[] docs;
    static List<RankDoc> rankDoc;
    static List<RankDoc> rankPage;
    static List<RankDoc> rankTopicPage;
    static List<RankDoc> rankHub;
    static List<RankDoc> rankAuthority;
    static List<RankDoc> rankQuery;
    static String expandedQuery;
    static Map<String, String> expandQuery;
    static Map<Integer, String> clusters;
    static Map<String, List<Integer>> clusterLinks;
    static List<String> clusterResult;
    
    public static void initialize() throws Exception {
        QueryExpand.initialize();
        reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        searcher = new IndexSearcher(reader);
        analyzer = new StandardAnalyzer();
        parser = new MultiFieldQueryParser(new String[] {title, content}, analyzer);
        WebGraph webGraph = new WebGraph();
        String file = "C:/Users/nali/Documents/cs/information retrieval/project/linkInfo";
        webGraph.readPages(file);
        file = "C:/Users/nali/Documents/cs/information retrieval/project/resultMap.txt";
        webGraph.readGraph(file);
        pages = webGraph.pages;
        readcluster();
        expandQuery = new HashMap<>();
    }

    private static void readcluster() throws Exception {
        clusters = new HashMap<>();
        clusterLinks = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("C:/Users/nali/Documents/cs/information retrieval/project/output1.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] cluster = line.split("\\s+");
                clusters.put(Integer.valueOf(cluster[0]), cluster[1]);
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader("C:/Users/nali/Documents/cs/information retrieval/project/output2.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] cluster = line.split("\\s+");
                List<Integer> list = new ArrayList<>();
                for (int i = 1; i < cluster.length; i++) {
                    list.add(Integer.valueOf(cluster[i]));
                }
                clusterLinks.put(cluster[0], list);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        initialize();
        Scanner in = new Scanner(System.in);
        System.out.println("Input the query:");
        while (in.hasNext()) {
            String q = in.nextLine();
            if (q.equals("q")) {
                break;
            }
            search(q, true);
            System.out.println("Input the query:");
        }
        in.close();

    }
    
    public static void search(String q, boolean extra) throws Exception {
        
        Query query = parser.parse(q);
        TopDocs results = searcher.search(query, 50 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
        docs = new RankDoc[hits.length];
        for (int i = 0; i < hits.length; i++) {
            docs[i] = new RankDoc(searcher, hits[i]);
        }
        rankDoc = getTopDoc(docs);
        expandedQuery = expandQuery.get(q);
        if (expandedQuery == null) {
            QueryExpand qe = new QueryExpand();
            try {
                expandedQuery = qe.urlReader(rankDoc, q);
                expandQuery.put(q, expandedQuery);
            } catch (Exception e) {
                ;
            }
        }
        
        
        if (extra) {
            rankPage = getPageRank(docs);
            rankTopicPage = getTopicPageRank(docs);
            Map<Integer, RankDoc> rootSet = new HashMap<>();
            int num = 0;
            for (RankDoc d : docs) {
                if (num++ < 200) {
                    rootSet.put(d.name, d);
                } else {
                    break;
                }
            }
            Map<Integer, RankDoc> baseSet = getBaseSet(rootSet, pages);
            hits(baseSet);
            RankDoc[] hitsDocs = baseSet.values().toArray(new RankDoc[0]);
            rankHub = getHubDocs(hitsDocs);
            rankAuthority = getAuthorityDocs(hitsDocs);
            clusterResult = new ArrayList<>();
            Set<String> exist = new HashSet<>();
            for (RankDoc rd : rankTopicPage) {
                String c = clusters.get(rd.name);
                if (c != null && !exist.contains(c)) {
                    exist.add(c);
                    for (int i : clusterLinks.get(c)) {
                        clusterResult.add(pages.get(i).url);
                    }
                }
            }
        }
        
        if (expandedQuery != null) {
            ScoreDoc[] newHits = searcher.search(parser.parse(expandedQuery), 200).scoreDocs;
            RankDoc[] expandDocs = new RankDoc[200];
            for (int i = 0; i < 200; i++) {
                expandDocs[i] = new RankDoc(searcher, newHits[i]);
            }
            rankQuery = getTopDoc(expandDocs);
        } else {
            rankQuery = new ArrayList<>();
        }
        System.out.println(rankQuery.size());
        System.out.println(clusterResult.size());
    }

    public static void doPagingSearch(Query query)
            throws Exception {
        TopDocs results = searcher.search(query, 50 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
        RankDoc[] docs = new RankDoc[hits.length];
        for (int i = 0; i < hits.length; i++) {
            docs[i] = new RankDoc(searcher, hits[i]);
        }

        int numTotalHits = results.totalHits;
        System.out.println(numTotalHits + " total matching documents");

        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);
        int links = 0;
        Set<String> existed = new HashSet<>();
        for (int i = start; i < numTotalHits; i++) {
            if (raw) { // output raw format
                System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
                continue;
            }

            Document doc = searcher.doc(hits[i].doc);
            String url = doc.get("url");
            String title = doc.get("title");
            if (url.matches(nonEng)||url.matches(".*//m\\..*") || title == null || existed.contains(title) || title.trim().equals("no title")) {
                continue;
            }
            existed.add(title);
            System.out.println((links + 1) + ". " + url);
            System.out.println("   Title: " + doc.get("title"));
            if (++links >= end) {
                break;
            }
        }
        List<RankDoc> pageRankDocs = getPageRank(docs);
        for (RankDoc rankDoc : pageRankDocs) {
            System.out.println(rankDoc.url);
            System.out.println(rankDoc.title);
            System.out.println(rankDoc.pageRank);
        }
        List<RankDoc> topicPageRankDocs = getTopicPageRank(docs);
        for (RankDoc rankDoc : topicPageRankDocs) {
            System.out.println(rankDoc.url);
            System.out.println(rankDoc.title);
            System.out.println(rankDoc.topicPageRank);
        }
        
        Map<Integer, RankDoc> rootSet = new HashMap<>();
        int num = 0;
        for (RankDoc d : docs) {
            if (num++ < 200) {
                rootSet.put(d.name, d);
            } else {
                break;
            }
        }
        Map<Integer, RankDoc> baseSet = getBaseSet(rootSet, pages);
        hits(baseSet);
        RankDoc[] hitsDocs = baseSet.values().toArray(new RankDoc[0]);
        List<RankDoc> hubDocs = getHubDocs(hitsDocs);
        for (RankDoc rankDoc : hubDocs) {
            System.out.println(rankDoc.url);
            System.out.println(rankDoc.title);
            System.out.println(rankDoc.hub);
        }
        List<RankDoc> authorityDocs = getAuthorityDocs(hitsDocs);
        for (RankDoc rankDoc : authorityDocs) {
            System.out.println(rankDoc.url);
            System.out.println(rankDoc.title);
            System.out.println(rankDoc.authority);
        }
    }

    public static List<RankDoc> getAuthorityDocs(RankDoc[] hitsDocs) {
        Arrays.sort(hitsDocs, new Comparator<RankDoc>() {

            @Override
            public int compare(RankDoc doc1, RankDoc doc2) {
                if (doc1.authority > doc2.authority) {
                    return -1;
                } else if (doc1.authority < doc2.authority) {
                    return 1;
                }
                return 0;
            }
        });
        return getTopDoc(hitsDocs);
    }

    public static List<RankDoc> getHubDocs(RankDoc[] hitsDocs) {
        Arrays.sort(hitsDocs, new Comparator<RankDoc>() {

            @Override
            public int compare(RankDoc doc1, RankDoc doc2) {
                if (doc1.hub > doc2.hub) {
                    return -1;
                } else if (doc1.hub < doc2.hub) {
                    return 1;
                }
                return 0;
            }
        });
        return getTopDoc(hitsDocs);
    }

    public static void hits(Map<Integer, RankDoc> baseSet) {
        int iteration = 5;
        while (iteration-- > 0) {
            double norm = 0;
            for (RankDoc doc : baseSet.values()) {
                doc.authority = 0;
                for (RankDoc d : doc.inDocs) {
                    doc.authority += d.hub;
                }
                norm += doc.authority * doc.authority;
            }
            norm = Math.sqrt(norm);
            for (RankDoc doc : baseSet.values()) {
                doc.authority /= norm;
            }
            norm = 0;
            for (RankDoc doc : baseSet.values()) {
                doc.hub = 0;
                for (RankDoc d : doc.outDocs) {
                    doc.hub += d.authority;
                }
                norm += doc.hub * doc.hub;
            }
            norm = Math.sqrt(norm);
            for (RankDoc doc : baseSet.values()) {
                doc.hub /= norm;
            }
        }
        
    }

    public static Map<Integer, RankDoc> getBaseSet(Map<Integer, RankDoc> rootSet, Map<Integer, Page> pages) {
        Map<Integer, RankDoc> baseSet = new HashMap<>();
        baseSet.putAll(rootSet);
        for (Map.Entry<Integer, RankDoc> entry : rootSet.entrySet()) {
            Page page = pages.get(entry.getKey());
            RankDoc rankDoc = entry.getValue();
            for (int outPage : page.outEdges) {
                RankDoc doc;
                if (!baseSet.containsKey(outPage)) {
                    doc = new RankDoc(outPage);
                    baseSet.put(outPage, doc);
                } else {
                    doc = baseSet.get(outPage);
                }
                doc.inDocs.add(rankDoc);
                entry.getValue().outDocs.add(doc);
            }
            for (int inPage : page.inEdges) {
                if (rankDoc.inDocs.size() > 100) {
                    break;
                }
                RankDoc doc;
                if (!baseSet.containsKey(inPage)) {
                    doc = new RankDoc(inPage);
                    baseSet.put(inPage, doc);
                } else {
                    doc = baseSet.get(inPage);
                }
                doc.inDocs.add(entry.getValue());
                entry.getValue().outDocs.add(doc);
            }
        }
        return baseSet;
    }

    public static List<RankDoc> getPageRank(RankDoc[] docs) {
        Arrays.sort(docs, new Comparator<RankDoc>() {

            @Override
            public int compare(RankDoc doc1, RankDoc doc2) {
                double rank1 = doc1.score + 1000 * doc1.pageRank;
                double rank2 = doc2.score + 1000 * doc2.pageRank;
                if (rank1 > rank2) {
                    return -1;
                } else if (rank1 < rank2) {
                    return 1;
                }
                return 0;
            }
            
        });
        return getTopDoc(docs);
    }

    public static List<RankDoc> getTopDoc(RankDoc[] docs) {
        Set<String> existed = new HashSet<>();
        List<RankDoc> result = new ArrayList<>();
        for (int i = 0; i < docs.length; i++) {
            if (docs[i].url==null||docs[i].url.matches(nonEng)||docs[i].url.matches(".*//m\\..*") || docs[i].title == null || existed.contains(docs[i].title) || docs[i].title.equals("no title")) {
                continue;
            }
            existed.add(docs[i].title);
            result.add(docs[i]);
            if (result.size() == numPresent) {
                break;
            }
        }
        return result;
    }

    public static List<RankDoc> getTopicPageRank(RankDoc[] docs) {
        Arrays.sort(docs, new Comparator<RankDoc>() {

            @Override
            public int compare(RankDoc doc1, RankDoc doc2) {
                double rank1 = doc1.score + Math.log(doc1.topicPageRank);
                double rank2 = doc2.score + Math.log(doc2.topicPageRank);
                if (rank1 > rank2) {
                    return -1;
                } else if (rank1 < rank2) {
                    return 1;
                }
                return 0;
            }
            
        });
        return getTopDoc(docs);
    }
}
