import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class SimpleSearcher2
{

	//This variable will hold all terms of each document in an array.
	private static List<String[]> termsDocsArray_queryDoc;
	private static List<String[]> termsDocsArray_relatedTermDoc;

	private static List<String> allTerms; //to hold all terms
	private static List<String> allTerms1; //to hold all terms
	private static List<double[]> tfidfDocsVector_relatedTermDoc;
	private static List<double[]> tfidfDocsVector_queryDoc;
	public static boolean DESC = false;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		//		sets for query document and related term document to calculate jaccard 
		Set<String> setA = new HashSet<String>();

		Set<String> setB = new HashSet<String>();



		//String line = "";
		File indexDir = new File("C:/index/");
		int hits = 5000;


		Directory directory = FSDirectory.open(indexDir);

		IndexSearcher searcher = new IndexSearcher(directory);
		//IndexReader reader = IndexReader.open(directory);



		//reading first file
		BufferedReader initialTerm = new BufferedReader(new FileReader("C:\\Users\\Tato\\Desktop\\Team Project\\Termlist\\term.txt"));
		//reading second file
		//	BufferedReader secondTerm = new BufferedReader(new FileReader("C:\\Users\\Tato\\Desktop\\Team Project\\Termlist\\termlist02271.txt"));


		List<String> terms = new ArrayList<String>();
		//List<String> term2 = new ArrayList<String>();
		SimpleSearcher2 mainObject = new SimpleSearcher2();				 
		//total number of documents

		//	secondTerm.readLine(); //this will read the first line of second term document(we want second term document starts from second line)

		String initialTerm1;
		//	String initialTerm2 ;

		//start reading initial term document line by line
		while ((initialTerm1 = initialTerm.readLine()) != null) {


			terms.add(initialTerm1)	; 
			//		term2.add(initialTerm1);

		}
		initialTerm.close();


		/*	//start reading second term document from second
		while((initialTerm2 = secondTerm.readLine()) != null) // will start from second line
		{
			term2.add(initialTerm2);

		}
		secondTerm.close();*/
		String newname;	
		System.out.println("read : " + terms.size() + " terms ");

		for (int i = 0; i < terms.size(); i++) {

			//for cosine sim. 
			termsDocsArray_queryDoc = new ArrayList<String[]>();
			termsDocsArray_relatedTermDoc = new ArrayList<String[]>();

			allTerms = new ArrayList<String>(); //to hold all terms
			allTerms1 = new ArrayList<String>(); //to hold all terms
			tfidfDocsVector_relatedTermDoc = new ArrayList<double[]>();
			tfidfDocsVector_queryDoc = new ArrayList<double[]>();
			System.out.println("solving term : " + terms.get(i));
			//creating map 
			Map<String, Double> unsortMap = new HashMap<String, Double>();

			int numResultsInitial = mainObject.searchIndex(searcher, terms.get(i), hits);
			System.out.println("i = " + i);
			//	System.out.println("solving term : " + terms.get(i));

			newname = terms.get(i).replaceAll("%|/|:|\"|\\.", " ").replaceAll("\\s+", "");

			//mainObject.searchIndex_Cosine(searcher, newname, hits);
//			long startTime = System.currentTimeMillis();


			//get def and tokenize
			String[] tokenizedTerms_for_word = SimpleSearcher2.searchIndex_Cosine(searcher, newname, hits).toString().replaceAll("[\\W&&[^\\s]]", "").split("\\W+");   //to get individual terms
			for (String term : tokenizedTerms_for_word) {
				if (!allTerms1.contains(term)) {  //avoid duplicate entry
					allTerms1.add(term);
					//					add terms in related term document in set A to calculate Jaccard
					setA.add(term);
				}
				//				System.out.println(term);
			}

			termsDocsArray_queryDoc.add(tokenizedTerms_for_word);

			for(int j = 0; j< terms.size() ; j++ )
			{	
				//	System.out.println("j = " +j);
				//search second term
				int numResultsOther = mainObject.searchIndex(searcher,  terms.get(j), hits);
				//search initial and second term in boolean query
				int numResultsBoth = mainObject.searchBoolean(searcher,  terms.get(i), terms.get(j), hits);

				//				System.out.println( term1.get(i) + ": " +numResultsInitial +" \n" +  term2.get(j)  + ": " + numResultsOther + "\n " +  term1.get(i)+ " and " + term2.get(j)  + ": " +  numResultsBoth);
				//calculate their probabilities

				int totalDocs =  searcher.getIndexReader().numDocs();
				double prob_numResultsInitial = (double)(numResultsInitial +1) / totalDocs;
				double prob_numResultsOther = (double)(numResultsOther + 1) / totalDocs;
				double prob_numResultsBoth = (double)(numResultsBoth+1) / totalDocs;
				//calculate mutual information 
				double mutual_information = prob_numResultsBoth / (prob_numResultsInitial* prob_numResultsOther);
				//System.out.println("NORMALIZED MUTUAL INFORMATION:  \n");
				double pmi = (Math.log(mutual_information));
				//calcualte normalized mutual information
				double npmi =  (pmi / -(Math.log(prob_numResultsBoth)));
				//System.out.println(npmi);

				//				System.out.println("\n");

				//if npmi is greater then 0 store it in hash map
				if(npmi > 0 && npmi != 1) {				    	
					//	put them into Map set
					unsortMap.put(terms.get(j), npmi);

				}


			}


			//sorting values according to descending order

			Map<String, Double> sortedMapDesc = sortByComparator(unsortMap, DESC);
			String filename = 	mainObject.fromTermToObjectFilename(terms.get(i).replaceAll("%|/|:|\"|\\.", " ").replaceAll("\\s+", ""));
			printMap(sortedMapDesc, filename) ; 

			System.out.println(terms.get(i).replaceAll("%|/|:|\"|\\.", " ").replaceAll("\\s+", "") + " Key Values: \n");
			//	SimpleSearcher searcher = new SimpleSearcher();
			int k = 10;
			
			
//			carry  key values from hash map print function to here . object to array
			List<String> arr = new ArrayList<String>();

		
			arr= mainObject.printTopKRelatedTerms(filename, k );
		
			
//			call each key value to calculate cosine with actual term
			for (int t = 0; t < arr.size(); t++) {
				
				System.out.println(searchIndex_Cosine(searcher, arr.get(t), hits));

				System.out.println(arr.get(t));
				
				String[] tokenizedTerms =  searchIndex_Cosine(searcher, arr.get(t), hits).toString().replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
				//to get term definitions 
		
//				System.out.println(tokenizedTerms.toString());
				for (String term : tokenizedTerms) {
					if (!allTerms.contains(term)) {  //avoid duplicate entry
						allTerms.add(term);
						//						add terms in related term document in set B to calculate Jaccard
						
						setB.add(term);
						
					}
					//										System.out.println(term);
				}

				termsDocsArray_relatedTermDoc.add(tokenizedTerms);
			
			}			

	

			//				calculate tfidf		
			calcualte_tfidf(termsDocsArray_relatedTermDoc,termsDocsArray_queryDoc);

			//			calculate cosine and return 
			CosineSimilarity.return_cosine(terms.get(i), arr, tfidfDocsVector_queryDoc, tfidfDocsVector_relatedTermDoc) ;
			
			
			
//			long endTime = System.currentTimeMillis();
//			long tookTime = endTime - startTime ;
//			tookTime = tookTime / 1000;
//			System.out.println("computing for " +i + " took " + tookTime + " seconds");

			System.out.println(" \n");

		}


	}
	

