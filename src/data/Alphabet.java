package data;

import features.WordDistributedFeatureFactory;
import features.WordFeatures;
import features.WordShapeFeatureFactory;
import features.WordSuffixFeatureFactory;

public class Alphabet {
	private WordDistributedFeatureFactory wordVectorFactory = new WordDistributedFeatureFactory();
	private WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
	private WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();

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

	// Methods

	public void loadFeaturesFromFiles(String taggerName, int dim){
		if (WordFeatures.withWordFeats) this.wordVectorFactory.readDistributedWordFeaturesSparse(taggerName, dim);
		if (WordFeatures.withShapeFeats) this.wordSuffixFactory.readSuffixList(taggerName);
		if (WordFeatures.withShapeFeats) this.wordShapeFactory.readShapeList(taggerName);
	}

	public void clean(){
		System.out.println("Cleaning word vectors ... ");
		this.wordVectorFactory.clean();
		
		System.out.println("Cleaning suffix list ... ");
		this.wordSuffixFactory.clean();
		
		System.out.println("Cleaning shape list ... ");
		this.wordShapeFactory.clean();

	}

}
