package de.dfki.mlt.gnt.trainer;

import java.io.IOException;

import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.corpus.Corpus;
import de.dfki.mlt.gnt.corpus.CorpusProcessor;
import de.dfki.mlt.gnt.corpus.GNTcorpusProperties;
import de.dfki.mlt.gnt.corpus.IndicatorWordsCreator;
import de.dfki.mlt.gnt.data.GNTdataProperties;
import de.dfki.mlt.gnt.data.Window;
import de.dfki.mlt.gnt.features.WordClusterFeatureFactory;
import de.dfki.mlt.gnt.features.WordDistributedFeatureFactory;
import de.dfki.mlt.gnt.features.WordShapeFeatureFactory;
import de.dfki.mlt.gnt.features.WordSuffixFeatureFactory;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class GNTrainer {

  private TrainerInMem trainer;
  private long time1;
  private long time2;
  private Corpus corpus = new Corpus();
  private Archivator archivator;
  private GNTdataProperties dataProps;


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

    // make sure corpus is available in CoNLL format and as plain text sentences
    CorpusProcessor.prepreCorpus(corpusProps);

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


  // This is a method for on-demand creation of the feature files
  private void createWordVectors(int dim) {

    if (dim > 0) {
      WordDistributedFeatureFactory dwvFactory = new WordDistributedFeatureFactory();
      dwvFactory.createAndWriteDistributedWordFeaturesSparse(dim, this.getCorpus());
    }
  }


  private void createShapeFeatures(String trainingFileName) {

    WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
    wordShapeFactory.createAndSaveShapeFeature(trainingFileName);
  }


  private void createSuffixFeatures(String trainingFileName) {

    WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
    wordSuffixFactory.createAndSaveSuffixFeature(trainingFileName);
  }


  private void createClusterFeatures(String clusterIdSourceFileName) {

    WordClusterFeatureFactory wordClusterFactory = new WordClusterFeatureFactory();
    wordClusterFactory.createAndSaveClusterIdFeature(clusterIdSourceFileName);
  }


  // This is a method for on-demand creation of the feature files
  private void createTrainingFeatureFiles(String trainingFileName, String clusterIdSourceFileName, int dim) {

    String taggerName = this.getDataProps().getGlobalParams().getTaggerName();
    System.out.println("Create feature files from: " + trainingFileName + " and TaggerName: " + taggerName);

    if (this.getDataProps().getAlphabet().isWithWordFeats()) {
      this.createWordVectors(dim);
    }
    if (this.getDataProps().getAlphabet().isWithShapeFeats()) {
      this.createShapeFeatures(trainingFileName);
    }
    if (this.getDataProps().getAlphabet().isWithSuffixFeats()) {
      this.createSuffixFeatures(trainingFileName);
    }
    if (this.getDataProps().getAlphabet().isWithClusterFeats()) {
      this.createClusterFeatures(clusterIdSourceFileName);
    }
  }


  private void gntTrainingFromConllFile(String trainingFileName, int dim, int maxExamples) throws IOException {

    String taggerName = this.getDataProps().getGlobalParams().getTaggerName();

    System.out.println("From  " + GlobalConfig.getModelBuildFolder());
    System.out.println("Load feature files for tagger " + taggerName + ":");
    this.time1 = System.currentTimeMillis();

    this.getTrainer().getAlphabet().loadFeaturesFromFiles(dim);

    System.out.println("Cleaning not used storage:");
    this.getTrainer().getAlphabet().clean();

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1));

    System.out.println("Create windows with size: " + this.getTrainer().getWindowSize());
    this.time1 = System.currentTimeMillis();
    System.out.println("Set window count: ");
    Window.setWindowCnt(0);

    this.getTrainer().trainFromConllTrainingFileInMemory(trainingFileName, maxExamples);

    this.time2 = System.currentTimeMillis();
    System.out.println("Total training time: " + (this.time2 - this.time1));

    //    this.getTrainer().getProblem().n = OffSets.windowVectorSize;
    //    this.getTrainer().getProblem().l=Window.windowCnt;

    System.out.println("Offsets: " + this.getTrainer().getOffSets().toString());
    System.out.println("Sentences: " + this.getTrainer().getData().getSentenceCnt());
    System.out.println("Feature instances size: " + this.getTrainer().getProblem().n);
    System.out.println("Average window vector lenght: " + ProblemInstance.getCumLength() / Window.getWindowCnt());
    System.out.println("Training instances: " + this.getTrainer().getProblem().l);
    System.out.println("Approx. GB needed: "
        + ((ProblemInstance.getCumLength() / Window.getWindowCnt()) * Window.getWindowCnt() * 8 + Window.getWindowCnt())
            / 1000000000.0);
  }


  // This is the main caller for training
  public void gntTrainingWithDimensionFromConllFile(String trainingFileName, String clusterIdSourceFileName, int dim,
      int maxExamples)
      throws IOException {

    this.time1 = System.currentTimeMillis();

    // Create feature files
    IndicatorWordsCreator iwp = new IndicatorWordsCreator();
    iwp.createIndicatorTaggerNameWords(
        this.getCorpus(), this.getDataProps().getGlobalParams().getSubSamplingThreshold());
    this.createTrainingFeatureFiles(trainingFileName + "-sents.txt", clusterIdSourceFileName, dim);

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1));

    // Do training
    this.gntTrainingFromConllFile(trainingFileName + ".conll", dim, maxExamples);
  }
}
