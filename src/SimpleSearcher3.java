import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class SimpleSearcher3
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		//String line = "";
		File indexDir = new File("C:/index/");
		int hits1 = 5000;
		Directory directory = FSDirectory.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(directory);
		IndexReader reader = IndexReader.open(directory);

		File folder = new File("C:\\Users\\Tato\\Desktop\\Team Project\\nmip\\");
		
		FileReader fr = null;
		BufferedReader br = null;
		
		for (File fileEntry : folder.listFiles()) {
			try{
				readObject(fileEntry.toString());
			printTopKRelatedTerms(fileEntry.toString(),5);
			return;
			}
			catch(Exception ex){
				ex.printStackTrace();

			} 
	
			
            }
		
		

		}

	
	public static void printTopKRelatedTerms(String filename ,int k) {
		Map<String, Double> all = readObject(filename);
		int limit = k;
		if (all.size() < k) 
			limit = all.size();
		for(int i = 0 ; i <limit; i++){
			System.out.println(all.keySet().toArray()[i]);
		}
	}
	
	private static Map<String, Double> readObject(String filename){ 
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

	
	


