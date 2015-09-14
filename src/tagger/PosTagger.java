package tagger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import trainer.ProblemInstance;
import data.Alphabet;
import data.Data;
import data.OffSets;
import data.Window;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

public class PosTagger {
	private Data data = new Data();
	private Alphabet alphabet = new Alphabet();
	private OffSets offSets = new OffSets();
	private int windowSize = 2;
	private Model model ;

	private long time1 ;
	private long time2;

	// Setters and getters

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
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	
	// Init
	public PosTagger(){
	}

	// Methods

	public void initPosTagger(String modelFile, int windowSize) throws IOException{
		time1 = System.currentTimeMillis();
		
		System.out.println("Set window size: " + windowSize);
		this.setWindowSize(windowSize);
		
		System.out.println("Load feature files:");
		this.getAlphabet().loadFeaturesFromFiles();
		
		System.out.println("Load label set:");
		this.getData().readLabelSet();

		System.out.println("Resetting non-used variables ...");
		this.getAlphabet().clean();

		System.out.println("Initialize offsets:");
		this.getOffSets().initializeOffsets(this.getAlphabet(), this.getWindowSize());
		System.out.println("\t"+this.getOffSets().toString());
		
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1)+"\n");
		
		time1 = System.currentTimeMillis();
		
		System.out.println("Load model file: " + modelFile);
		this.setModel(Model.load(new File(modelFile)));
		
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
	}
	
	/**
	 * The same as trainer.TrainerInMem.createWindowFramesFromSentence()!
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
	 * Iterate through all window frames:
	 * - create the feature vector: train=false means: handle unknown words; adjust=true: means adjust feature indices
	 * - create a problem instance -> mainly the feature vector
	 * - and call the learner with model and feature vector
	 * - save the predicted label in the corresponding field of the word in the sentence.
	 */
	private void constructProblemAndTag() {
		int problemCnt = 0;
		int prediction = 0;
		
		for (int i = 0; i < data.getInstances().size();i++){
			// For each window frame of a sentence
			Window nextWindow = data.getInstances().get(i);
			// Fill the frame with all availablel features. First boolean sets 
			// training mode to false which means that unknown words are handled.
			nextWindow.fillWindow(false, true);
			// Create the feature vector
			ProblemInstance problemInstance = new ProblemInstance();
			problemInstance.createProblemInstanceFromWindow(nextWindow);
			problemCnt++;
			
			// Call the learner to predict the label
			prediction = (int) Linear.predict(this.getModel(), problemInstance.getFeatureVector());
			
			//Here, I am assuming that sentence length equals # of windows
			// So store predicted label i to word i
			this.getData().getSentence().getLabelArray()[i]=prediction;
			nextWindow.clean();
		}	
	}
	
	public void tagTokens(String[] tokens) throws IOException{
		this.time1 = System.currentTimeMillis();
		
		// create internal sentence object
		this.getData().generateSentenceObjectFromUnlabeledTokens(tokens);
		
		// create window frames from sentence and store in list
		this.createWindowFramesFromSentence();
		
		// create feature vector instance for each window frame and tag
		this.constructProblemAndTag();
		
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1)+"\n");
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

	public static void main(String[] args) throws IOException{
		int windowSize = 2;
		String modelFile1 = "/Users/gune00/data/wordVectorTests/testModel_L2R_LR.txt";
		String modelFile2 = "/Users/gune00/data/wordVectorTests/testModel_MCSVM_CS.txt";

		PosTagger posTagger = new PosTagger();

		posTagger.initPosTagger(modelFile2, windowSize);
		
		String sentence ="This is the first call of the GNT-tagger . ";
		sentence = "Do not underestimate the effects of the Internet economy on load growth . "
				+ "I have been preaching the tremendous growth described below for the last year . "
				+ "The utility infrastructure simply can not handle these loads at the distribution level "
				+ "and ultimatley distributed generation will be required for power quality reasons . "
				+ "The City of Austin , TX has experienced 300 + MW of load growth this year due to server farms and technology companies . "
				+ "There is a 100 MW server farm trying to hook up to HL&P as we speak and "
				+ "they can not deliver for 12 months due to distribution infrastructure issues . "
				+ "Obviously , Seattle , Porltand , Boise , Denver , "
				+ "San Fran and San Jose in your markets are in for a rude awakening in the next 2 - 3 years . "
				+ "George Hopley 09/05/2000 11:41 AM Internet Data Gain Is a Major Power Drain on Local Utilities"
				+ "( September 05 , 2000 )"
				+ "In 1997 , a little - known Silicon Valley company called Exodus Communications opened a 15,000 - square - foot data center in Tukwila . "
				+ "The mission was to handle the Internet traffic and computer servers for the region 's growing number of dot - coms . "
				+ "Fast - forward to summer 2000 . "
				+ "Exodus is now wrapping up construction on a new 13 - acre , 576,000 - square - foot data center less than a mile from its original facility . "
				+ "Sitting at the confluence of several fiber optic backbones , the Exodus plant will consume "
				+ "enough power for a small town and eventually house Internet servers for firms such as Avenue A , Microsoft and Onvia.com . ";
		
		String[] tokens = sentence.split(" ");
		posTagger.tagTokens(tokens);
		
		System.out.println("#Tokens: " + tokens.length);
		System.out.println(posTagger.taggedSentenceToString());
	}

}
