package trainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

	Problem problem = new Problem();
	problem.l = ... // number of training examples
	problem.n = ... // number of features
	problem.x = ... // feature nodes
	problem.y = ... // target values

	SolverType solver = SolverType.L2R_LR; // -s 0
	double C = 1.0;    // cost of constraints violation
	double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less

	Parameter parameter = new Parameter(solver, C, eps);
	Model model = Linear.train(problem, parameter);
	File modelFile = new File("model");
	model.save(modelFile);
	// load model or use it directly
	model = Model.load(modelFile);

	Feature[] instance = { new FeatureNode(1, 4), new FeatureNode(2, 2) };
	double prediction = Linear.predict(model, instance);

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
	private Corpus corpus = new Corpus();
	private Data data = new Data();
	private Alphabet alphabet = new Alphabet();
	private OffSets offSets = new OffSets();
	private int windowSize = 2;
	private ModelInfo modelInfo = new ModelInfo();

	// API/Values for Liblinear
	// GN: used in MDP
	private double bias = -1;
	// GN: default values as used in Flors
	// C -> cost of constraints violation
	// eps -> stopping criteria; influences number of iterations performed, the higher the less

	private Parameter parameter = new Parameter(SolverType.L2R_LR, 1.0, 0.01);

	private Problem problem = new Problem();

	// Setters and getters
	public Corpus getCorpus() {
		return corpus;
	}
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}
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
	
	public TrainerInMem(ModelInfo modelInfo) {
		this.setWindowSize(modelInfo.getWindowSize());
		this.setModelInfo(modelInfo);

		this.setParameter(new Parameter(
				modelInfo.getSolver(),
				modelInfo.getC(),
				modelInfo.getEps()));
	}

	public TrainerInMem (ModelInfo modelInfo, int windowSize){
		this.setWindowSize(windowSize);
		this.setModelInfo(modelInfo);

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
	 * For each token of a sentence create a window frame, add the label of the current sentence token wot the window
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
	 * Loops through a file where each line is conll encoded, collects tokens to a sentence 
	 * and calls windows creator on sentence.
	 * @param conllReader
	 * @param max if -1 then infinite else max sentences are processed and then methods stops
	 * @throws IOException
	 */
	private void createTrainingInstancesFromConllReader(BufferedReader conllReader, int max) throws IOException{
		String line = "";
		List<String[]> tokens = new ArrayList<String[]>();

		while ((line = conllReader.readLine()) != null) {
			if (line.isEmpty()) {
				// Stop if max sentences have been processed
				if  ((max > 0) && (data.getSentenceCnt() >= max)) break;

				// create internal sentence object and label maps
				data.generateSentenceObjectFromConllLabeledSentence(tokens);

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
		problem.n = OffSets.windowVectorSize;
		problem.x = new FeatureNode[problem.l][];
		problem.y = new double[problem.l];

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
	 */
	private void constructProblem(boolean train, boolean adjust) {
		int mod = 10000;
		int problemCnt = 0;

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

		System.out.println("Construct model:");
		this.runLiblinearTrainer();

		System.out.println("... done");
	}

	private void runLiblinearTrainer() throws IOException {
		long time1;
		long time2;
		Linear.disableDebugOutput();
		time1 = System.currentTimeMillis();
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
