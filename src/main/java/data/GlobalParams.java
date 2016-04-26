package data;

public class GlobalParams {
	/**
	 * Globals flags for defining window site, number of sentences, vector dimension and
	 * subsampling range.
	 */
	public static String taggerName = "";
	
	public static int windowSize = 2;
	public static int numberOfSentences = -1;
	public static int dim = 0;
	public static double subSamplingThreshold = 0.000000001;
	
	/**
	 * This is a global flag to trigger saving of model input file;
	 */
	public static boolean saveModelInputFile = false;

}
