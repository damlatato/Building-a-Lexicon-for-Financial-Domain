import java.util.Map;
import java.util.Map.Entry;


public class DocToVector
{
	
	TermDictionary dictionary;
	DocToVector(TermDictionary dictionary){
		this.dictionary = dictionary;
	}
	
	
// Create document vectors
	 public double [] vectorTerm(String t) {
		
//		path to the object files(where we stored our npmi values and keys) 
		String path = "C:\\Users\\Tato\\Desktop\\Team Project\\nmip\\" + t.replaceAll("[^A-Za-z0-9 ]", "") + ".obj" ;
		
		double [] result = new double[dictionary.GetLenght()];
//		read values from matrix
		Map<String,Double> pmi = Sorting.readObject(path);
		for(Entry<String, Double> e: pmi.entrySet()){
			String currentTerm = e.getKey();
			int currentId = dictionary.getIdOfTerm(currentTerm);
			result[currentId] = e.getValue();
		}			
		return result;
	}
}
