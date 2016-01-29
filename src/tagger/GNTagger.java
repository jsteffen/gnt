package tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import corpus.EvalConllFile;
import trainer.ProblemInstance;
import data.Alphabet;
import data.Data;
import data.ModelInfo;
import data.OffSets;
import data.Sentence;
import data.Window;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

public class GNTagger {
	private Data data = new Data();
	private Alphabet alphabet = new Alphabet();
	private ModelInfo modelInfo = new ModelInfo();
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
	public ModelInfo getModelInfo() {
		return modelInfo;
	}
	public void setModelInfo(ModelInfo modelInfo) {
		this.modelInfo = modelInfo;
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
	public GNTagger(){
	}

	public GNTagger(ModelInfo modelInfo) {
		this.setModelInfo(modelInfo);
		this.setData(new Data(modelInfo.getTaggerName()));
	}

	// Methods

	public void initGNTagger(int windowSize, int dim) throws IOException{
		time1 = System.currentTimeMillis();

		System.out.println("Set window size: " + windowSize);
		this.setWindowSize(windowSize);

		System.out.println("Load feature files with dim: " + dim);
		this.getAlphabet().loadFeaturesFromFiles(this.getModelInfo().getTaggerName(), dim);

		System.out.println("Load label set:");
		this.getData().readLabelSet();

		System.out.println("Cleaning non-used variables in Alphabet and in Data:");
		this.getAlphabet().clean();
		this.getData().cleanWordSet();

		System.out.println("Initialize offsets:");
		this.getOffSets().initializeOffsets(this.getAlphabet(), this.getWindowSize());
		System.out.println("\t"+this.getOffSets().toString());

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1)+"\n");

		time1 = System.currentTimeMillis();

		System.out.println("Load model file: " + this.getModelInfo().getModelFile());
		this.setModel(Model.load(new File(this.getModelInfo().getModelFile())));
		System.out.println(".... DONE!");

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
		System.out.println(this.getModel().toString()+"\n");
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
			// Assume that both arrays together define an ordered one-to-one correspondence 
			// between token and label (POS)
			int labelIndex = this.getData().getSentence().getLabelArray()[i];

			// create local context for tagging t_i of size 2*windowSize+1 centered around t_i
			Window tokenWindow = new Window(this.getData().getSentence(), i, windowSize, data, alphabet);
			tokenWindow.setLabelIndex(labelIndex);

			this.getData().getInstances().add(tokenWindow);

