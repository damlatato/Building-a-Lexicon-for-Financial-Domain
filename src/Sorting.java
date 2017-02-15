import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Sorting
{
	static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order) 
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
			oos.close();
			fos.close();

		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}

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
