package trainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import corpus.Corpus;
import data.Alphabet;
import data.Data;
import data.ModelInfo;
import data.OffSets;
import data.Window;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;


/**
 * General usage of API (from http://liblinear.bwaldvogel.de/ & https://github.com/bwaldvogel/liblinear-java):

	<p>Problem problem = new Problem();
	<p>problem.l = ... // number of training examples
	<p>problem.n = ... // number of features
	<p>problem.x = ... // feature nodes
	<p>problem.y = ... // target values

	<p>SolverType solver = SolverType.L2R_LR; // -s 0
	<p>double C = 1.0;    // cost of constraints violation
	<p>double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less

	<p>Parameter parameter = new Parameter(solver, C, eps);
	<p>Model model = Linear.train(problem, parameter);
	<p>File modelFile = new File("model");
	<p>model.save(modelFile);
	<p>// load model or use it directly
	<p>model = Model.load(modelFile);
<p>
	<p>Feature[] instance = { new FeatureNode(1, 4), new FeatureNode(2, 2) };
	<p>double prediction = Linear.predict(model, instance);

 *<p>
 * My idea is to create directly a FeatureNode list from a training instance
 * by using the relative indices from the alphabet and using corresponding offsets.
 * In order to do so, I need the tokenVectorSize in advance (non-incremental version) or 
 * I need to create an intermediate representation with window-size many sublists of sublist (for the token feature parts)
 * with relative indices, for which I then create the final one (incremental version);
 * such a intermediate representation should be useful for testing anyway.
 * </p>
 * @author gune00
 *
 */

public class TrainerInMem {
	private Data data = new Data();
	private Alphabet alphabet = new Alphabet();
	private OffSets offSets = new OffSets();
	private int windowSize = 2;
	private ModelInfo modelInfo = new ModelInfo();
	public static boolean debug = false;

	// API/Values for Liblinear
	// GN: biased -> used in Problem() -> if <= 0 add extra feature
	private double bias = -1;
	// GN: default values as used in Flors
	// C -> cost of constraints violation
	// eps -> stopping criteria; influences number of iterations performed, the higher the less

	private Parameter parameter = new Parameter(SolverType.L2R_LR, 1.0, 0.01);

	private Problem problem = new Problem();

