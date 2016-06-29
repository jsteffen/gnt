package trainer;

import java.io.IOException;

import archive.Archivator;
import corpus.Corpus;
import corpus.CorpusProcessor;
import corpus.GNTcorpusProperties;
import corpus.IndicatorWordsCreator;
import data.Alphabet;
import data.GNTdataProperties;
import data.GlobalParams;
import data.ModelInfo;
import data.Window;
import features.WordClusterFeatureFactory;
import features.WordDistributedFeatureFactory;
import features.WordShapeFeatureFactory;
import features.WordSuffixFeatureFactory;

public class GNTrainer {

	private TrainerInMem trainer;
	private long time1 ;
	private long time2;
	private Corpus corpus = new Corpus();
	private Archivator archivator;

	// Setters and getters

	public Archivator getArchivator() {
		return archivator;
	}
	public void setArchivator(Archivator archivator) {
		this.archivator = archivator;
	}
	public TrainerInMem getTrainer() {
		return trainer;
	}
	public void setTrainer(TrainerInMem trainer) {
		this.trainer = trainer;
	}
	public Corpus getCorpus() {
		return corpus;
	}
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	// Creators

	public GNTrainer(ModelInfo modelInfo, GNTdataProperties dataProps, GNTcorpusProperties corpusProps){

		System.out.println(Alphabet.toActiveFeatureString());

		modelInfo.createModelFileName(GlobalParams.windowSize, GlobalParams.dim, GlobalParams.numberOfSentences);
		System.out.println(modelInfo.toString());

		this.corpus = new Corpus(corpusProps);

		CorpusProcessor mapper = new CorpusProcessor(this.corpus);

		try {
			mapper.processConllFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setArchivator(new Archivator(modelInfo.getModelFileArchive()));
		this.trainer = new TrainerInMem(this.getArchivator(), modelInfo, GlobalParams.windowSize);
	}

	public GNTrainer(ModelInfo modelInfo, int windowSize) {
		this.setArchivator(new Archivator(modelInfo.getModelFileArchive()));
		this.trainer = new TrainerInMem(this.getArchivator(), modelInfo, windowSize);
	}

	// Methods

	// This is a method for on-demand creation of the indicator words

	private void createIndicatorWords(String taggerName, double subSamplingThreshold){
		IndicatorWordsCreator iwp = new IndicatorWordsCreator();
		iwp.createAndWriteIndicatorTaggerNameWordsFromCorpus(
				this.getArchivator(), taggerName, this.getCorpus(), subSamplingThreshold);
	}

	// This is a method for on-demand creation of the feature files

	private void createWordVectors(String taggerName, int dim) throws IOException{
		if (dim > 0){
			WordDistributedFeatureFactory dwvFactory = new WordDistributedFeatureFactory();
			dwvFactory.createAndWriteDistributedWordFeaturesSparse(this.getArchivator(), taggerName, dim, this.getCorpus());	
		}
	}

	private void createShapeFeatures(String taggerName, String trainingFileName){
		WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
		wordShapeFactory.createAndSaveShapeFeature(this.getArchivator(), taggerName, trainingFileName);
	}

	private void createSuffixFeatures(String taggerName, String trainingFileName){
		WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
		wordSuffixFactory.createAndSaveSuffixFeature(this.getArchivator(), taggerName, trainingFileName);	
	}

	private void createClusterFeatures(String taggerName, String clusterIdSourceFileName){
		WordClusterFeatureFactory wordClusterFactory = new WordClusterFeatureFactory();
		wordClusterFactory.createAndSaveClusterIdFeature(this.getArchivator(), taggerName, clusterIdSourceFileName);	
	}

	// This is a method for on-demand creation of the feature files

	private void createTrainingFeatureFiles(String trainingFileName, String clusterIdSourceFileName, int dim)
			throws IOException{

		System.out.println("Create feature files from: " + trainingFileName + " and TaggerName: " + GlobalParams.taggerName);

		if (Alphabet.withWordFeats) this.createWordVectors(GlobalParams.taggerName, dim);
		if (Alphabet.withShapeFeats) this.createShapeFeatures(GlobalParams.taggerName, trainingFileName);
		if (Alphabet.withShapeFeats)this.createSuffixFeatures(GlobalParams.taggerName, trainingFileName);
		if (Alphabet.withClusterFeats) this.createClusterFeatures(GlobalParams.taggerName, clusterIdSourceFileName);

	}

	private void gntTrainingFromConllFile(String trainingFileName, int dim, int maxExamples) throws IOException{
		String taggerName = GlobalParams.taggerName;

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
		System.out.println("Set window count: ");
		Window.windowCnt = 0;

		this.getTrainer().trainFromConllTrainingFileInMemory(trainingFileName, maxExamples);

		time2 = System.currentTimeMillis();
		System.out.println("Total training time: " + (time2-time1));

		//		this.getTrainer().getProblem().n = OffSets.windowVectorSize;
		//		this.getTrainer().getProblem().l=Window.windowCnt;

		System.out.println("Offsets: " + this.getTrainer().getOffSets().toString());
		System.out.println("Sentences: " + this.getTrainer().getData().getSentenceCnt());
		System.out.println("Feature instances size: " + this.getTrainer().getProblem().n);
		System.out.println("Average window vector lenght: " + ProblemInstance.cumLength/Window.windowCnt);
		System.out.println("Training instances: " + this.getTrainer().getProblem().l);
		System.out.println("Approx. GB needed: " + ((ProblemInstance.cumLength/Window.windowCnt)*Window.windowCnt*8+Window.windowCnt)/1000000000.0);
	}

	public void gntTrainingWithDimensionFromConllFile(String trainingFileName, String clusterIdSourceFileName, int dim, int maxExamples) 
			throws IOException{
		time1 = System.currentTimeMillis();

		// add copied dataProps file to archive
		this.getArchivator().getFilesToPack().add(GNTdataProperties.configTmpFileName);

		// Create feature files
		this.createIndicatorWords(GlobalParams.taggerName, GlobalParams.subSamplingThreshold);
		this.createTrainingFeatureFiles(trainingFileName+"-sents.txt", clusterIdSourceFileName, dim);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		// Do training
		this.gntTrainingFromConllFile(trainingFileName+".conll", dim, maxExamples);
	}
}
