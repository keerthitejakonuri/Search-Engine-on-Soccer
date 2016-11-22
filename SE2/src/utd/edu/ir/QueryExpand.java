package utd.edu.ir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author keert
 */
public class QueryExpand {

    static protected StanfordCoreNLP pipeline;
    static ArrayList<String> al = new ArrayList();
    static ArrayList<String> bl = new ArrayList();
    List<String> QuestionLemma = new ArrayList<String>();
    TreeMap<Integer, List<String>> docs = new TreeMap();
    Set<String> vocabulary = new TreeSet<String>();
    List<String> vocabularyList = new ArrayList<String>();
    TreeMap<String, List<Integer>> QuestionLemmaPosition = new TreeMap();
    TreeMap<String, List<Integer>> findPos = new TreeMap();
    String expandedQuery = "";
    String newexpandedQuery = "";

    public static void initialize() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
        stopWords();
        commonWords();
    }
    // // static TreeMap<String,TreeMap<String,List<Integer>>> findCor= new
    // TreeMap();
    // public QueryExpand() {
    //
    // }

    public static void main(String args[]) {
        initialize();
        QueryExpand e1 = new QueryExpand();
        QueryExpand e2 = new QueryExpand();
        System.out.println(" The Query is");
        String query = "Barcelona Messi";
        System.out.println(query);
        List<String> urls = new ArrayList<String>();
        urls.add("http://www.mirror.co.uk/sport/football/news/barcelona-vs-sporting-gijon-live-7819235");
        urls.add("https://twitter.com/search?q=barcelona+messi&ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Esearch");
        urls.add("http://www.si.com/planet-futbol/2016/04/23/lionel-messi-barcelona-sporting-gijon-goal-video");
        urls.add("http://www.fcbarcelona.com/football/first-team/staff/players/messi");
        urls.add(
                "http://www.cbssports.com/soccer/eye-on-soccer/25561224/watch-messis-amazing-assist-sets-up-suarez-for-barcelonas-second-goal");
                // System.out.println("The List of Urls are "+urls);
//        System.out.println("Expanded Query using Metric clustering is"+e1.urlReader(urls,query));

        // String[] queryTerm = query.split("\\s+");
        // ArrayList<String> queryTerms = new
        // ArrayList<String>(Arrays.asList(queryTerm));

    }

    public String urlReader(List<RankDoc> urls, String query) {
        int i = 0;
        query = query.toLowerCase();
        query = query.replaceAll("\\?", "");
        expandedQuery = expandedQuery + query + " ";
        System.out.println("The Query is " + query);
        List<String> l = lemmatize(query);
        Iterator<String> it = l.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            key = replaceSpecialCharacters(key);
            if (!al.contains(key) && !bl.contains(key)) {
                QuestionLemma.add(key);
            }
        }
        System.out.println(QuestionLemma);
        /* For Reading Query */

        /* For retrieving text from URL */
        for (i = 0; i < 5; i++) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();

            FileInputStream inputstream;
            try {
                URL oracle = new URL(urls.get(i).url);
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                String inputLine = "";
                String inputLine1 = "";
                while ((inputLine1 = in.readLine()) != null)
                    inputLine = inputLine + inputLine1;
                inputLine = inputLine.replaceAll("\\.", "");
                // System.out.println(inputLine); Content from Html is retrieved
                in.close();
                // InputStream is = new ByteArrayInputStream(str.getBytes());
                // InputStream stream = new
                // ByteArrayInputStream(inputLine.getBytes());
                File myFile = new File("html" + i + ".txt");
                FileWriter fW = new FileWriter(myFile);
                if (myFile.delete()) {
                    myFile.createNewFile();
                    fW.write(inputLine);
                    fW.flush();
                    fW.close();
                } else {
                    myFile.createNewFile();
                    fW.write(inputLine);
                    fW.flush();
                    fW.close();
                }
                inputstream = new FileInputStream(new File("html" + i + ".txt"));
                ParseContext pcontext = new ParseContext();
                HtmlParser htmlparser = new HtmlParser();
                htmlparser.parse(inputstream, handler, metadata, pcontext);
                QueryExpand qe1 = new QueryExpand();

                List<String> q = qe1.lemmatize(handler.toString().toLowerCase());
                q.removeAll(al);
                q.removeAll(bl);
                for(int p = 0; p < QuestionLemma.size(); p++)        //code to be replaced
                    if(!q.contains(QuestionLemma.get(p)))
                        q.add(QuestionLemma.get(p));
                for (int k = 0; k < q.size(); k++) {
                    String s = replaceSpecialCharacters(q.get(k));
                    if (!al.contains(s) && !bl.contains(s)) {
                        vocabulary.add(s);
                    }

                }
                vocabulary.removeAll(al);
                vocabulary.removeAll(bl);
                // System.out.println(" Vocabulary Set is "+vocabulary.size());
                docs.put(i, q);
                // System.out.println(docs.get(i));
            } catch (Exception ex) {
                // Logger.getLogger(Weights.class.getName()).log(Level.SEVERE,
                // null, ex);
                return query + " soccer";
            } 
        }
        // System.out.println(" Vocabulary Set is "+vocabulary.size());

        Iterator<String> itr = vocabulary.iterator();
        while (itr.hasNext()) {
            vocabularyList.add(itr.next());
        }
        List<Integer> pos;
        for (i = 0; i < vocabularyList.size(); i++) {
            pos = new ArrayList<Integer>();
            for (int j = 0; j < docs.size(); j++) {
                // System.out.println(docs.get(j).indexOf(vocabularyList.get(i)));
                pos.add(docs.get(j).indexOf(vocabularyList.get(i)));
            }
            // System.out.println(vocabularyList.get(i) + pos);
            findPos.put(vocabularyList.get(i), pos);
            // pos.clear();
        }
        // System.out.println("Find Position "+ findPos.get("Messi"));
        /* For finding the positions of lemmas */
