package de.dfki.mlt.gnt.trainer;

import java.io.IOException;

import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.CorpusConfig;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.config.ModelConfig;
import de.dfki.mlt.gnt.corpus.CorpusProcessor;
import de.dfki.mlt.gnt.corpus.IndicatorWordsCreator;
import de.dfki.mlt.gnt.data.Alphabet;
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
  private ModelConfig modelConfig;
  private CorpusConfig corpusConfig;
  private Alphabet alphabet;

  private Archivator archivator;


  public GNTrainer(ModelConfig modelConfig, CorpusConfig corpusConfig) {

    this.modelConfig = modelConfig;
    this.corpusConfig = corpusConfig;

    // init alphabet
    this.alphabet = new Alphabet(modelConfig);
    System.out.println(this.alphabet.toActiveFeatureString());

    // make sure corpus is available in CoNLL format and as plain text sentences
    CorpusProcessor.prepreCorpus(corpusConfig);

    // set the ZIP archivator
    this.archivator = new Archivator(modelConfig.getModelName() + ".zip");
    // Initialize and set the internal training algorithm
    this.setTrainer(new TrainerInMem(this.archivator, this.alphabet, this.modelConfig));
  }


  public Archivator getArchivator() {

    return this.archivator;
  }


  public void setArchivator(Archivator archivator) {

    this.archivator = archivator;
  }


  public TrainerInMem getTrainer() {

    return this.trainer;
  }


  public void setTrainer(TrainerInMem trainer) {

    this.trainer = trainer;
  }


  // This is a method for on-demand creation of the feature files
  private void createWordVectors(int dim) {

    if (dim > 0) {
      WordDistributedFeatureFactory dwvFactory = new WordDistributedFeatureFactory();
      dwvFactory.createAndWriteDistributedWordFeaturesSparse(dim, this.corpusConfig);
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

    String taggerName = this.modelConfig.getString(ConfigKeys.TAGGER_NAME);
    System.out.println("Create feature files from: " + trainingFileName + " and TaggerName: " + taggerName);

    if (this.alphabet.isWithWordFeats()) {
      this.createWordVectors(dim);
    }
    if (this.alphabet.isWithShapeFeats()) {
      this.createShapeFeatures(trainingFileName);
    }
    if (this.alphabet.isWithSuffixFeats()) {
      this.createSuffixFeatures(trainingFileName);
    }
    if (this.alphabet.isWithClusterFeats()) {
      this.createClusterFeatures(clusterIdSourceFileName);
    }
  }


  private void gntTrainingFromConllFile(String trainingFileName, int dim, int maxExamples) throws IOException {

    String taggerName = this.modelConfig.getString(ConfigKeys.TAGGER_NAME);

    System.out.println("From  " + GlobalConfig.getModelBuildFolder());
    System.out.println("Load feature files for tagger " + taggerName + ":");
    this.time1 = System.currentTimeMillis();

    this.getTrainer().getAlphabet().loadFeaturesFromFiles(dim);

    System.out.println("Cleaning not used storage:");
    this.getTrainer().getAlphabet().clean();

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1));

    System.out.println("Create windows with size: " + this.modelConfig.getInt(ConfigKeys.WINDOW_SIZE));
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
  public void gntTrainingWithDimensionFromConllFile()
      throws IOException {

    String trainingFileName = this.corpusConfig.getString(ConfigKeys.TRAINING_FILE).split("\\.conll")[0];
    String clusterIdSourceFileName = this.corpusConfig.getString(ConfigKeys.CLUSTER_FILE);
    int dim = this.modelConfig.getInt(ConfigKeys.DIM);
    int maxExamples = this.modelConfig.getInt(ConfigKeys.NUMBER_OF_SENTENCES);

    this.time1 = System.currentTimeMillis();

    // Create feature files
    IndicatorWordsCreator iwp = new IndicatorWordsCreator();
    iwp.createIndicatorTaggerNameWords(
        this.corpusConfig, this.modelConfig.getDouble(ConfigKeys.SUB_SAMPLING_THRESHOLD));
    this.createTrainingFeatureFiles(trainingFileName + "-sents.txt", clusterIdSourceFileName, dim);

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1));

    // Do training
    this.gntTrainingFromConllFile(trainingFileName + ".conll", dim, maxExamples);
  }
}
