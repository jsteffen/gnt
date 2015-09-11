package test;

import java.io.IOException;

import data.OffSets;
import data.Window;
import features.WordDistributedFeatureFactory;
import trainer.ProblemInstance;
import trainer.Trainer;
import trainer.TrainerInMem;

public class TestTrainer {

	private TrainerInMem trainer;
	private long time1 ;
	private long time2;

	public TrainerInMem getTrainer() {
		return trainer;
	}
	public void setTrainer(TrainerInMem trainer) {
		this.trainer = trainer;
	}

	public TestTrainer(int dim) {
		this.trainer = new TrainerInMem(dim);
	}

	// test methods

	private void createWordVectors(int dim) throws IOException{
		WordDistributedFeatureFactory dwvFactory = new WordDistributedFeatureFactory();

		dwvFactory.createAndWriteDistributedWordFeaturesSparse(dim);
	}

	private void testPerformanceTraining(int maxExamples, boolean saveVectors) throws IOException{
		System.out.println("Load feature files:");
		time1 = System.currentTimeMillis();

		this.getTrainer().getAlphabet().loadFeaturesFromFiles();
		
		System.out.println("Resetting not used storage:");
		this.getTrainer().getAlphabet().clean();

		this.getTrainer().getOffSets().initializeOffsets(this.getTrainer().getAlphabet(), this.getTrainer().getWindowSize());
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println("Create windows:");
		time1 = System.currentTimeMillis();
		
		this.getTrainer().trainFromConllTrainingFileInMemory("/Users/gune00/data/MLDP/english/english-train.conll", maxExamples);

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

	private void testPerformanceTrainingWithDimension(int dim, int maxExamples, boolean saveVectors) throws IOException{
		System.out.println("Create wordVectors:");
		time1 = System.currentTimeMillis();

		this.createWordVectors(dim);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		this.testPerformanceTraining(maxExamples, saveVectors);
	}




	public static void main(String[] args) throws IOException{
		int windowSize = 2;
		TestTrainer testTrainer = new TestTrainer(windowSize);

		
		testTrainer.testPerformanceTraining(5000, false);

	}

}
