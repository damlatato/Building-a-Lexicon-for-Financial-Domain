import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class PopulateDictionary
{	public static boolean DESC = false;
static TermDictionary termDictionary = new TermDictionary();


/**
 * @param args
 * @throws Exception 
 */
public static void main(String[] args) throws Exception
{

	PopulateDictionary populate = new PopulateDictionary();
	PopulateDictionary.populateDictionary();
	double[][]npmiValues  = PopulateDictionary.populateNPMIvalues();
	System.out.println(PopulateDictionary.termDictionary.getIndexByName());
	BufferedReader initialTerm = new BufferedReader(new FileReader("C:\\Users\\Tato\\Desktop\\Team Project\\Termlist\\termlist0227.txt"));
	List<String> terms = new ArrayList<String>();
	String initialTerm1;

	//start reading initial term document line by line
	while ((initialTerm1 = initialTerm.readLine()) != null) {

		terms.add(initialTerm1)	; 
	}
	initialTerm.close();

	//ioana
	double[][] cosineALL = new double[PopulateDictionary.termDictionary.GetLenght()][PopulateDictionary.termDictionary.GetLenght()];
	for (Entry<Integer, String> e1: termDictionary.getIndexByID().entrySet()){
		double[] vector1 = npmiValues[e1.getKey()];
		Map<String, Double> unsortMap = new HashMap<>();

		for (Entry<Integer, String> e2: termDictionary.getIndexByID().entrySet()){

			double[] vector2 = npmiValues[e2.getKey()];
			double cosineScore = CosineSimilarity.computeCosine(vector1, vector2);
			//					fill matrix with cosine scores
			cosineALL[e1.getKey()][e2.getKey()] = cosineScore;
			cosineALL[e2.getKey()][e1.getKey()] = cosineScore;
			unsortMap.put(e2.getValue(), cosineScore);

		}
		Sorting sort = new Sorting();
		Map<String, Double> sortedMapDesc = Sorting.sortByComparator(unsortMap, DESC);
		String filename = 	populate.fromTermToObjectFilename(e1.getValue().replaceAll("[^A-Za-z0-9 ]", ""));
		Sorting.printMap(sortedMapDesc, filename) ; 
	}

	System.out.println(" \n");

}


//	Create dictionary
public static void populateDictionary() throws Exception{

	BufferedReader initialTerm = new BufferedReader(new FileReader("C:\\Users\\Tato\\Desktop\\Team Project\\Termlist\\termlist0227.txt"));

	List<String> terms = new ArrayList<String>();


	String initialTerm1;
	//start reading initial term document line by line
	while ((initialTerm1 = initialTerm.readLine()) != null) {

		terms.add(initialTerm1)	; 
	}
	initialTerm.close();

	for (int i = 0; i< terms.size(); i++) {
		String path = "C:\\Users\\Tato\\Desktop\\Team Project\\nmip\\" + terms.get(i).replaceAll("[^A-Za-z0-9 ]", "") + ".obj" ;
		String keyTerm = terms.get(i);
		termDictionary.addTerm(keyTerm);

	}		
}


//fill matrix with npmi values
public static double[][]  populateNPMIvalues() throws Exception{

	BufferedReader initialTerm = new BufferedReader(new FileReader("C:\\Users\\Tato\\Desktop\\Team Project\\Termlist\\termlist0227.txt"));

	List<String> terms = new ArrayList<String>();


	String initialTerm1;
	//start reading initial term document line by line
	while ((initialTerm1 = initialTerm.readLine()) != null) {

		terms.add(initialTerm1)	; 
	}
	initialTerm.close();
	double[][]npmivalues = new double[terms.size()][terms.size()] ;
	for (int i = 0; i< terms.size(); i++) {
		String term1 = terms.get(i);
		int term1Id = termDictionary.getIdOfTerm(term1);
		String path = "C:\\Users\\Tato\\Desktop\\Team Project\\nmip\\" + terms.get(i).replaceAll("[^A-Za-z0-9 ]", "") + ".obj" ;
		Map<String,Double> pmi = Sorting.readObject(path);
		for(Entry<String, Double> e: pmi.entrySet()){
			String term2 = e.getKey(); 
			int term2Id = termDictionary.getIdOfTerm(term2);
			npmivalues[term1Id][term2Id] = e.getValue();
			npmivalues[term2Id][term1Id] = e.getValue();
		}

	}
	return npmivalues;

}




private String fromTermToObjectFilename(String term) {
	return "C:\\Users\\Tato\\Desktop\\Team Project\\CosineSimilarity\\" + term + ".obj" ;
}
}
