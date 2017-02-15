import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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


public class Test_QueryExpansion
{public static boolean DESC = false;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException
	{
		// Read input from user
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));		
			    String term  = "";
			    
			    while (!term.equalsIgnoreCase("q")) {
			      try {
			        System.out.println("Enter the search query (q=quit):");
			        term = br.readLine();
			        if (term.equalsIgnoreCase("q")) 
			        {
			          break;
			        }
			        // Check if the term entered is spelled correctly
			       
			        
			    
	
		File indexDir = new File("C:/index/");
		int hits = 10;
		Directory directory = FSDirectory.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(directory);
		
		
		QueryExpansion expansion = new QueryExpansion();
	
			 Map<String, Double> arr = new HashMap<String, Double>();
			System.out.println("solving term : " + term);		

			Sorting sort = new Sorting();
		
			String filename = fromTermToObjectFilename(term.replaceAll("[^A-Za-z0-9 ]", ""));
			
			arr= readObject(filename); 
			
			 searchBoolean(searcher,term, hits);
				int limit = 3;
				if (arr.size() < limit) 
					limit = arr.size();
				Map<String,Double> extraTerms = new HashMap<>();
				for(int h = 0 ; h <limit; h++){
//					System.out.println(arr.keySet().toArray()[h]);
					extraTerms.put(arr.keySet().toArray()[h].toString(),(Double) arr.values().toArray()[h]);
				}
				
				for (Entry<String, Double> entry : extraTerms.entrySet()) {
				    String key = entry.getKey();
				   Double value = entry.getValue();
				   searchBoolean_Calc(searcher, key, value, hits);
				   System.out.println("---------------");
				}
			System.out.println(" \n");
			      }
			      catch (Exception e) {
			          System.out.println("Error searching " + term + " : " + e.getMessage());
			          e.printStackTrace();
			      }
			   }
	}
	private static String fromTermToObjectFilename(String term) {
		return "C:\\Users\\Tato\\Desktop\\Team Project\\nmip\\" + term + ".obj" ;
	}
	private static  void searchBoolean(IndexSearcher iSearcher, String firstTerm, int maxHits) throws ParseException, IOException 
	{
		 String name="";
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
		BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
		BooleanQuery booleanQuery = new BooleanQuery();
		Query queryFirst = parser.parse(QueryParser.escape(firstTerm));
	
		
		BooleanClause clauseFirst = new BooleanClause(queryFirst, BooleanClause.Occur.MUST);	
		booleanQuery.add(clauseFirst);
		
		
		TopDocs topDocs = iSearcher.search(booleanQuery, maxHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		//String line1 = queryStr.replaceAll("\\s","");
		for (int i = 0; i < hits.length; i++) {
			
			int docId = hits[i].doc;
			Document d = iSearcher.doc(docId);
			name = d.get("filename");
		System.out.println(name);
		}   
		return;
		
	}
	
	private static  void searchBoolean_Calc(IndexSearcher iSearcher, String firstTerm, double similarity, int maxHits) throws ParseException, IOException 
	{
		 String name="";
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
		BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
		BooleanQuery booleanQuery = new BooleanQuery();
		Query queryFirst = parser.parse(QueryParser.escape(firstTerm));
	
		
		BooleanClause clauseFirst = new BooleanClause(queryFirst, BooleanClause.Occur.MUST);
	
		
		booleanQuery.add(clauseFirst);
		
		Map<String, Double> results = new HashMap<String, Double>();
		Map<String, Double> unsortMap = new HashMap<>();
		TopDocs topDocs = iSearcher.search(booleanQuery, maxHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		//String line1 = queryStr.replaceAll("\\s","");
		for (int i = 0; i < hits.length; i++) {
//			System.out.print("Score: "+ hits[i].score * similarity + " ");
			
			int docId = hits[i].doc;
			Document d = iSearcher.doc(docId);
			name = d.get("filename");
//		System.out.println(name);
		results.put(name, hits[i].score * similarity);
		} 
		for (Entry<String, Double> e: results.entrySet()){
			unsortMap.put(e.getKey(), e.getValue());
			
		}
		Sorting sort = new Sorting();
		Map<String, Double> sortedMapDesc = Sorting.sortByComparator(unsortMap, DESC);
		for (Entry<String, Double> sortKey: sortedMapDesc.entrySet()){
			System.out.println(sortKey.getKey());
			
		}
		return;
		
	}
	public void printTopKRelatedTerms(String filename ,int k) {
		Map<String, Double> all = this.readObject(filename);
		int limit = k;
		if (all.size() < k) 
			limit = all.size();
		for(int i = 0 ; i <limit; i++){
			System.out.println(all.keySet().toArray()[i]);
		}
	}
	static Map<String, Double> readObject(String filename){ 
		// object ýnput stream wýth ýnput the fýlename and result should be a Map
		try{
			File f = new File(filename);
			if (!f.exists()) System.out.println("searched for nonexisent file: " + filename);
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
