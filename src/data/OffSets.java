package data;


public class OffSets {
	public static int wvLeftSize = -1;
	public static int wvRightSize = -1;
	public static int suffixSize = -1;
	public static int shapeSize = -1;
	public static int tokenVectorSize = -1;
	public static int windowVectorSize = -1;
	
	// This is the length of the feature vector of a window element, this is constant, as well is the window vector size 
	public void initializeOffsets(Alphabet alphabet, int windowSize){
		OffSets.wvLeftSize = alphabet.getWordVectorFactory().getIw2num().size() + 1; 
		// plus one for unknown word statistics, cf. features.WordDistributedFeature.WordDistributedFeature(int)
		OffSets.wvRightSize = alphabet.getWordVectorFactory().getIw2num().size() + 1;
		OffSets.suffixSize = alphabet.getWordSuffixFactory().getSuffix2num().size();
		OffSets.shapeSize = alphabet.getWordShapeFactory().getSignature2index().size();
		OffSets.tokenVectorSize = ( wvLeftSize + wvRightSize + suffixSize  + shapeSize);
		OffSets.windowVectorSize = (tokenVectorSize * (windowSize*2+1)+1);
	}
	
	
	public String toString(){
		String output = "";
		output += "wvLeftSize: " + wvLeftSize + "; wvRightSize: " + OffSets.wvLeftSize +
				"; suffixSize: " + suffixSize + "; shapSize: " + shapeSize + 
				"; total token vector size: " + tokenVectorSize + 
				"; total window vector size: " + windowVectorSize;
		return output;
	}
	
}
