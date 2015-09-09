package trainer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import corpus.Corpus;
import data.Alphabet;
import data.Data;
import data.OffSets;
import data.Window;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import features.WordFeatures;


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

public class Trainer {
	private Corpus corpus = new Corpus();
	private Data data = new Data();
	private Alphabet alphabet = new Alphabet();
	private OffSets offSets = new OffSets();
	private int windowSize = 2;

	// API/Vlaues for Liblinear
	// GN: used in MDP
	private double bias = -1;

	// GN: default values as used in Flors
	private Parameter parameter = new Parameter(SolverType.L2R_LR, 1.0, 0.01);
	// GN: used in MDP
	//private Parameter parameter = new Parameter(SolverType.MCSVM_CS, 0.1, 0.3);
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
	
	public Trainer (){
	}
	
	public Trainer (int windowSize){
		this.setWindowSize(windowSize);
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
	 * - create training instance
	 * - create feature vectors
	 * - add to problem
	 * 
	 * - after all training instances have been computed
	 * - do training
	 * - save model file
	 * - save label index set if Alpha part
	 */

	public void trainFromConllTrainingFile(String sourceFileName, int max)
			throws IOException {
		System.out.println("Do training from file: " + sourceFileName);
		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),"UTF-8"));
		String line = "";
		List<String[]> tokens = new ArrayList<String[]>();
		boolean train = true;
		boolean adjust = true;
		System.out.println("Train?: " + train + " Adjust?: " + adjust);
		
		while ((line = conllReader.readLine()) != null) {
			if (line.isEmpty()) {
				// Stop of max sentences have been processed
				if  ((max > 0) && (data.getSentenceCnt() >= max)) break;
				
				// If all conll lines of a sentence have been collected
				// extract the relevant information (here word and pos)
				// and make a sentence object of it (two parallel int[];)
				// as a side effect, word and pos SetIndexMaps are created
				// and stored in the data object
				data.generateSentenceObjectFromConllLabeledSentence(tokens);

				// do training for new sentence
				trainFromSentence(train, adjust);

				// reset tokens
				tokens = new ArrayList<String[]>();
			}
			else
			{
				// Collect each conll line of sentence into a List
				String[] tokenizedLine = line.split("\t");
				tokens.add(tokenizedLine);
			}
		}
		conllReader.close();
		System.out.println("... done");
	}

	/*
	 * - loop through sentence object
	 * - create training instance
	 * - create feature vectors -> DONE here
	 * - add offSet to each window -> just to check whether it will work !
	 * 
	 * - make new problem instance and add to problem space
	 * UNCLEAR:
	 * - is it necessary to know in advance problem.l -> # training instances -> # number of words (windows)
	 * - and problem.n => trainer.getProblem().n = OffSets.windowVectorSize;
	 */
	private void trainFromSentence(boolean train, boolean adjust) {
		// This is the main working horse!
		// for each token t_i of current training sentence do
		// System.out.println("Sentence no: " + data.getSentenceCnt());
		int mod = 100000;
		for (int i = 0; i < this.getData().getSentence().getWordArray().length; i++){
			// create local context for tagging t_i of size 2*windowSize+1 centered around t_i
			
			Window tokenWindow = new Window(this.getData().getSentence(), i, windowSize, data, alphabet, offSets);
			// Fill the elements of the window
			// first boolean: training mode on/off
			// second boolean:  adjust offsets on/off
			
			tokenWindow.fillWindow(train, adjust);
			
			// Print how many windows are created, and pretty print every mod-th window
			if ((Window.windowCnt % mod) == 0) {
				System.out.println("Windows filled: " + Window.windowCnt);
				tokenWindow.ppWindowElement();
				System.out.println(tokenWindow.toString());
			}
			
			// make a problem instance out of it using offSets for mapping relative feature index to absolute feature index
			
			// add to problem: unclear: can I add problem.n and problem.l at the end?

			
		}
		// When problem is created run trainer (or save training file for later usage)
	}

	public static void main(String[] args) throws IOException{
		Trainer trainer = new Trainer(2);

		trainer.alphabet.loadFeaturesFromFiles();
		trainer.offSets.initializeOffsets(trainer.alphabet, trainer.windowSize);
		
		trainer.trainFromConllTrainingFile("/Users/gune00/data/MLDP/english/english-train.conll", 10000);
		
		trainer.getProblem().n = OffSets.windowVectorSize;
		trainer.getProblem().l=trainer.getData().getSentenceCnt();

		
		System.out.println("Offsets: " + trainer.offSets.toString());
		System.out.println("Training instances: " + trainer.getProblem().l);
		System.out.println("Feature instances size: " + trainer.getProblem().n);
		System.out.println("Windows: " + Window.windowCnt);



	}
}