//	to calculate tdidf 
	public static void calcualte_tfidf (List<String[]> termsDocsArray_relatedTermDoc, List<String[]> termsDocsArray_queryDoc)  
	{
		double tf_relatedTermDoc; //term frequency
		double idf_relatedTermDoc; //inverse document frequency
		double tfidf_relatedTermDoc; //term requency inverse document frequency

		double tf_queryDoc; //term frequency for query doc
		double idf_queryDoc; //inverse document frequency
		double tfidf_queryDoc; //term requency inverse document frequency


		//	--------------------------calculate tf-idf for each related term document ---------------------------------------------------------
		for (String[] docTermsArray : termsDocsArray_relatedTermDoc) {
			double[] tfidfvectors = new double[allTerms.size()];
			int count = 0;
			for (String terms : allTerms) {
				tf_relatedTermDoc = new TfIdf().tfCalculator(docTermsArray, terms);
				idf_relatedTermDoc = new TfIdf().idfCalculator(termsDocsArray_relatedTermDoc, terms);
				tfidf_relatedTermDoc = tf_relatedTermDoc* idf_relatedTermDoc;


				tfidfvectors[count] = tfidf_relatedTermDoc;

				count++;

			} //end of second for
			tfidfDocsVector_relatedTermDoc.add(tfidfvectors);  //storing document vectors;    

		} //end of second for 

		//	----------------------calculate tf-idf for query document---------------------------------------------------------------
		for (String[] docTermsArray1 : termsDocsArray_queryDoc) {
			double[] tfidfvectors1 = new double[allTerms1.size()];
			int count1 = 0;
			for (String terms1 : allTerms1) {
				tf_queryDoc = new TfIdf().tfCalculator(docTermsArray1, terms1);

				idf_queryDoc = new TfIdf().idfCalculator(termsDocsArray_queryDoc, terms1);

				tfidf_queryDoc = tf_queryDoc * idf_queryDoc;




				tfidfvectors1[count1] = tfidf_queryDoc;
				//					System.out.println("tfidf for related term doc for "  + terms1 + tfidfvectors1[count1]);
				count1++;

			}// end of second for 
			tfidfDocsVector_queryDoc.add(tfidfvectors1);  //storing document vectors;    

		} //end of first for

	}


