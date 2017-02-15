import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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


public class QueryExpansion2
{
	public static boolean DESC = false;
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException
	{
		File indexDir = new File("C:/index/");
		int hits = 10;
		Directory directory = FSDirectory.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(directory);
		
		
		QueryExpansion expansion = new QueryExpansion();
		BufferedReader initialTerm = new BufferedReader(new FileReader("C:\\Users\\Tato\\Desktop\\Team Project\\Termlist\\term.txt"));
		List<String> terms = new ArrayList<String>();
		String initialTerm1;
		//start reading initial term document line by line
		while ((initialTerm1 = initialTerm.readLine()) != null) {

			terms.add(initialTerm1)	; 
		}
		initialTerm.close();
		
		for (int i = 0; i < terms.size(); i++) {
		
			 Map<String, Double> arr = new HashMap<String, Double>();
			System.out.println("solving term : " + terms.get(i));		

			Sorting sort = new Sorting();
		
			String filename = fromTermToObjectFilename(terms.get(i).replaceAll("[^A-Za-z0-9 ]", ""));
			
			arr= readObject(filename); 
			
			
			 searchBoolean(searcher,terms.get(i), hits);
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

			System.out.println(" \n");
//			int numResultsBoth = mainObject.searchBoolean(searcher,  terms.get(i), hits);
			
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
