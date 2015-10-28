package data;

public class OffSets {
	public static int wvLeftSize = -1;
	public static int wvRightSize = -1;
	public static int suffixSize = -1;
	public static int shapeSize = -1;
	public static int clusterIdSize = -1;
	public static int tokenVectorSize = -1;
	public static int windowVectorSize = -1;
	
	// This is the length of the feature vector of a window element, this is constant, as well is the window vector size 

	public void initializeOffsets(Alphabet alphabet, int windowSize){
		OffSets.wvLeftSize = (Alphabet.withWordFeats)?alphabet.getWordVectorFactory().getIw2num().size() + 1:0; 
		// plus one for unknown word statistics, cf. features.WordDistributedFeature.WordDistributedFeature(int)
		OffSets.wvRightSize = (Alphabet.withWordFeats)?alphabet.getWordVectorFactory().getIw2num().size() + 1:0;
		OffSets.suffixSize = (Alphabet.withSuffixFeats)?alphabet.getWordSuffixFactory().getSuffix2num().size():0;
		OffSets.shapeSize = (Alphabet.withShapeFeats)?alphabet.getWordShapeFactory().getSignature2index().size():0;
		OffSets.clusterIdSize = (Alphabet.withClusterFeats)?alphabet.getWordClusterFactory().getClusterIdcnt():0;
		OffSets.tokenVectorSize = ( wvLeftSize + wvRightSize + suffixSize  + shapeSize + clusterIdSize);
		OffSets.windowVectorSize = (tokenVectorSize * (windowSize*2+1)+1);
	}
	
	public String toString(){
		String output = "";
		output += "wvLeftSize: " + wvLeftSize + "; wvRightSize: " + OffSets.wvLeftSize +
				"; suffixSize: " + suffixSize + "; shapSize: " + shapeSize + "; clusterIDsize: " + clusterIdSize +
				"; total token vector size: " + tokenVectorSize + 
				"; total window vector size: " + windowVectorSize;
		return output;
	}
	
}
