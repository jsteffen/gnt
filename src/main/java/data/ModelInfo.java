package data;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import de.bwaldvogel.liblinear.SolverType;

public class ModelInfo {

	public static SolverType solver = SolverType.L2R_LR; // -s 0
	public static double C = 1.0;    // cost of constraints violation
	public static double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less

	private String modelFilePrefix = "resources/models/model_";
	private String modelFile = "";

	private String modelInputFilePrefix = "resources/modelInputFiles/modelInputFile_";
	private String modelInputFile = "";
	private BufferedWriter modelInputFileWriter = null;

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
	

	public SolverType getSolver() {
		return solver;
	}
	
	public double getC() {
		return C;
	}
	
	public double getEps() {
		return eps;
	}
	
	public String getTaggerName() {
		return taggerName;
	}
	public void setTaggerName(String taggerName) {
		ModelInfo.taggerName = taggerName.toUpperCase();
	}

	public String getModelFilePrefix() {
		return modelFilePrefix;
	}
	public void setModelFilePrefix(String modelFilePrefix) {
		this.modelFilePrefix = modelFilePrefix;
	}
	public String getModelFile() {
		return modelFile;
	}
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	public String getModelInputFilePrefix() {
		return modelInputFilePrefix;
	}
	public void setModelInputFilePrefix(String modelInputFilePrefix) {
		this.modelInputFilePrefix = modelInputFilePrefix;
	}
	public String getModelInputFile() {
		return modelInputFile;
	}
	public void setModelInputFile(String modelInputFile) {
		this.modelInputFile = modelInputFile;
	}
	public BufferedWriter getModelInputFileWriter() {
		return modelInputFileWriter;
	}
	public void setModelInputFileWriter(BufferedWriter modelInputFileWriter) {
		this.modelInputFileWriter = modelInputFileWriter;
	}
	//
	public ModelInfo(){
	}

	public ModelInfo(String type){
		if (type.equalsIgnoreCase("FLORS")) this.initFlorsInfo();
		else
			if (type.equalsIgnoreCase("MDP")) this.initMDPInfo();
			else
				if (type.equalsIgnoreCase("GNT")) this.initGNTInfo();
				else
				{
					System.err.println("Unknown model info type: " + type);
					System.exit(0);
				}
		this.setModelFile(this.getModelFilePrefix()+this.getSolver()+".txt");
	}

	public void initFlorsInfo(){
		// L2-regularized L2-loss support vector classification (primal)
		ModelInfo.solver = SolverType.L2R_L2LOSS_SVC;
		ModelInfo.C = 1.0;
		ModelInfo.eps = 0.01;
	}

	public void initMDPInfo(){
		// multi-class SVM by Crammer and Singer
		ModelInfo.solver = SolverType.MCSVM_CS;
		ModelInfo.C = 0.1;
		ModelInfo.eps = 0.3;
	}

	public void initGNTInfo(){
		// L2-regularized logistic regression (primal)
		ModelInfo.solver = SolverType.L2R_LR;
		ModelInfo.C = 1.0;
		ModelInfo.eps = 0.01;
	}

	public String toString(){
		String output ="ModelInfo:\n";
		output += "Solver: " + this.getSolver()+"\n";
		output += "C: " + this.getC()+"\n";
		output += "Eps: " + this.getEps()+"\n";
		output += "ModelFilePrefix: " + this.getModelFilePrefix()+"\n";
		output += "ModelFileNames: " + this.getModelFile()+"\n";
		return output;

	}

	/**
	 * A mode file name is build from
	 * <p>modelFilePrefix + taggerName + windowSize + dimension + number of training sentences + wordFeat-flag + shapeFeat-flag + suffixFeat-flag
	 * @param windowSize + ".txt"
	 * @param dim
	 * @param numberOfSentences
	 */
	public void createModelFileName(int windowSize, int dim, int numberOfSentences) {
		String wordFeatString = (Alphabet.withWordFeats)?"T":"F";
		String shapeFeatString = (Alphabet.withShapeFeats)?"T":"F";
		String suffixFeatString = (Alphabet.withSuffixFeats)?"T":"F";
		String clusterFeatString = (Alphabet.withClusterFeats)?"T":"F";
		if (wordFeatString.equals("F")) dim=0;

		String fileSuffix = this.getTaggerName()+"_"+windowSize+"_"+dim+"iw"+numberOfSentences+"sent_"+
				wordFeatString+shapeFeatString+suffixFeatString+clusterFeatString+"_"+
				this.getSolver()+".txt";
		
		this.modelFile = this.modelFilePrefix + fileSuffix;
		
		if (ModelInfo.saveModelInputFile){
			//Only if ModelInfo.saveModelInputFile=true then save the modelInputFile
			this.modelInputFile = this.modelInputFilePrefix + fileSuffix;
			// And create and open the writerBuffer
			try {
				this.setModelInputFileWriter(new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(this.modelInputFile),"UTF-8")));
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
