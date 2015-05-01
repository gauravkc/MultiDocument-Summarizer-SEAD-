/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package multidocumentsummarization;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author eeestuds
 */
public class multiDocumentSummarization {

    /**
     * @param args the command line arguments
     */
    static ArrayList<String> content = new ArrayList<String>();
    static int noOfFilesFetched = 0;
    static Map<String, String> sentences = new HashMap<String, String>();//sentences and their id: docId and (sentenceId or sentenceNumberWithinDoc)
    public static void main(String[] args) throws FileNotFoundException, InterruptedException, MalformedURLException, BoilerpipeProcessingException, UnsupportedEncodingException, IOException {
        // taking input of all docs gathered from web
        
        // Gaurav Get docs from urls
        
        URLReader ob = new URLReader();
        ob.getDocs();
        /*while (noOfFilesFetched < 5) { //5 is the no. of files being fetched
            Thread.sleep(10);
        }*/
        // Gaurav End
        
        //to be removed and above /* */ shd be kept
        /*String folderPath = "C:\\wamp\\www\\mdsnew\\test data\\Farmer suicide";//path of the folder containing required docs
        File folder = new File(folderPath);
        File[] listOfDocs = folder.listFiles();
        
        for (File doc : listOfDocs) {
            if (doc.isFile()) {
                String docName = doc.getName();
                //System.out.println(docName);
                
                /*
                if (true) {
                    String docContent = new Scanner(new File(folderPath + "\\" + "y11.txt")).useDelimiter("\\Z").next();
                    content.add(docContent);
                    System.out.println("\n**********************\n" + docContent + "\n**********************\n");
                }*///change in sri code
                
                /*String docContent = readFile(folderPath + "\\" + docName, StandardCharsets.ISO_8859_1);
                content.add(docContent);
                //System.out.println("\n**********************\n" + docContent + "\n**********************\n");
                
            }
        }
        //to be removed
        //System.out.println("content size " + content.size());
        */
        //initializations go here
        Map<String, Integer> wordFreq = new HashMap<String, Integer>();//all unique words and their freq in cluster
        
        Map<String, Integer> docs = new HashMap<String, Integer>();//documents in the cluster and corresponding id
        Map<String, Double> sentencePositionRank = new HashMap<String, Double>();
        ArrayList<String> distinctWords = new ArrayList<String>();//distinct words: used for bag of words
        int docCount = 0;
        int sentenceCount = 0;
        int fileIndex;
        
        for (fileIndex = 0;fileIndex < content.size();fileIndex++) {
            String docContent = content.get(fileIndex);
            docContent = docContent.trim();//removing any extra white spaces
            docCount++;//incrementing document counter
            docs.put(docContent, docCount);//storing each document read
            sentenceCount = 0;
            //System.out.println("docContent ----------------\n" + docContent);

            String tempSentence = "";
            for (int i = 0;i < docContent.length();i++) {
                tempSentence = tempSentence + docContent.charAt(i);
                if ((docContent.charAt(i) == '\n' || docContent.charAt(i) == '\r') || ((docContent.charAt(i) == '.' || docContent.charAt(i) == '!' || docContent.charAt(i) == '?')/**/ && i + 1 < docContent.length() && docContent.charAt(i + 1) == ' '/**/)) {
                    sentenceCount++;
                    //i++;//since its a space ' '
                    tempSentence = tempSentence.trim();
                 
                    tempSentence.replaceAll("[\n\r\t]", "");
                    
                    if (tempSentence.length() > 40) {
                        //System.out.println(tempSentence);
                        //Thread.sleep(5000);
                        int numCount = 0;
                        for (int k = 0;k < tempSentence.length();k++) {
                            if ((int)(tempSentence.charAt(k) - '0') >= 0 && (int)(tempSentence.charAt(k) - '0') <= 9) {
                                numCount++;
                            }
                        }
                        if (numCount < 9) {
                            sentences.put(tempSentence, docCount + ", " + sentenceCount);
                        } else {
                            //System.out.println(tempSentence);
                            tempSentence = "";
                            continue;
                        }
                        numCount = 0;
                    } else {
                        tempSentence = "";
                        continue;
                    }
                    if (tempSentence.length() > 0)
                    sentences.put(tempSentence, docCount + ", " + sentenceCount);
                    //System.out.println("-------------------tempssss----------------------------"+ tempSentence);
                    //entering words into wordFreq map
                   // System.out.println("Document ");
                    String[] words = tempSentence.split("\\s+|,|:|!|\\?|\\.|-|\\(|\\)|\"|\\|");//splitting sentences based on these delimiters
                    for (int j = 0;j < words.length;j++) {
                       // System.out.println(words[j]);
                        words[j] = words[j].trim();
                        if (words[j].length() == 0) {
                            continue;
                        }
                        words[j] = words[j].toLowerCase();
                        
                        //Thread.sleep(1000);
                        if (wordFreq.get(words[j]) != null) {
                            wordFreq.put(words[j], wordFreq.get(words[j]) + 1);
                        } else {
                            wordFreq.put(words[j], 1);
                            distinctWords.add(words[j]);
                        }
                    }
                    tempSentence = "";
                }
            }
            Iterator itSentences = sentences.entrySet().iterator();
            while (itSentences.hasNext()) {
                Map.Entry pair = (Map.Entry) itSentences.next();
                String pairKey = (String) pair.getKey();
                String pairVal = (String) pair.getValue();
                String[] tokens = pairVal.split(",");
                int sentenceNumber = 0;
                for (int k = 1;k < tokens.length;k++) {
                    tokens[k] = tokens[k].trim();
                    sentenceNumber = Integer.parseInt(tokens[k]);
                }
                double sentencePos = ((sentenceNumber - 1) * 1.0) / (sentenceCount - 1);
                sentencePositionRank.put(pairKey, sentencePos);
            }
        }
        
        //printMap(sentences);
        //printMap(wordFreq);
        //System.out.println("sentencessss " + sentences.size());
        
        //calculation of idf goes here
        Map<String, Double> wordIdf = new HashMap<String, Double>();
        
        Iterator it = wordFreq.entrySet().iterator();//map of words n their freq in cluster
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String tempWord = (String) pair.getKey();
            double n1 = 0.000000001;//denominator of idf, numerator is same as docCount
            Iterator it1 = docs.entrySet().iterator();
            
            while (it1.hasNext()) {
                Map.Entry pair1 = (Map.Entry) it1.next();
                String docContent = (String) pair1.getKey();
                docContent = docContent.toLowerCase();
                if (docContent.contains(tempWord)) {
                    n1++;
                }
            }
            wordIdf.put(tempWord, log (docCount / n1));//denominator shd not be zero, initialize with low val
        }
        
