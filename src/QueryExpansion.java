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


public class QueryExpansion
{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException
	{
		
		File indexDir = new File("C:/index/");
		int hits = 50;
		Directory directory = FSDirectory.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(directory);

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
	
			String filename = fromTermToObjectFilename(terms.get(i).replaceAll("[^A-Za-z0-9 ]", ""));
			
			arr= readObject(filename); 
			
//				System.out.println(arr.keySet().toArray()[h]);
			
				 searchBoolean(searcher,terms.get(i),arr.keySet().toArray()[1].toString() ,arr.keySet().toArray()[2].toString(), arr.keySet().toArray()[3].toString(), hits);	
			System.out.println(" \n");
//			int numResultsBoth = mainObject.searchBoolean(searcher,  terms.get(i), hits);
			
		}
	}
	private static String fromTermToObjectFilename(String term) {
		return "C:\\Users\\Tato\\Desktop\\Team Project\\nmip\\" + term + ".obj" ;
	}
	private static  void searchBoolean(IndexSearcher iSearcher, String firstTerm, String secondTerm, String thirdTerm, String fourthTerm, int maxHits) throws ParseException, IOException 
	{
		 String name="";
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
		BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
		BooleanQuery booleanQuery = new BooleanQuery();
		Query queryFirst = parser.parse(QueryParser.escape(firstTerm));
		//		Query queryFirst = parser.parse(firstTerm);  
		Query querySecond = parser.parse(QueryParser.escape(secondTerm));		
		//		Query querySecond = parser.parse(secondTerm);
		Query queryThird = parser.parse(QueryParser.escape(thirdTerm));
		Query queryFourth = parser.parse(QueryParser.escape(fourthTerm));
		
		BooleanClause clauseFirst = new BooleanClause(queryFirst, BooleanClause.Occur.SHOULD);
		BooleanClause clauseSecond = new BooleanClause(querySecond, BooleanClause.Occur.SHOULD);
		BooleanClause clauseThird = new BooleanClause(queryThird, BooleanClause.Occur.SHOULD);
		BooleanClause clauseFourth = new BooleanClause(queryFourth, BooleanClause.Occur.SHOULD);
		
		booleanQuery.add(clauseFirst);
		booleanQuery.add(clauseSecond);
		booleanQuery.add(clauseThird);
		booleanQuery.add(clauseFourth);
		
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
