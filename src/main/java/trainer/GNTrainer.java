package trainer;

import java.io.IOException;

import archive.Archivator;
import corpus.Corpus;
import corpus.CorpusProcessor;
import corpus.GNTcorpusProperties;
import corpus.IndicatorWordsCreator;
import data.GNTdataProperties;
import data.Window;
import features.WordClusterFeatureFactory;
import features.WordDistributedFeatureFactory;
import features.WordShapeFeatureFactory;
import features.WordSuffixFeatureFactory;

public class GNTrainer {

  private TrainerInMem trainer;
  private long time1;
  private long time2;
  private Corpus corpus = new Corpus();
  private Archivator archivator;
  private GNTdataProperties dataProps;


  // Creators

  public GNTrainer(GNTdataProperties dataProps, GNTcorpusProperties corpusProps) {

    // Get general parameters and create model file name
    this.setDataProps(dataProps);
    System.out.println(this.getDataProps().getAlphabet().toActiveFeatureString());

    this.getDataProps().getModelInfo().createModelFileName(this.getDataProps().getGlobalParams().getWindowSize(),
        this.getDataProps().getGlobalParams().getDim(),
        this.getDataProps().getGlobalParams().getNumberOfSentences(),
        this.getDataProps().getAlphabet(),
        this.getDataProps().getGlobalParams());
    System.out.println(this.getDataProps().getModelInfo().toString());

    // Set the corpus files for performing training and testing
    this.corpus = new Corpus(corpusProps, this.getDataProps().getGlobalParams());
    CorpusProcessor mapper = new CorpusProcessor(this.corpus, this.dataProps);

    try {
      // Make sure that they are all in correct CONLL format
      mapper.processConllFiles();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // set the ZIP archivator
    this.setArchivator(
        new Archivator(this.getDataProps().getModelInfo().getModelFileArchive()));
    // Initialize and set the internal training algorithm
    this.setTrainer(
        new TrainerInMem(this.getArchivator(), this.getDataProps().getModelInfo(),
            this.getDataProps().getAlphabet(),
            this.getDataProps().getGlobalParams(),
            this.getDataProps().getGlobalParams().getWindowSize()));
  }


  // Setters and getters


  public Archivator getArchivator() {

    return this.archivator;
  }


  public void setArchivator(Archivator archivator) {

    this.archivator = archivator;
  }


  public GNTdataProperties getDataProps() {

    return this.dataProps;
  }


  public void setDataProps(GNTdataProperties dataProps) {

    this.dataProps = dataProps;
  }


  public TrainerInMem getTrainer() {

    return this.trainer;
  }


  public void setTrainer(TrainerInMem trainer) {

    this.trainer = trainer;
  }


  public Corpus getCorpus() {

    return this.corpus;
  }


  public void setCorpus(Corpus corpus) {

    this.corpus = corpus;
  }


  // Methods


  // This is a method for on-demand creation of the indicator words

  private void createIndicatorWords(String taggerName, double subSamplingThreshold) {

    IndicatorWordsCreator iwp =
        new IndicatorWordsCreator(this.getDataProps().getGlobalParams().getFeatureFilePathname());
    iwp.createAndWriteIndicatorTaggerNameWordsFromCorpus(
        this.getArchivator(), taggerName, this.getCorpus(), subSamplingThreshold);
  }


  // This is a method for on-demand creation of the feature files

  private void createWordVectors(String taggerName, int dim) throws IOException {

    if (dim > 0) {
      WordDistributedFeatureFactory dwvFactory =
          new WordDistributedFeatureFactory(this.getDataProps().getGlobalParams().getFeatureFilePathname());
      dwvFactory.createAndWriteDistributedWordFeaturesSparse(this.getArchivator(), taggerName, dim, this.getCorpus());
    }
  }


  private void createShapeFeatures(String taggerName, String trainingFileName) {

    WordShapeFeatureFactory wordShapeFactory =
        new WordShapeFeatureFactory(this.getDataProps().getGlobalParams().getFeatureFilePathname());
    System.out.println("Feature file pathname: " + this.getDataProps().getGlobalParams().getFeatureFilePathname());
    wordShapeFactory.createAndSaveShapeFeature(this.getArchivator(), taggerName, trainingFileName);
  }


  private void createSuffixFeatures(String taggerName, String trainingFileName) {

    WordSuffixFeatureFactory wordSuffixFactory =
        new WordSuffixFeatureFactory(this.getDataProps().getGlobalParams().getFeatureFilePathname());
    wordSuffixFactory.createAndSaveSuffixFeature(this.getArchivator(), taggerName, trainingFileName);
  }


  private void createClusterFeatures(String taggerName, String clusterIdSourceFileName) {

    WordClusterFeatureFactory wordClusterFactory =
        new WordClusterFeatureFactory(this.getDataProps().getGlobalParams().getFeatureFilePathname());
    wordClusterFactory.createAndSaveClusterIdFeature(this.getArchivator(), taggerName, clusterIdSourceFileName);
  }


  // This is a method for on-demand creation of the feature files

  private void createTrainingFeatureFiles(String trainingFileName, String clusterIdSourceFileName, int dim)
      throws IOException {

    String taggerName = this.getDataProps().getGlobalParams().getTaggerName();
    System.out.println("Create feature files from: " + trainingFileName + " and TaggerName: " + taggerName);

    if (this.getDataProps().getAlphabet().isWithWordFeats()) {
      this.createWordVectors(taggerName, dim);
    }
    if (this.getDataProps().getAlphabet().isWithShapeFeats()) {
      this.createShapeFeatures(taggerName, trainingFileName);
    }
    if (this.getDataProps().getAlphabet().isWithSuffixFeats()) {
      this.createSuffixFeatures(taggerName, trainingFileName);
    }
    if (this.getDataProps().getAlphabet().isWithClusterFeats()) {
      this.createClusterFeatures(taggerName, clusterIdSourceFileName);
    }

  }


  private void gntTrainingFromConllFile(String trainingFileName, int dim, int maxExamples) throws IOException {

    String featureFilePath = this.getDataProps().getGlobalParams().getFeatureFilePathname();
    String taggerName = this.getDataProps().getGlobalParams().getTaggerName();

    System.out.println("From  " + featureFilePath);
    System.out.println("Load feature files for tagger " + taggerName + ":");
    this.time1 = System.currentTimeMillis();

    this.getTrainer().getAlphabet().loadFeaturesFromFiles(taggerName, dim, featureFilePath);

    System.out.println("Cleaning not used storage:");
    this.getTrainer().getAlphabet().clean();

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1));

