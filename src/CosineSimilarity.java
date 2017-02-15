


public class CosineSimilarity
{
	
	static double computeCosine(double[] term1Vector,
			double[] term2Vector)
	{
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;
		
		for (int i = 0; i < term1Vector.length; i++) //docVector1 and docVector2 must be of same length
		{
			dotProduct += term1Vector[i] * term2Vector[i];  //a.b
			magnitude1 += Math.pow(term1Vector[i], 2);  //(a^2)
			magnitude2 += Math.pow(term2Vector[i], 2); //(b^2)
		}

		magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
		magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

		if (magnitude1 != 0.0 | magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);

		}
		else {

			return 0.0;
		}

		System.out.println(cosineSimilarity);

		return cosineSimilarity;
	}



}