	// Setters and getters

	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public ModelInfo getModelInfo() {
		return modelInfo;
	}
	public void setModelInfo(ModelInfo modelInfo) {
		this.modelInfo = modelInfo;
	}
	public Alphabet getAlphabet() {
		return alphabet;
	}
	public void setAlphabet(Alphabet alphabet) {
		this.alphabet = alphabet;
	}
	public OffSets getOffSets() {
		return offSets;
	}
	public void setOffSets(OffSets offSets) {
		this.offSets = offSets;
	}
	public int getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}
	public double getBias() {
		return bias;
	}
	public void setBias(double bias) {
		this.bias = bias;
	}
	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public Problem getProblem() {
		return problem;
	}
	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	// Instances
	public TrainerInMem (){
	}

	public TrainerInMem (int windowSize){
		this.setWindowSize(windowSize);
	}

	public TrainerInMem (ModelInfo modelInfo, int windowSize){
		this.setWindowSize(windowSize);
		this.setModelInfo(modelInfo);
		this.setData(new Data(modelInfo.getTaggerName()));

		this.setParameter(new Parameter(
				modelInfo.getSolver(),
				modelInfo.getC(),
				modelInfo.getEps()));
	}

	// Methods

	/*
	 * training steps
	 * - init corpus
	 * - init alphabet
	 * - init window
	 * - init offsets
	 * - init liblinear (non-incremental version, since needs feature vector size)
	 * 
	 * - readConllTrainingFile
	 * - create sentence object
	 * 
	 * - loop through sentence object
	 * - create training instance frames and store
	 * - after all frames have been computed
	 * - create feature vectors
	 * - add to problem
	 * - do training
	 * - save model file
	 */


	/**
	 * For each token of a sentence create a window frame, add the label of the current sentence token wrt the window
	 * and store it in Data instances.
	 * The latter is a global storage and stores all frames first, before the windows are filled.
	 * I do this because I do not know in advance the number of sentences and hence, the number of tokens in a file.
	 * @throws IOException
	 */
	private void createWindowFramesFromSentence() throws IOException {
		// for each token t_i of current training sentence do
		// System.out.println("Sentence no: " + data.getSentenceCnt());
		int mod = 100000;
		for (int i = 0; i < this.getData().getSentence().getWordArray().length; i++){
			int labelIndex = this.getData().getSentence().getLabelArray()[i];
			// create local context for tagging t_i of size 2*windowSize+1 centered around t_i

			Window tokenWindow = new Window(this.getData().getSentence(), i, windowSize, data, alphabet);
			tokenWindow.setLabelIndex(labelIndex);

			this.getData().getInstances().add(tokenWindow);

			// Print how many windows are created so far, and pretty print every mod-th window
			if ((Window.windowCnt % mod) == 0) {
				System.out.println("\n************");
				System.out.println("# Window instances: " + Window.windowCnt);
			}
		}
	}

	/**
	 * Loops through a file where each line is conll encoded, collect tokens to a sentence 
	 * and call windows creator on sentence.
	 * Steps involved:
	 * <p>- collect conll tokens in list
	 * <p>- create internal sentence object and label maps
	 * <p>- create window frames and store in list (non-feature filled windows): 
	 *      I do this, because each window is then filled iteratively when calling the trainer; it actually saves space
	 * <p>-	Finally, feature files for label set and word set lists are created and stored for taggerName
	 * @param conllReader
	 * @param max if -1 then infinite else max sentences are processed and then methods stops
	 * @throws IOException
	 */
	private void createTrainingInstancesFromConllReader(BufferedReader conllReader, int max) throws IOException{
		String line = "";
		List<String[]> tokens = new ArrayList<String[]>();

		while ((line = conllReader.readLine()) != null) {
			if (line.isEmpty()) {
				if  ((max > 0) && (data.getSentenceCnt() > max)) break;

				// create internal sentence object and label maps
				data.generateSentenceObjectFromConllLabeledSentence(tokens);

				// System.out.println("In:  " + this.taggedSentenceToString());

				// create window frames and store in list
				createWindowFramesFromSentence();

				// reset tokens
				tokens = new ArrayList<String[]>();
			}
			else {
				String[] tokenizedLine = line.split("\t");
				tokens.add(tokenizedLine);
			}
		}
		conllReader.close();
		data.saveLabelSet();
		data.saveWordSet();
		System.out.println("... done");
	}

	/**
	 * initialize problem for liblinear using
	 * Window.windowCnt for problem.l (training instance size)
	 * OffSets.windowVectorSize for problem.n (OffSets.tokenVectorSize*windowSize+1)
	 */
	private void initProblem(){
		Problem problem = new Problem();
		problem.l = Window.windowCnt;
		//problem.n = OffSets.windowVectorSize;
		problem.x = new FeatureNode[problem.l][];
		problem.y = new double[problem.l];
		problem.bias = this.getBias();

		this.setProblem(problem);

		System.out.println("problem.l: " + problem.l);
		System.out.println("problem.n: " + problem.n);
		System.out.println("problem.y.size: " + problem.y.length);
		System.out.println("problem.x.size: " + problem.x.length);
	}

	/**
	 * Loop through all window frames. Fill the window, adjust the feature indices
	 * and create a feature vector for the filled window.
	 * This is directly add to problem.x, where the corresponding label of the window
	 * is added to problem.y
	 * @param train
	 * @param adjust
	 * @throws IOException 
	 */
	private void constructProblem(boolean train, boolean adjust) throws IOException {
		int mod = 10000;
		int problemCnt = 0;

		// Initialize problem with potential feature vector size and number of training instances
		// and size of x and y which uses training instance
		// current element has index i
		this.initProblem();

		for (int i = 0; i < data.getInstances().size();i++){
			Window nextWindow = data.getInstances().get(i);
			nextWindow.fillWindow(train, adjust);
			ProblemInstance problemInstance = new ProblemInstance();
			problemInstance.createProblemInstanceFromWindow(nextWindow);
			problemCnt++;

			this.getProblem().y[i]=nextWindow.getLabelIndex();
			this.getProblem().x[i]=problemInstance.getFeatureVector();

			nextWindow.clean();

			// Print how many problems are created so far
			if ((problemCnt % mod) == 0) {
				System.out.println("************");
				System.out.println("Problem instances created: " + problemCnt);
			}
		}	

		// Number of feature can be set here, because we know the number of examples now.
		System.out.println("Window lenght: " + this.getProblem().x[0].length);
		this.getProblem().n = OffSets.windowVectorSize;

	}

	/**
	 * Main pipeline for training a liblinear model from a training file with conll encoded labeled
	 * examples. 
	 * @param sourceFileName
	 * @param max
	 * @throws IOException
	 */
	public void trainFromConllTrainingFileInMemory(String sourceFileName, int max)
			throws IOException {
		long time1;
		long time2;

		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),"UTF-8"));
		boolean train = true;
		boolean adjust = true;

		System.out.println("Do training with TrainerInMem() from file: " + sourceFileName);
		System.out.println("Train?: " + train + " Adjust?: " + adjust);
		System.out.println(this.getModelInfo().toString());

		time1 = System.currentTimeMillis();
		this.createTrainingInstancesFromConllReader(conllReader, max);
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println("Offsets: " + this.getOffSets().toString());
		System.out.println("Sentences: " + this.getData().getSentenceCnt());
		System.out.println("Feature instances size: " + OffSets.windowVectorSize);
		System.out.println("Training instances: " + Window.windowCnt);

		System.out.println("Construct problem:");
		time1 = System.currentTimeMillis();
		this.constructProblem(train, adjust);
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));	

		System.out.println("Average window vector lenght: " + ProblemInstance.cumLength/Window.windowCnt);
		System.out.println("Approx. GB needed: " + ((ProblemInstance.cumLength/Window.windowCnt)*Window.windowCnt*8+Window.windowCnt)/1000000000.0);

		time1 = System.currentTimeMillis();
		this.runLiblinearTrainer();
		time2 = System.currentTimeMillis();
		System.out.println("Complete time for training and writing model (msec): " + (time2-time1));	
	}

	public String taggedSentenceToString(){
		String output ="";
		int mod = 10;
		int cnt = 0;
		for (int i=0; i < this.getData().getSentence().getWordArray().length;i++){
			output += this.getData().getWordSet().getNum2label().get(this.getData().getSentence().getWordArray()[i])+"/"+
					this.getData().getLabelSet().getNum2label().get(this.getData().getSentence().getLabelArray()[i])+" ";
			cnt++;
			if ((cnt % mod)==0) output+="\n";
		}
		return output;

	}

	private void runLiblinearTrainer() throws IOException {
		long time1;
		long time2;
		Linear.disableDebugOutput();
		time1 = System.currentTimeMillis();
		System.out.println("problem.n: " + this.getProblem().n);
		System.out.println("Do training:");
		Model model = Linear.train(this.getProblem(), this.getParameter());
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println("Save  model file: " + modelInfo.getModelFile());
		time1 = System.currentTimeMillis();
		model.save(new File(modelInfo.getModelFile()));
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));		
	}
}
