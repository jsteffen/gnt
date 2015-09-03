package trainer;

import features.DistributedWordVectorFactory;
import features.WordShapeFeatureFactory;
import features.WordSuffixFeatureFactory;

public class Trainer {
	private DistributedWordVectorFactory wordVectorFactory = new DistributedWordVectorFactory();
	private WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
	private WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
	
	private int windowSize = 2;
	
	private Liblinear liblinear = new Liblinear("modelfileDummy");
	
	private int[] featureIndex = new int[92195];

	public void printFeatureIndex(){
		int cnt = -1;
		for (int x : featureIndex){
			cnt++;
			System.out.println(cnt +": " + featureIndex[x]);
		}
	}
	
	/*
	 * My idea is to create directly a FeatureNode list from a training instance
	 * by using the relative indices from the alphabet and using corresponding offsets.
	 * In order to do so, I need the tokenVectorSize in advance (non-incremental version) or 
	 * I need to create an intermediate representation with window-size many sublists of sublist (for the token feature parts)
	 * with relative indices, for which I then create the final one (incremental version);
	 * such a intermediate representation should be useful for testing anyway.
	 */
	
	public static void main(String[] args){
		Trainer trainer = new Trainer();
		
		trainer.wordVectorFactory.readFlorsCondensed();
		trainer.wordSuffixFactory.testReadSuffixList();
		trainer.wordShapeFactory.testReadShapList();
		
		int wvLeftSize = trainer.wordVectorFactory.getIw2num().size() + 1;
		int wvRightSize = trainer.wordVectorFactory.getIw2num().size() + 1;
		int suffixSize = trainer.wordSuffixFactory.getNum2suffix().size();
		int shapeSize = trainer.wordShapeFactory.getIndex2signature().size();
		int tokenVectorSize = ( wvLeftSize + wvRightSize + suffixSize  + shapeSize);
		int windowVectorSize = tokenVectorSize * 5;
		
		System.out.println(
				"wvLeftSize: " + wvLeftSize + " wvRightSize: " + wvLeftSize +
				" suffixSize: " + suffixSize + " shapSize: " + shapeSize);
		System.out.println("total token vector size: " + tokenVectorSize);
		System.out.println("total window vector size: " + windowVectorSize);
		
		
		
	}
}