    System.out.println("Create windows with size: " + this.getTrainer().getWindowSize());
    this.time1 = System.currentTimeMillis();
    System.out.println("Set window count: ");
    Window.windowCnt = 0;

    this.getTrainer().trainFromConllTrainingFileInMemory(trainingFileName, maxExamples);

    this.time2 = System.currentTimeMillis();
    System.out.println("Total training time: " + (this.time2 - this.time1));

    //    this.getTrainer().getProblem().n = OffSets.windowVectorSize;
    //    this.getTrainer().getProblem().l=Window.windowCnt;

    System.out.println("Offsets: " + this.getTrainer().getOffSets().toString());
    System.out.println("Sentences: " + this.getTrainer().getData().getSentenceCnt());
    System.out.println("Feature instances size: " + this.getTrainer().getProblem().n);
    System.out.println("Average window vector lenght: " + ProblemInstance.cumLength / Window.windowCnt);
    System.out.println("Training instances: " + this.getTrainer().getProblem().l);
    System.out.println("Approx. GB needed: "
        + ((ProblemInstance.cumLength / Window.windowCnt) * Window.windowCnt * 8 + Window.windowCnt) / 1000000000.0);
  }


  // This is the main caller for training
  public void gntTrainingWithDimensionFromConllFile(String trainingFileName, String clusterIdSourceFileName, int dim,
      int maxExamples)
      throws IOException {

    this.time1 = System.currentTimeMillis();

    // add copied dataProps file to archive
    this.getArchivator().getFilesToPack().add(GNTdataProperties.configTmpFileName);

    // Create feature files
    this.createIndicatorWords(this.getDataProps().getGlobalParams().getTaggerName(),
        this.getDataProps().getGlobalParams().getSubSamplingThreshold());
    this.createTrainingFeatureFiles(trainingFileName + "-sents.txt", clusterIdSourceFileName, dim);

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1));

    // Do training
    this.gntTrainingFromConllFile(trainingFileName + ".conll", dim, maxExamples);
  }
}