        //calculation of centroid goes here
        
        Map<String, Double> wordTfxIdf = new HashMap<String, Double>();
        double tfxIdfMinThreshold = 1;//to be set
        double tfxIdfMaxThreshold = 3; // to be set
        //array list can be used to store centroid instead of map
        Iterator itWordFreq = wordFreq.entrySet().iterator();
        
//        //ritz code starts
//        PrintWriter writer = new PrintWriter("D:\\tfidf2.txt");
//        int tcount = 1;
//        double max = -1;
//        //ritz code ends
        
        while (itWordFreq.hasNext()) {
            Map.Entry pair = (Map.Entry) itWordFreq.next();
            int tempTf = (int) pair.getValue();
            double tempIdf = wordIdf.get(pair.getKey());
            double tempTfxIdf = tempTf * tempIdf;
            if (tempTfxIdf >= tfxIdfMinThreshold && tempTfxIdf <= tfxIdfMaxThreshold) {
                wordTfxIdf.put((String) pair.getKey(), tempTfxIdf);
                //System.out.println(pair.getKey() + " has tfxidf value of " + tempTfxIdf);
                
//                //ritz code
//                writer.println(tcount++ + "," + tempTfxIdf);
//                if (tempTfxIdf > max) {
//                    max = tempTfxIdf;
//                }
//                //r ends
            }
        }
        //centroid calculation ends
        
//        //ritz code
//        writer.close();
//        System.out.println("max val is " + max);
//        //r ends
        