			// Print how many windows are created so far, and pretty print every mod-th window
			if ((Window.windowCnt % mod) == 0) {
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
	 * 
	 * Mainly the same as trainer.TrainerInMem.constructProblem(train, adjust), but uses predictor
	 */
	private void constructProblemAndTag(boolean train, boolean adjust) {
		int prediction = 0;

		for (int i = 0; i < data.getInstances().size();i++){
			// For each window frame of a sentence
			Window nextWindow = data.getInstances().get(i);
			// Fill the frame with all available features. First boolean sets 
			// training mode to false which means that unknown words are handled.
			nextWindow.fillWindow(train, adjust);
			// Create the feature vector
			ProblemInstance problemInstance = new ProblemInstance();
			problemInstance.createProblemInstanceFromWindow(nextWindow);

			if (ModelInfo.saveModelInputFile){
				problemInstance.saveProblemInstance(
						this.getModelInfo().getModelInputFileWriter(),
						nextWindow.getLabelIndex());
			} else {
				// Call the learner to predict the label
				prediction = (int) Linear.predict(this.getModel(), problemInstance.getFeatureVector());
				//			System.out.println("Word: " + this.getData().getWordSet().getNum2label().get(this.getData().getSentence().getWordArray()[i]) +
				//			"\tPrediction: " + this.getData().getLabelSet().getNum2label().get(prediction));

				//	Here, I am assuming that sentence length equals # of windows
				// So store predicted label i to word i
				this.getData().getSentence().getLabelArray()[i]=prediction;
			}

			// Free space by resetting filled window to unfilled-window
			nextWindow.clean();
		}	
	}

	private void tagSentenceObject() throws IOException{
		// create window frames from sentence and store in list
		this.createWindowFramesFromSentence();

		// create feature vector instance for each window frame and tag
		this.constructProblemAndTag(false, true);

		// reset instances - need to do this here, because learner is called directly on windows
		this.getData().cleanInstances();
	}

	/**
	 * A method for tagging a single sentence given as list of tokens.
	 * @param tokens
	 * @throws IOException
	 */
	public void tagUnlabeledTokens(String[] tokens) throws IOException{
		this.time1 = System.currentTimeMillis();

		// create internal sentence object
		this.getData().generateSentenceObjectFromUnlabeledTokens(tokens);

		// tag sentence object
		this.tagSentenceObject();

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1)+"\n");
	}

	/**
	 * A simple print out of a sentence in form of list of word/tag
	 * @return
	 */
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

	/**
	 * For each internalized conll sentence:
	 * - compute window frames
	 * - fill windows using train=false mode
	 * - construct problem and call predictor
	 * - reset instance variable of data object, because predictor is called directly on windows of sentences
	 * @param conllReader
	 * @param max
	 * @throws IOException
	 */
	private void tagSentencesFromConllReader(BufferedReader conllReader, int max) throws IOException{
		String line = "";
		List<String[]> tokens = new ArrayList<String[]>();

		while ((line = conllReader.readLine()) != null) {
			if (line.isEmpty()) {
				// Stop if max sentences have been processed
				if  ((max > 0) && (data.getSentenceCnt() > max)) break;

				// create internal sentence object and label maps
				// I use this here although labels will be later overwritten 
				// - but can use it in eval modus as well
				data.generateSentenceObjectFromConllLabeledSentence(tokens);

				// tag sentence object
				this.tagSentenceObject();

				// reset tokens
				tokens = new ArrayList<String[]>();
			}
			else {
				String[] tokenizedLine = line.split("\t");
				tokens.add(tokenizedLine);
			}
		}
		conllReader.close();
	}


	private void tagAndWriteSentencesFromConllReader(BufferedReader conllReader, BufferedWriter conllWriter, int max) throws IOException{
		String line = "";
		List<String[]> tokens = new ArrayList<String[]>();

		while ((line = conllReader.readLine()) != null) {
			if (line.isEmpty()) {
				// For found sentence, do tagging:
				// Stop if max sentences have been processed
				if  ((max > 0) && (data.getSentenceCnt() > max)) break;

				// create internal sentence object and label maps
				// Use specified label from conll file for evaluation purposes leter
				data.generateSentenceObjectFromConllLabeledSentence(tokens);

				// tag sentence object
				this.tagSentenceObject();

				// Create conlleval consistent output using original conll tokens plus predicted labels 
				this.writeTokensAndWithLabels(conllWriter, tokens, data.getSentence());

				// reset tokens
				tokens = new ArrayList<String[]>();
			}
			else {
				// Collect all the words of a conll sentence
				String[] tokenizedLine = line.split("\t");
				tokens.add(tokenizedLine);
			}
		}
	}

	// NOTE:
	// I have adjusted the NER conll format to be consistent with the other conll formats, i.e.,
	// LONDON NNP I-NP I-LOC -> 1	LONDON	NNP	I-NP	I-LOC
	// This is why I have 5 elements instead of 4 
	private void writeTokensAndWithLabels(BufferedWriter conllWriter,
			List<String[]> tokens, Sentence sentence) throws IOException {
		for (int i=0; i < tokens.size(); i++){
			String[] token = tokens.get(i);
			String label = this.getData().getLabelSet().getNum2label().get(sentence.getLabelArray()[i]);
			String newConllToken=token[0]+" "
					+token[Data.wordFormIndex]+" "
					+token[Data.posTagIndex]+" "
					+label
					+"\n";

			conllWriter.write(newConllToken);
		}
		conllWriter.write("\n");
	}

	// TODO NOT USED
	private void tagFromConllDevelFile(String sourceFileName, int max)
			throws IOException {
		long time1;
		long time2;

		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),"UTF-8"));
		boolean train = false;
		boolean adjust = true;

		System.out.println("Do testing from file: " + sourceFileName);
		System.out.println("Train?: " + train + " Adjust?: " + adjust + "\n");
		System.out.println("Offsets: " + this.getOffSets().toString());
		System.out.println(this.getModel().toString());

		time1 = System.currentTimeMillis();
		this.tagSentencesFromConllReader(conllReader, max);
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println("Sentences: " + this.getData().getSentenceCnt());
		System.out.println("Testing instances: " + Window.windowCnt);
	}

	// This is the current main caller for the GNTagger
	public void tagAndWriteFromConllDevelFile(String sourceFileName, String evalFileName, int sentenceCnt)
			throws IOException {
		long time1;
		long time2;

		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),"UTF-8"));
		BufferedWriter conllWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(evalFileName),"UTF-8"));

		/*
		 * Set the writer buffer for the model input file based on the given sourceFileName
		 */
		if (ModelInfo.saveModelInputFile){
			String fileName = new File(sourceFileName).getName();
			this.getModelInfo().setModelInputFileWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(
											this.getModelInfo().getModelInputFilePrefix() + fileName+ ".txt"),"UTF-8")));
		} 
		System.out.println("\n++++\nDo testing from file: " + sourceFileName);
		// Reset some data to make sure each file has same change
		this.getData().setSentenceCnt(0);
		Window.windowCnt=0;

		time1 = System.currentTimeMillis();
		// -1 means: all sentences from file are processed
		this.tagAndWriteSentencesFromConllReader(conllReader,conllWriter, sentenceCnt);
		// close the buffers
		conllReader.close(); conllWriter.close();
		if (ModelInfo.saveModelInputFile)
			this.getModelInfo().getModelInputFileWriter().close();

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println("Sentences: " + this.getData().getSentenceCnt());
		System.out.println("Testing instances: " + Window.windowCnt);
		System.out.println("Sentences/sec: " + (this.getData().getSentenceCnt()*1000)/(time2-time1));
		System.out.println("Words/sec: " + (Window.windowCnt*1000)/(time2-time1));
	}
}
