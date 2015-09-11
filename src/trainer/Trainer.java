package trainer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import corpus.Corpus;
import corpus.ModelFiles;
import data.Alphabet;
import data.Data;
import data.OffSets;
import data.Window;
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

public class Trainer {
	private Corpus corpus = new Corpus();
	private ModelFiles modelFiles = new ModelFiles();
	private Data data = new Data();
	private Alphabet alphabet = new Alphabet();
	private OffSets offSets = new OffSets();
	private int windowSize = 2;

	// API/Values for Liblinear
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
	public ModelFiles getModelFiles() {
		return modelFiles;
	}
	public void setModelFiles(ModelFiles modelFiles) {
		this.modelFiles = modelFiles;
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
	 * TODO hierix
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

				data.generateSentenceObjectFromConllLabeledSentence(tokens);

				// create window instances and store in list
				trainFromSentence(train, adjust);
				
				// do training for new sentence stored in this.data
				// trainFromSentence(train, adjust);

				// reset tokens
				tokens = new ArrayList<String[]>();
			}
			else {
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
	 * - create feature vectors -> DONE
	 * - add offSet to each window -> DONE
	 * - make new problem instance -> DONE
	 * 
	 * - add to problem space
	 * UNCLEAR:
	 * - is it necessary to know in advance problem.l -> YES
	 * - and problem.n => trainer.getProblem().n = OffSets.windowVectorSize;
	 */
	private void trainFromSentence(boolean train, boolean adjust) throws IOException {
		// for each token t_i of current training sentence do
		// System.out.println("Sentence no: " + data.getSentenceCnt());
		int mod = 100000;
		for (int i = 0; i < this.getData().getSentence().getWordArray().length; i++){
			int labelIndex = this.getData().getSentence().getLabelArray()[i];
			// create local context for tagging t_i of size 2*windowSize+1 centered around t_i

			Window tokenWindow = new Window(this.getData().getSentence(), i, windowSize, data, alphabet);
			// Fill the elements of the window
			// first boolean: training mode on/off
			// second boolean:  adjust offsets on/off
			// if adjust = true -> use offSets for mapping relative feature index to absolute feature index
			tokenWindow.fillWindow(train, adjust);

			ProblemInstance problemInstance = new ProblemInstance();
			problemInstance.createProblemInstanceFromWindow(tokenWindow);
			
			// Write out training instances into file, iff class ModelFiles is active
			if (this.getModelFiles().isActive()) {
				String longString = labelIndex + " " + problemInstance.toString();
				this.getModelFiles().getWriterTrainingInstances().write(longString);
				this.getModelFiles().getWriterTrainingInstances().write("\n");
			}

			// Print how many windows are created, and pretty print every mod-th window
			if ((Window.windowCnt % mod) == 0) {
				System.out.println("\n************");
				System.out.println("Windows filled: " + Window.windowCnt);
				// tokenWindow.ppWindowElement();
				// System.out.println(tokenWindow.toString());
				//System.out.println(labelIndex + " "+problemInstance.toString());
			}
		}
	}
	
	
}