        //calculation of sentence salience based on centroid obtained above
        
        Map<String, Integer> sentenceCentroidVal = new HashMap<String, Integer>();
        Iterator itSentence = sentences.entrySet().iterator();
        while (itSentence.hasNext()) {
            Map.Entry pair = (Map.Entry) itSentence.next();
            String tempSentence = (String) pair.getKey();
            //for each word in sentence we need to see if it is in centroid
            String[] tempWords = tempSentence.split("\\s+|,|:|!|\\?|\\.|-|\\(|\\)|\"|\\|");
            int impCount = 0;
            for (int j = 0;j < tempWords.length;j++) {
                tempWords[j] = tempWords[j].toLowerCase();
                if (wordTfxIdf.containsKey(tempWords[j])) {
                    impCount++;
                }
            }
            sentenceCentroidVal.put(tempSentence, impCount);
        }
        
        //creating another map which is in descending order of impCount
        Map sentenceCentroidValSorted = sortByValue(sentenceCentroidVal);
        //printing sentences based on their importance
        //printMap(sentenceCentroidValSorted);
        
        Iterator itSentenceSorted = sentenceCentroidValSorted.entrySet().iterator();
        Map<String, Integer> rankedSentenceCentroid = new HashMap<String, Integer>();//assigning ranks to sentences based on sentenceCentroidValSorted map
        int rank = 0;
        
        while (itSentenceSorted.hasNext()) {
            Map.Entry pair = (Map.Entry) itSentenceSorted.next();
            String tempSentence = (String) pair.getKey();
            rank++;
            rankedSentenceCentroid.put(tempSentence, rank);
        }
        //centroid based method ends here
        
        //degree based method starts here
        
        /*creating bag of words model*/
        Collections.sort(distinctWords);//sorted array of words
        Map<String, ArrayList> bagOfWords = new HashMap<String, ArrayList>();
        itSentence = sentences.entrySet().iterator();
        int noOfDistinctWords = wordFreq.size();
        //System.out.println("wordfreq size is" + wordFreq.size());
        //System.out.println("distinctWords size is" + distinctWords.size());
        //System.out.println("sentences " + sentences.size());
        while (itSentence.hasNext()) {
            Map.Entry pair = (Map.Entry) itSentence.next();
            String tempSentence = (String) pair.getKey();
            ArrayList<Integer> tempList = new ArrayList<Integer>();
            for (int i = 0;i < noOfDistinctWords;i++) {//initializing with zero
                tempList.add(0);
            }
            String[] tempWords = tempSentence.split("\\s+|,|:|!|\\?|\\.|-|\\(|\\)|\"|\\|");
            for (int i = 0;i < tempWords.length;i++) {
                if (tempWords[i].length() == 0) {
                    continue;
                }
                tempWords[i] = tempWords[i].toLowerCase();
                //System.out.println("tempWord size is " + tempWords[i].length());
                int index = distinctWords.indexOf(tempWords[i]);
                //System.out.println("index " + index);//array outof bound since index = -1, implies it din't find that word, which occurs only if word is empty or of length zero in this case
                tempList.set(index, tempList.get(index) + 1);//bag of words implementation
            }
            bagOfWords.put(tempSentence, tempList);
        }
        /*end of bag of words*/
        
        ArrayList<ArrayList<Double> > cosineSimilarity = new ArrayList<ArrayList<Double> >();
        double avgSimilarity = 0;
        int itemp = 0, jtemp = 0;
        
