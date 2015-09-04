package features;

public class Alphabet {
	private DistributedWordVectorFactory wordVectorFactory = new DistributedWordVectorFactory();
	private WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
	private WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
	
	// Setters and getters
	public DistributedWordVectorFactory getWordVectorFactory() {
		return wordVectorFactory;
	}
	public void setWordVectorFactory(DistributedWordVectorFactory wordVectorFactory) {
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
		this.wordVectorFactory.readFlorsCondensed();
		this.wordSuffixFactory.testReadSuffixList();
		this.wordShapeFactory.testReadShapeList();
	}

}
