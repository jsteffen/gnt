package data;

import features.WordDistributedFeatureFactory;
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

	public void loadFeaturesFromFiles(){
		this.wordVectorFactory.readDistributedWordFeaturesSparse();
		this.wordSuffixFactory.testReadSuffixList();
		this.wordShapeFactory.testReadShapeList();
	}

}
