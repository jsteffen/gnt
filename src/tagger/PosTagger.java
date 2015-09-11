package tagger;

import java.io.File;
import java.io.IOException;

import data.Alphabet;
import data.Data;
import data.OffSets;
import de.bwaldvogel.liblinear.Model;

public class PosTagger {
	private Data data = new Data();
	private Alphabet alphabet = new Alphabet();
	private OffSets offSets = new OffSets();
	private int windowSize = 2;

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

	// Init
	public PosTagger(){
	}

	// Methods

	public void initPosTagger(String modelFile, int windowSize) throws IOException{
		System.out.println("Set window size: " + windowSize);
		this.setWindowSize(windowSize);
		System.out.println("Load feature files:");
		time1 = System.currentTimeMillis();
		
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
		
		System.out.println("Load model file: " + modelFile);
		time1 = System.currentTimeMillis();
		Model.load(new File(modelFile));
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
	}

	public static void main(String[] args) throws IOException{
		int windowSize = 2;
		String modelFile1 = "/Users/gune00/data/wordVectorTests/testModel_L2R_LR.txt";
		String modelFile2 = "/Users/gune00/data/wordVectorTests/testModel_MCSVM_CS.txt";

		PosTagger posTagger = new PosTagger();

		posTagger.initPosTagger(modelFile2, windowSize);

	}

}
