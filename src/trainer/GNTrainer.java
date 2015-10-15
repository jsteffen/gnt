package trainer;

import java.io.IOException;

import data.ModelInfo;
import data.OffSets;
import data.Window;
import features.WordDistributedFeatureFactory;
import features.WordShapeFeatureFactory;
import features.WordSuffixFeatureFactory;

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

	// initialization

	public GNTrainer(ModelInfo modelInfo, int dim) {
		this.trainer = new TrainerInMem(modelInfo, dim);
	}

	// This is a method for on-demand creation of the feature files

	private void createTrainingFeatureFiles(String trainingFileName, int dim)
			throws IOException{
		String taggerName = this.getTrainer().getModelInfo().getTaggerName();

		System.out.println("Create feature files from: " + trainingFileName + " and TaggerName: " + taggerName);

		this.createWordVectors(taggerName, dim);
		this.createShapeFeatures(taggerName, trainingFileName);
		this.createSuffixFeatures(taggerName, trainingFileName);	
	}

	private void createWordVectors(String taggerName, int dim) throws IOException{
		if (dim > 0){
			WordDistributedFeatureFactory dwvFactory = new WordDistributedFeatureFactory();
			dwvFactory.createAndWriteDistributedWordFeaturesSparse(taggerName, dim);	
		}
	}

	private void createShapeFeatures(String taggerName, String trainingFileName){
		WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
		wordShapeFactory.createAndSaveShapeFeature(taggerName, trainingFileName);
	}
	
	private void createSuffixFeatures(String taggerName, String trainingFileName){
		WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
		wordSuffixFactory.createAndSaveSuffixFeature(taggerName, trainingFileName);	
	}



	private void gntTrainingFromConllFile(String trainingFileName, int dim, int maxExamples) throws IOException{
		String taggerName = this.getTrainer().getModelInfo().getTaggerName();
		
		System.out.println("Load feature files for tagger " + taggerName + ":");
		time1 = System.currentTimeMillis();

		this.getTrainer().getAlphabet().loadFeaturesFromFiles(taggerName,dim);

		System.out.println("Cleaning not used storage:");
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
		time1 = System.currentTimeMillis();

		this.createTrainingFeatureFiles(trainingFileName+"-sents.txt", dim);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		this.gntTrainingFromConllFile(trainingFileName+".conll", dim, maxExamples);
	}
}