//        vocabularyList.removeAll(QuestionLemma);
        for (i = 0; i < QuestionLemma.size(); i++) {
            
            if(findPos.get(QuestionLemma.get(i))== null)
            {
                return query + " football " + " sport "; 
            }
            // System.out.println(findPos.get(QuestionLemma.get(i)));
            for (int z = 0; z < findPos.get(QuestionLemma.get(i)).size(); z++) {
                QuestionLemmaPosition.put(QuestionLemma.get(i), findPos.get(QuestionLemma.get(i)));
            }
        }
        // System.out.println(QuestionLemmaPosition);
        /* For finding the positions of lemmas */
        /*
         * for(Map.Entry<String,List<Integer>> m:findPos.entrySet()) {
         * List<Integer> dis = m.getValue(); }
         */
        /* For retrieving text from URL */
        for (int z = 0; z < QuestionLemmaPosition.size(); z++) {
            TreeMap<Double, String> findCor = new TreeMap<>();
            NavigableSet nset = findCor.descendingKeySet();
            List<Integer> lis = QuestionLemmaPosition.get(QuestionLemma.get(z));
            // System.out.print(lis);
            for (Map.Entry<String, List<Integer>> entry : findPos.entrySet()) {
                String key = entry.getKey();
                // System.out.println(key);

                List<Integer> value = entry.getValue();
                double distance = 0;
                double metric = 0;
                for (int docnum = 0; docnum < value.size(); docnum++) {
                    if (value.get(docnum) == -1) {
                        value.set(docnum, Integer.MAX_VALUE);
                    }
                    distance = Math.abs(lis.get(docnum) - value.get(docnum));

                    if (distance == 0)
                        distance = Integer.MAX_VALUE;
                    metric = metric + 1 / distance;
                    findCor.put(metric, key);
                    nset = findCor.descendingKeySet();

                }

            }

            Iterator<Integer> iterator = nset.iterator();
            int count = 0;

            while (iterator.hasNext() && count < 1) {

                if (!al.contains(findCor.get(iterator.next())) && !bl.contains(findCor.get(iterator.next()))) {
                    count = count + 1;
                    expandedQuery = expandedQuery + (findCor.get(iterator.next())) + " ";
                }
            }
        }

        String arr[] = expandedQuery.split(" ");

        Set<String> set = new HashSet<String>();
        for (i = 0; i < arr.length; i++) {
            // System.out.println(arr[i]);
            if (set.add(arr[i]) == false) { // your duplicate element } }

            } else {
                newexpandedQuery = newexpandedQuery + arr[i] + " ";
            }
        }

        return newexpandedQuery;
    }

    public static String replaceSpecialCharacters(String sCurrentLine) {
        sCurrentLine = sCurrentLine.toString().replaceAll("<.*>", "");
        sCurrentLine = sCurrentLine.replaceAll("'s", "");
        sCurrentLine = sCurrentLine.replaceAll("[+^:,?';=%#&~`$!@*_)/(}{]", "");
        sCurrentLine = sCurrentLine.replaceAll("-", "\t");
        sCurrentLine = sCurrentLine.replaceAll("\\.", "");
        sCurrentLine = sCurrentLine.replaceAll("\\d*", "");
        sCurrentLine = sCurrentLine.replaceAll("[^\\x00-\\x7F]", "");
        sCurrentLine = sCurrentLine.replaceAll("[^\\p{L}\\p{Nd}]+", "");
        sCurrentLine = sCurrentLine.toLowerCase();
        // sCurrentLine=sCurrentLine.replaceAll(" +"," ");
        return sCurrentLine;
    }

    public List<String> lemmatize(String documentText) {
        List<String> lemmas = new LinkedList<>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        pipeline.annotate(document);
        // Iterate over all of the sentences found
        String temp = "";
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
                temp = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (!al.contains(temp) && !bl.contains(temp)) {
                    lemmas.add(temp);
                }

            }
        }
        return lemmas;
    }

    public  static void stopWords(){
        BufferedReader br1=null;
        try{
        br1 = new BufferedReader(new FileReader("C:/Users/nali/Documents/cs/information retrieval/project/stopwords")); 
        String sCurrentLine;
                while ((sCurrentLine = br1.readLine()) != null) {
    al.add(sCurrentLine);
                }
    br1.close();
        }
        catch(Exception e){
       
        e.printStackTrace();
        }
        }
        
        public  static void commonWords(){
        BufferedReader br1=null;
        try{
        br1 = new BufferedReader(new FileReader("C:/Users/nali/Documents/cs/information retrieval/project/common_words")); 
        String sCurrentLine;
                while ((sCurrentLine = br1.readLine()) != null) {
    bl.add(sCurrentLine);
                }
    br1.close();
        }
        catch(Exception e){
       
        e.printStackTrace();
        }
        }
}