// find definition of query by matching query term with filename and return the definition of it 
	private static String searchIndex_Cosine(IndexSearcher iSearcher,  String queryStr, int maxHits) 
			throws Exception {
		String content =
		"";

		QueryParser parser = new QueryParser(Version.LUCENE_30, "filename", new StandardAnalyzer(Version.LUCENE_30));

		Query query = parser.parse(QueryParser.escape(queryStr));
		TopDocs topDocs = iSearcher.search(query, maxHits);

		int nrHits = topDocs.totalHits;

		int totalNrDocs = iSearcher.getIndexReader().numDocs() ;

		ScoreDoc[] hits = topDocs.scoreDocs;



		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = iSearcher.doc(docId);
		
			content = d.get("contents");

			//			System.out.println(content);

		}
		return content;
	}


	private String fromTermToObjectFilename(String term) {
		return "C:\\Users\\Tato\\Desktop\\Team Project\\npm2\\" + term.replaceAll("%|/|:|\"|\\.", " ").replaceAll("\\s+", "") + ".obj" ;
	}


	private int searchIndex(IndexSearcher iSearcher,  String queryStr, int maxHits) 
			throws Exception {



		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
		//		Query query = parser.createPhraseQuery("TEXT", QueryParser.escape(queryString));	

		Query query = parser.parse(QueryParser.escape(queryStr));


		TopDocs topDocs = iSearcher.search(query, maxHits);

		int nrHits = topDocs.totalHits;
		//System.out.println(nrHits);
		int totalNrDocs = iSearcher.getIndexReader().numDocs() ;



		ScoreDoc[] hits = topDocs.scoreDocs;
		//String line1 = queryStr.replaceAll("\\s","");


		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = iSearcher.doc(docId);
			String name = d.get("filename");


			//System.out.println(d.get("filename"));
			//}
		}   
		// System.out.println("Found " + hits.length);
		//		System.out.println("\n");

		return nrHits;
	}


	private int searchBoolean(IndexSearcher iSearcher, String firstTerm, String secondTerm, int maxHits) throws IOException, ParseException
	{


		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
		BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );

		BooleanQuery booleanQuery = new BooleanQuery();

		Query queryFirst = parser.parse(QueryParser.escape(firstTerm));
		//		Query queryFirst = parser.parse(firstTerm);  
		Query querySecond = parser.parse(QueryParser.escape(secondTerm));
		//		Query querySecond = parser.parse(secondTerm);
		BooleanClause clauseFirst = new BooleanClause(queryFirst, BooleanClause.Occur.MUST);
		BooleanClause clauseSecond = new BooleanClause(querySecond, BooleanClause.Occur.MUST);
		booleanQuery.add(clauseFirst);
		booleanQuery.add(clauseSecond);

		TopDocs topDocs = iSearcher.search(booleanQuery, maxHits);
		return topDocs.totalHits;
	}


	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order) 
	{

		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Double>>()
				{
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2)
			{
				if (order)
				{
					return o1.getValue().compareTo(o2.getValue());
				}
				else
				{
					return o2.getValue().compareTo(o1.getValue());

				}
			}
				});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static void printMap(Map<String, Double> map, String filename)
	{
		try
		{
			//	             //write values to file    
			FileOutputStream fos =
					new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);


			oos.writeObject(map);
			//System.out.printf("Serialized HashMap data is saved in hashmap.txt \n");

			//			for (Entry<String, Double> entry : map.entrySet())
			//			{
			//				oos.writeObject(entry.getKey() + " Value : "+ entry.getValue()+ "\n");
			//				System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
			//
			//			}
			oos.close();
			fos.close();

		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}

	}

	public ArrayList<String> printTopKRelatedTerms(String filename ,int k) {
		ArrayList<String> toprelated = new ArrayList<String>();
		Map<String, Double> all = this.readObject( filename);
		int limit = k;

		if (all.size() < k) 
			limit = all.size();
		for(int i = 0 ; i <limit; i++){
			//System.out.println(all.keySet().toArray()[i].toString().replaceAll("%|/|:|\"|\\.", " ").replaceAll("\\s+", ""));

			toprelated.add(all.keySet().toArray()[i].toString().replaceAll("%|/|:|\"|\\.", " ").replaceAll("\\s+", ""));



		}
		return toprelated;
	}



	private Map<String, Double> readObject(String filename){ 
		// object ýnput stream wýth ýnput the fýlename and result should be a Map
		try{
			FileInputStream fin = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fin);
			//			  Object object=ois.readObject();

			Map<String, Double> map = (HashMap<String, Double>) ois.readObject();
			return map;
			//			return (Map<String, Double>) ois.readObject();


		}catch(Exception ex){
			ex.printStackTrace();

		} 
		return new HashMap<>();


	}
}
