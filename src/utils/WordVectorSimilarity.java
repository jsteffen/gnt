package utils;

import java.io.IOException;

import features.DistributedWordVector;
import features.DistributedWordVectorFactory;

/**
 * Given two words, compute their similarity based on the distributed word vectors.
 * Steps:
 * - assume distributed vectors are loaded
 * - for two words
 * - determine word vector
 * - compute similarity
 * - return value
 * @author gune00
 *
 */
public class WordVectorSimilarity {
	private static DistributedWordVectorFactory dwvFactory = new DistributedWordVectorFactory();
	
	

	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}   
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
	public static DistributedWordVector getWordVector(String word){
		if (dwvFactory.getWord2num().containsKey(word))
			return dwvFactory.getDistributedWordsTable().get(dwvFactory.getWord2num().get(word));
		else
			{
			DistributedWordVector unknownWordVector = dwvFactory.handleUnknownWordWithoutContext(word);
			return unknownWordVector;
			}
	}
	
	public static void testSimilarity(String word1, String word2){
		DistributedWordVector vec1 = WordVectorSimilarity.getWordVector(word1);
		DistributedWordVector vec2 = WordVectorSimilarity.getWordVector(word2);
		double[] vectorA = vec1.concatenateleftAndRightVector();
		double[] vectorB = vec2.concatenateleftAndRightVector();
		
		double simValue = WordVectorSimilarity.cosineSimilarity(vectorA, vectorB);
		
		System.out.println("Cosine similarity of " + word1 + " , " + word2 + ": " + simValue);
		
	}

	// Test main method
	public static void main(String[] args) throws IOException {
		WordVectorSimilarity.dwvFactory.testReadFlorsVectors();
		WordVectorSimilarity.testSimilarity("house", "building");
		WordVectorSimilarity.testSimilarity("house", "house");
		WordVectorSimilarity.testSimilarity("house", "houses");
		WordVectorSimilarity.testSimilarity("building", "building");
		WordVectorSimilarity.testSimilarity("woman", "king");
		WordVectorSimilarity.testSimilarity("woman", "queen");
		WordVectorSimilarity.testSimilarity("man", "king");
		WordVectorSimilarity.testSimilarity("man", "queen");
		WordVectorSimilarity.testSimilarity("frankfurt", "berlin");
		WordVectorSimilarity.testSimilarity("rain", "sun");
		WordVectorSimilarity.testSimilarity("rain", "man");
		WordVectorSimilarity.testSimilarity("running", "going");
		WordVectorSimilarity.testSimilarity("running", "stopping");
		WordVectorSimilarity.testSimilarity("paris", "berlin");
		WordVectorSimilarity.testSimilarity("aaaaa", "man");
		
		
	}
}
