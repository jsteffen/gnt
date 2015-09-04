package trainer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import corpus.Corpus;
import data.Data;
import data.OffSets;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import features.Alphabet;


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

	public void trainFromConllTrainingFile(String sourceFileName, String sourceEncoding)
			throws IOException {

		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),sourceEncoding));
		String line = "";
		List<String[]> tokens = new ArrayList<String[]>();

		while ((line = conllReader.readLine()) != null) {
			if (line.isEmpty()) {
				// If all conll lines of a sentence have been collected
				// extract the relevant information (here word and pos)
				// and make a sentence object of it (two parallel int[];)
				// as a side effect, word and pos SetIndexMaps are created
				// and stored in the data object
				data.generateSentenceObjectFromConllLabeledSentence(tokens);
				// do training for new sentence
				trainFromSentence();
			}
			else
			{
				// Collect each conll line of sentence into a List
				String[] tokenizedLine = line.split("\t");
				tokens.add(tokenizedLine);
			}
		}
		conllReader.close();
	}

	/*
	 * - loop through sentence object
	 * - create training instance
	 * - create feature vectors
	 * - make new problem instance and add to problem space
	 * UNCLEAR:
	 * - is it necessary to know in advance problem.l -> # training instances -> # number of words (windows)
	 * - and problem.n => trainer.getProblem().n = OffSets.windowVectorSize;
	 */
	private void trainFromSentence() {
		// TODO looks like the main horse

	}

	public static void main(String[] args){
		Trainer trainer = new Trainer();

		trainer.alphabet.loadFeaturesFromFiles();

		trainer.offSets.initializeOffsets(trainer.alphabet, trainer.windowSize);

		trainer.getProblem().n = OffSets.windowVectorSize;

		System.out.println(trainer.offSets.toString());
		System.out.println(trainer.getProblem().l);
		System.out.println(trainer.getProblem().n);



	}
}
