import java.util.HashMap;
import java.util.Map;


public class TermDictionary
{

	private  Map<String, Integer> IndexByName = new HashMap<String, Integer>();
	private Map<Integer, String> IndexByID = new HashMap<Integer, String>();


	
	public Map<String, Integer> getIndexByName()
	{
		return IndexByName;
	}
	
	public Map<Integer, String> getIndexByID()
	{
		return IndexByID;
	}
	
	
	public void addTerm(String term){
		
		
		if (!IndexByName.containsKey(term)){
			
			int l = GetLenght();
			IndexByName.put(term,l);
			IndexByID.put(l,term);
			
			
		}
	}
	

 int GetLenght() { 
		
		return IndexByName.size();}
	
	public String GetTermByID(int id) {
		
		return IndexByID.get(id);
	}
	
	public int getIdOfTerm(String T){
		return IndexByName.get(T);
	}
	

}