        Iterator itBagi = bagOfWords.entrySet().iterator();
        //System.out.println("cdvshgnfnbzdjhvfvhb " + bagOfWords.size());
        while (itBagi.hasNext()) {
            ArrayList<Double> rowCosine = new ArrayList<Double>();//row in the cosine matrix
            Map.Entry pair = (Map.Entry) itBagi.next();
            String tempSentencei = (String) pair.getKey();
            //System.out.println("aaaaaaaaaaaa  " + tempSentencei);
            ArrayList<Integer> listi = (ArrayList<Integer>) pair.getValue();
            Iterator itBagj = bagOfWords.entrySet().iterator();
            
            double denominatori = 0.00000000000001;//so that denominator doesn't become 0 in any worst case
            ArrayList<Integer> iwordIndex = new ArrayList<Integer>();//contains indices where freq of word is greater than zero -- we know this particular word is present in ith sentence -- used for calculating numerator since numerator calculation is based on words present in both sentences
            
            for (int i = 0;i < listi.size();i++) {
                double tfxIdf = 0;
                if (listi.get(i) > 0) {
                    iwordIndex.add(i);
                    tfxIdf = listi.get(i);//rhs gives term freq
                    String tempWord = distinctWords.get(i);//get the word
                    tfxIdf = tfxIdf * wordIdf.get(tempWord);//get idf of word obtained and calc tfxIdf
                    
                    denominatori = denominatori + (tfxIdf * tfxIdf);
                }
                
            }
            
            denominatori = sqrt(denominatori);
            
            while (itBagj.hasNext()) {
                Map.Entry pairj = (Map.Entry) itBagj.next();
                String tempSentencej = (String) pairj.getKey();
                ArrayList<Integer> listj = (ArrayList<Integer>) pairj.getValue();
                
                double denominatorj = 0.00000000000001;//so that denominator doesnt become 0 in any worst case
                for (int i = 0;i < listj.size();i++) {
                    double tfxIdf = 0;
                    if (listj.get(i) > 0) {
                        tfxIdf = listj.get(i);
                        String tempWord = distinctWords.get(i);
                        tfxIdf = tfxIdf * wordIdf.get(tempWord);

                        denominatorj = denominatorj + (tfxIdf * tfxIdf);
                    }
                }
                
                denominatorj = sqrt(denominatorj);
                
                double numeratorij = 0;
                for (int i = 0;i < iwordIndex.size();i++) {
                    if (listj.get(iwordIndex.get(i)) > 0) {//word is present in both sentences
                        double tfxIdf = listi.get(iwordIndex.get(i)) * listj.get(iwordIndex.get(i));
                        String tempWord = distinctWords.get(iwordIndex.get(i));
                        double idf = wordIdf.get(tempWord);
                        tfxIdf = tfxIdf * idf * idf;
                        numeratorij = numeratorij + tfxIdf; 
                    }
                }
                
                double idfModifiedCosine = numeratorij / (denominatorj * denominatori);
                rowCosine.add(idfModifiedCosine);
                avgSimilarity = avgSimilarity + idfModifiedCosine;
                jtemp++;
            }
            cosineSimilarity.add(rowCosine);
            itemp++;
        }
        
//        //ritz code starts
//        writer = new PrintWriter("D:\\cosine.txt");
//        tcount = 1;
//        max = -1;
//        //ritz code ends
        
//        for (int i = 0;i < cosineSimilarity.size();i++) {
//            //System.out.println(cosineSimilarity.get(i));
//            for (int j = 0;j < cosineSimilarity.get(i).size();j++) {
//                //ritz code
//                writer.println(tcount++ + "," + cosineSimilarity.get(i).get(j));
//                //System.out.println(cosineSimilarity.get(i).get(j));
//                if (cosineSimilarity.get(i).get(j) > max) {
//                    max = cosineSimilarity.get(i).get(j);
//                }
//                //r ends
//            }
//        }
//        
//        //ritz code
//        writer.close();
//        System.out.println("max val is " + max);
//        //r ends
        
        //filter cosine similarity matrix using predefined threshold
        avgSimilarity = avgSimilarity / (bagOfWords.size() * bagOfWords.size());//total divided by no.of entries in matrix
       // double similarityThreshold = avgSimilarity;//to be set // at present setting it to average of all values in the matrix
        
        double similarityThreshold = 0.05;
        
        //System.out.println(avgSimilarity);
        for (int i = 0;i < cosineSimilarity.size();i++) {
            int rowSize = cosineSimilarity.get(i).size();
            for (int j = 0;j < rowSize;j++) {
                if (cosineSimilarity.get(i).get(j) < similarityThreshold) {
                    cosineSimilarity.get(i).set(j, 0.0);
                }
            }
        }
        
