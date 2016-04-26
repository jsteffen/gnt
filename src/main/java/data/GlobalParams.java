package data;

public class GlobalParams {
	/**
	 * Unique name of the tagger
	 */
	public static String taggerName = "";
	
	/**
	 * Global path to features
	 */	
	public static String featureFilePathname = "resources/features/";
	
	/**
	 * Global path to eval files
	 */	
	public static String evalFilePathname = "resources/eval/";
	
	/**
	 * This is a global flag to trigger saving of model input file;
	 */
	public static boolean saveModelInputFile = false;
	
	/**
	 * Globals flags for defining window size, number of sentences, vector dimension and
	 * subsampling range.
	 */
	
	public static int windowSize = 2;
	public static int numberOfSentences = -1;
	public static int dim = 0;
	public static double subSamplingThreshold = 0.000000001;

}
