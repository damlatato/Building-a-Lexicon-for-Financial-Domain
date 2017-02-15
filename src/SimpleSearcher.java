import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class SimpleSearcher
{
	public static boolean DESC = false;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{		
		File indexDir = new File("C:/index/");
		int hits = 5000;
		Directory directory = FSDirectory.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(directory);
		//reading first file
		BufferedReader initialTerm = new BufferedReader(new FileReader("C:\\Users\\Tato\\Desktop\\Team Project\\Termlist\\term.txt"));
		List<String> terms = new ArrayList<String>();
		SimpleSearcher mainObject = new SimpleSearcher();				 
		String initialTerm1;

		//start reading initial term document line by line
		while ((initialTerm1 = initialTerm.readLine()) != null) {

			terms.add(initialTerm1)	; 
		}
		initialTerm.close();

		System.out.println("read : " + terms.size() + " terms ");

		for (int i = 0; i < terms.size(); i++) {
			System.out.println("solving term : " + terms.get(i));
			//creating unsorted map 
			Map<String, Double> unsortMap = new HashMap<String, Double>();

			int numResultsInitial = mainObject.searchIndex(searcher, terms.get(i), hits);
			System.out.println("i = " + i);
			//			long startTime = System.currentTimeMillis();

			for(int j = 0; j< terms.size() ; j++ )
			{	
				//search second term
				int numResultsOther = mainObject.searchIndex(searcher,  terms.get(j), hits);
				//search initial and second term in boolean query
				int numResultsBoth = mainObject.searchBoolean(searcher,  terms.get(i), terms.get(j), hits);
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

				//if npmi is greater then 0 store it in hash map
				if(npmi > 0 && npmi != 1) {				    	
					//	put them into Map set
					unsortMap.put(terms.get(j), npmi);
				}

			}
			Sorting sort = new Sorting();
			//sorting values according to descending order
			Map<String, Double> sortedMapDesc = Sorting.sortByComparator(unsortMap, DESC);
			String filename = 	mainObject.fromTermToObjectFilename(terms.get(i).replaceAll("[^A-Za-z0-9 ]", ""));
			Sorting.printMap(sortedMapDesc, filename) ; 

			System.out.println(terms.get(i) + " Key Values: \n");
			int k = 10;
			sort.printTopKRelatedTerms(filename, k );
			//
			//			long endTime = System.currentTimeMillis();
			//			long tookTime = endTime - startTime ;
			//			tookTime = tookTime / 1000;
			//			System.out.println("computing for " +i + " took " + tookTime + " seconds");

			System.out.println(" \n");
		}
	}	
	private String fromTermToObjectFilename(String term) {
		return "C:\\Users\\Tato\\Desktop\\Team Project\\nmip\\" + term + ".obj" ;
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
			String name = d.get("contents");
		}   
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

}
