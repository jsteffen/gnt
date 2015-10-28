package data;

import features.WordClusterFeatureFactory;
import features.WordDistributedFeatureFactory;
import features.WordShapeFeatureFactory;
import features.WordSuffixFeatureFactory;

/**
 * This class holds storage for the feature to liblinear indexing mappings.
 * It also defines which features are active/inactive
 * 
 * @author gune00
 *
 */
public class Alphabet {
	private WordDistributedFeatureFactory wordVectorFactory = new WordDistributedFeatureFactory();
	private WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
	private WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
	private WordClusterFeatureFactory wordClusterFactory = new WordClusterFeatureFactory();
	
	public static boolean withWordFeats = true;
	public static boolean withShapeFeats = true;
	public static boolean withSuffixFeats = true;
	public static boolean withClusterFeats = false;

	// Setters and getters
	public WordDistributedFeatureFactory getWordVectorFactory() {
		return wordVectorFactory;
	}
	public void setWordVectorFactory(WordDistributedFeatureFactory wordVectorFactory) {
		this.wordVectorFactory = wordVectorFactory;
	}
	public WordSuffixFeatureFactory getWordSuffixFactory() {
		return wordSuffixFactory;
	}
	public void setWordSuffixFactory(WordSuffixFeatureFactory wordSuffixFactory) {
		this.wordSuffixFactory = wordSuffixFactory;
	}
	public WordShapeFeatureFactory getWordShapeFactory() {
		return wordShapeFactory;
	}
	public void setWordShapeFactory(WordShapeFeatureFactory wordShapeFactory) {
		this.wordShapeFactory = wordShapeFactory;
	}
	public WordClusterFeatureFactory getWordClusterFactory() {
		return wordClusterFactory;
	}
	public void setWordClusterFactory(WordClusterFeatureFactory wordClusterFactory) {
		this.wordClusterFactory = wordClusterFactory;
	}
	
	// Methods
	
	public void loadFeaturesFromFiles(String taggerName, int dim){
		if (Alphabet.withWordFeats) this.wordVectorFactory.readDistributedWordFeaturesSparse(taggerName, dim);
		if (Alphabet.withShapeFeats) this.wordSuffixFactory.readSuffixList(taggerName);
		if (Alphabet.withShapeFeats) this.wordShapeFactory.readShapeList(taggerName);
		if (Alphabet.withClusterFeats) this.wordClusterFactory.readClusterIdList(taggerName);
	}

	public void clean(){
		System.out.println("Cleaning word vectors ... ");
		this.wordVectorFactory.clean();
		
		System.out.println("Cleaning suffix list ... ");
		this.wordSuffixFactory.clean();
		
		System.out.println("Cleaning shape list ... ");
		this.wordShapeFactory.clean();
		
		System.out.println("Cleaning cluster ID list ... ");
		this.wordClusterFactory.clean();
	}
	
	public static String toActiveFeatureString(){
		String output = "\nActive features\n";
		output += "withWordFeats= 	" + withWordFeats +"\n";
		output += "withShapeFeats=  " + withShapeFeats +"\n";
		output += "withSuffixFeats= " + withSuffixFeats +"\n";
		output += "withClusterFeats= " + withClusterFeats +"\n";
		return output;	
	}

}