        /*System.out.println("\n\n\nmatrix after applying similarity threshold\n\n\n");
        
        for (int i = 0;i < bagOfWords.size();i++) {
            System.out.println(cosineSimilarity.get(i));
        }*/
        
        //calculation of degree of each sentence based on similarity matrix
        
        Map<String, Integer> sentenceDegree = new HashMap<String, Integer>();
        itSentence = sentences.entrySet().iterator();
        int indexMatrix = 0;
        while (itSentence.hasNext()) {
            Map.Entry pair = (Map.Entry) itSentence.next();
            String tempSentence = (String) pair.getKey();
            int tempDegree = 0;
            for (int i = 0;i < cosineSimilarity.get(indexMatrix).size();i++) {
                if (cosineSimilarity.get(indexMatrix).get(i) > 0.0) {
                    tempDegree++;
                }
            }
            sentenceDegree.put(tempSentence, tempDegree);
            indexMatrix++;
        }
        
        Map sentenceDegreeSorted = sortByValue(sentenceDegree);
        
        Iterator itSentenceDegreeSorted = sentenceDegreeSorted.entrySet().iterator();
        Map<String, Integer> rankedSentenceDegree = new HashMap<String, Integer>();//assigning ranks to sentences based on sentenceCentroidValSorted map
        rank = 0;
        
        while (itSentenceDegreeSorted.hasNext()) {
            Map.Entry pair = (Map.Entry) itSentenceDegreeSorted.next();
            String tempSentence = (String) pair.getKey();
            rank++;
            rankedSentenceDegree.put(tempSentence, rank);
        }
              
        //degree based method ends here
        
        
        //combining centroid and degree methods
        
        double centroidWeight = 0.5, degreeWeight = 0.5, positionWeight = 50;
        Map<String, Double> rankedSentenceHybrid = new HashMap<String, Double>();
        //System.out.println("size " + rankedSentenceDegree.size());
        Iterator itrankedSentenceDegree = rankedSentenceDegree.entrySet().iterator();
        int breakFlag = 0;
        
        while (itrankedSentenceDegree.hasNext()) {
            Map.Entry pair = (Map.Entry) itrankedSentenceDegree.next();
            String tempSentenceDegree = (String) pair.getKey();
            double degreeRank = (int) pair.getValue();
            //System.out.println("hello " + degreeRank);
            Iterator itrankedSentenceCentroid = rankedSentenceCentroid.entrySet().iterator();
            while (itrankedSentenceCentroid.hasNext()) {
                Map.Entry pair1 = (Map.Entry) itrankedSentenceCentroid.next();
                String tempSentenceCentroid = (String) pair1.getKey();
                double centroidRank = (int) pair1.getValue();
                Iterator itSentencePositionRank = sentencePositionRank.entrySet().iterator();
                while (itSentencePositionRank.hasNext()) {
                    Map.Entry pair2 = (Map.Entry) itSentencePositionRank.next();
                    String tempSentencePosition = (String) pair2.getKey();
                    double positionRank = (double) pair2.getValue();
                    if (tempSentenceCentroid.equals(tempSentenceDegree) && tempSentenceCentroid.equals(tempSentencePosition)) {
                    
                        double hybridRank = (degreeWeight * degreeRank) + (centroidWeight * centroidRank) + (positionWeight * positionRank);
                        //System.out.println("rank" + hybridRank);
                        rankedSentenceHybrid.put(tempSentenceDegree, hybridRank);
                        breakFlag = 1;
                        break;
                    }
                }
                if (breakFlag == 1) {
                    breakFlag = 0;
                    break;
                }
            }
        }
        
        //printMap(rankedSentenceHybrid);
        rankedSentenceHybrid = sortByValueAscending(rankedSentenceHybrid);
		if (rankedSentenceHybrid.size() == 0) {
            rankedSentenceHybrid.put("Sorry, No results found !", 0.0);
        }
        printMap(rankedSentenceHybrid);
        
