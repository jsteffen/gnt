package data;

public class GlobalParams {
	/**
	 * Unique name of the tagger
	 */
	private String taggerName = "";
	
	/**
	 * Global path to features
	 */	
	private String featureFilePathname = "resources/features/";
	
	/**
	 * Global path to eval files
	 */	
	private String evalFilePathname = "resources/eval/";
	
	/**
	 * This is a global flag to trigger saving of model input file;
	 */
	private boolean saveModelInputFile = false;
	
	/**
	 * Globals flags for defining window size, number of sentences, vector dimension and
	 * subsampling range.
	 */
	
	private int windowSize = 2;
	private int numberOfSentences = -1;
	private int dim = 0;
	private double subSamplingThreshold = 0.000000001;
	
	
	public String getTaggerName() {
		return taggerName;
	}
	public void setTaggerName(String taggerName) {
		this.taggerName = taggerName;
	}
	public String getFeatureFilePathname() {
		return featureFilePathname;
	}
	public void setFeatureFilePathname(String featureFilePathname) {
		this.featureFilePathname = featureFilePathname;
	}
	public String getEvalFilePathname() {
		return evalFilePathname;
	}
	public void setEvalFilePathname(String evalFilePathname) {
		this.evalFilePathname = evalFilePathname;
	}
	public boolean isSaveModelInputFile() {
		return saveModelInputFile;
	}
	public void setSaveModelInputFile(boolean saveModelInputFile) {
		this.saveModelInputFile = saveModelInputFile;
	}
	public int getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}
	public int getNumberOfSentences() {
		return numberOfSentences;
	}
	public void setNumberOfSentences(int numberOfSentences) {
		this.numberOfSentences = numberOfSentences;
	}
	public int getDim() {
		return dim;
	}
	public void setDim(int dim) {
		this.dim = dim;
	}
	public double getSubSamplingThreshold() {
		return subSamplingThreshold;
	}
	public void setSubSamplingThreshold(double subSamplingThreshold) {
		this.subSamplingThreshold = subSamplingThreshold;
	}
}
