package test;

import java.io.IOException;

import data.ModelInfo;
import data.OffSets;
import data.Window;
import features.WordDistributedFeatureFactory;
import features.WordFeatures;
import trainer.ProblemInstance;
import trainer.TrainerInMem;

public class GNTrainer {

	private TrainerInMem trainer;
	private long time1 ;
	private long time2;
	
	//
	public TrainerInMem getTrainer() {
		return trainer;
	}
	public void setTrainer(TrainerInMem trainer) {
		this.trainer = trainer;
	}

	public GNTrainer(int dim) {
		this.trainer = new TrainerInMem(dim);
	}

	public GNTrainer(ModelInfo modelInfo, int dim) {
		this.trainer = new TrainerInMem(modelInfo, dim);
	}

	// This is a method for on-demand creation of the distributed word vectors given the dimension dim.

	private void createWordVectors(int dim) throws IOException{
		WordDistributedFeatureFactory dwvFactory = new WordDistributedFeatureFactory();
		dwvFactory.createAndWriteDistributedWordFeaturesSparse(dim);
	}

	private void gntTrainingFromConllFile(String trainingFileName, int dim, int maxExamples) throws IOException{
		System.out.println("Load feature files:");
		time1 = System.currentTimeMillis();

		this.getTrainer().getAlphabet().loadFeaturesFromFiles(dim);

		System.out.println("Resetting not used storage:");
		this.getTrainer().getAlphabet().clean();

		this.getTrainer().getOffSets().initializeOffsets(this.getTrainer().getAlphabet(), this.getTrainer().getWindowSize());
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println("Create windows with size: " + this.getTrainer().getWindowSize());
		time1 = System.currentTimeMillis();

		this.getTrainer().trainFromConllTrainingFileInMemory(trainingFileName, maxExamples);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		this.getTrainer().getProblem().n = OffSets.windowVectorSize;
		this.getTrainer().getProblem().l=Window.windowCnt;

		System.out.println("Offsets: " + this.getTrainer().getOffSets().toString());
		System.out.println("Sentences: " + this.getTrainer().getData().getSentenceCnt());
		System.out.println("Feature instances size: " + this.getTrainer().getProblem().n);
		System.out.println("Average window vector lenght: " + ProblemInstance.cumLength/Window.windowCnt);
		System.out.println("Training instances: " + this.getTrainer().getProblem().l);
		System.out.println("Approx. GB needed: " + ((ProblemInstance.cumLength/Window.windowCnt)*Window.windowCnt*8+Window.windowCnt)/1000000000.0);
	}

	public void gntTrainingWithDimensionFromConllFile(String trainingFileName, int dim, int maxExamples) throws IOException{
		System.out.println("Create wordVectors:");
		time1 = System.currentTimeMillis();

		if (dim > 0) this.createWordVectors(dim);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		this.gntTrainingFromConllFile(trainingFileName, dim, maxExamples);
	}

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		int windowSize = 2;
		int numberOfSentences = 38215;
		int dim = 0;
		
		WordFeatures.withWordFeats=false;
		
		modelInfo.createModelFileName(dim, numberOfSentences);
		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize);
		String trainingFileName = "resources/data/english/ptb3-std-training.conll";

		gnTrainer.gntTrainingWithDimensionFromConllFile(trainingFileName, dim, numberOfSentences);

	}

}