        /*//debugging
        System.out.println("\nposition\n");
        printMapConsole(sortByValueAscending(sentencePositionRank));
        System.out.println("\ncentroid\n");
        printMapConsole(rankedSentenceCentroid);
        System.out.println("\ndegree\n");
        printMapConsole(rankedSentenceDegree);
        
        //debugging ends*/
        
        
        //Deleting files in folder
        //for (File doc : listOfDocs)doc.delete();
        
        
    }
    
    public static void printMap(Map map) throws FileNotFoundException, UnsupportedEncodingException {
        Iterator it = map.entrySet().iterator();
        int temp = 0;
        PrintWriter writer = new PrintWriter("C:\\wamp\\www\\mdsnew\\output.txt", "UTF-8");
        String text = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            //System.out.println(++temp + " " + pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
            
            //Writing to file  Gaurav
            text = text + pair.getKey() + "\n";
            temp++;
            if(temp > 6)break;
        }
        
        writer.println(text);
        writer.close();
    }
    
    

    public static void printMapConsole(Map map) throws FileNotFoundException, UnsupportedEncodingException {
        Iterator it = map.entrySet().iterator();
        int temp = 0;
        //PrintWriter writer = new PrintWriter("C:\\wamp\\www\\mdsnew\\output.txt", "ASCII");
        String text = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            System.out.println(++temp + " " + pair.getKey() + " = " + pair.getValue());
            System.out.println(sentences.get(pair.getKey()));
            //it.remove(); // avoids a ConcurrentModificationException
            
            //Writing to file  Gaurav
            //text = text + " " + pair.getKey() + "\n";
            
            if(temp > 4)break;
        }
    }
    public static Map sortByValue(Map unsortMap) {	 
	List list = new LinkedList(unsortMap.entrySet());
 
	Collections.sort(list, new Comparator() {
		public int compare(Object o1, Object o2) {
			return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
		}
	});
 
	Map sortedMap = new LinkedHashMap();
	for (Iterator it = list.iterator(); it.hasNext();) {
		Map.Entry entry = (Map.Entry) it.next();
		sortedMap.put(entry.getKey(), entry.getValue());
	}
	return sortedMap;
    }
    
    public static Map sortByValueAscending(Map unsortMap) {	 
	List list = new LinkedList(unsortMap.entrySet());
 
	Collections.sort(list, new Comparator() {
		public int compare(Object o1, Object o2) {
			return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
		}
	});
 
	Map sortedMap = new LinkedHashMap();
	for (Iterator it = list.iterator(); it.hasNext();) {
		Map.Entry entry = (Map.Entry) it.next();
		sortedMap.put(entry.getKey(), entry.getValue());
	}
	return sortedMap;
    }
    
    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    
}


class URLReader {
    void getDocs() throws MalformedURLException, BoilerpipeProcessingException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
           System.getProperties().put("http.proxyHost", "172.31.1.4");
           System.getProperties().put("http.proxyPort", "8080");
           int fileCount = 0;
           
           InputStream in = new FileInputStream("C:\\wamp\\www\\mdsnew\\urlFile.txt");
           Scanner sc = new Scanner(in);
           
           while (sc.hasNextLine() && fileCount < 5) {
               String text = "";
               String data = sc.nextLine();
               fetchContent ob = new fetchContent(data);
               ob.returnContent();
               fileCount++;
           }
           
           //return toReturn;
    }
}

class fetchContent implements Runnable {
    Thread fetcher;
    String urlString;
    String text = "nothing fetched";
    URL url;
    fetchContent(String urlParam) throws MalformedURLException {
        urlString = urlParam;
        url = new URL(urlString);
        //System.out.println("finished constructor");
    }
    
    public void returnContent() throws InterruptedException {
        fetcher = new Thread(this, "IamAthread");
        fetcher.start();
        fetcher.join();
    }
    
    @Override
    public void run() {
        //System.out.println("thread started working");
        try {
            text = ArticleExtractor.INSTANCE.getText(url);
            System.out.println("good " + url);
        } catch(Exception e) {
            System.out.println("error " + url);
        }
        //System.out.println("Thread finished working");
        multiDocumentSummarization.content.add(text);
        multiDocumentSummarization.noOfFilesFetched++;
    }
    
    
}

