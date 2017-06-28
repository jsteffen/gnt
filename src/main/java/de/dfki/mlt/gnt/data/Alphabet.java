package de.dfki.mlt.gnt.data;

import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.ModelConfig;
import de.dfki.mlt.gnt.features.WordClusterFeatureFactory;
import de.dfki.mlt.gnt.features.WordDistributedFeatureFactory;
import de.dfki.mlt.gnt.features.WordShapeFeatureFactory;
import de.dfki.mlt.gnt.features.WordSuffixFeatureFactory;

/**
 * This class holds storage for the feature to liblinear indexing mappings.
 * It also defines which features are active/inactive
 *
 * @author Günter Neumann, DFKI
 */
public class Alphabet {

  private WordDistributedFeatureFactory wordVectorFactory;
  private WordSuffixFeatureFactory wordSuffixFactory;
  private WordShapeFeatureFactory wordShapeFactory;
  private WordClusterFeatureFactory wordClusterFactory;

  private boolean withWordFeats = true;
  private boolean withShapeFeats = true;
  private boolean withSuffixFeats = true;
  private boolean withClusterFeats = false;
  private boolean withLabelFeats = false;


  public Alphabet(ModelConfig modelConfig) {

    this.wordVectorFactory = new WordDistributedFeatureFactory();
    this.wordSuffixFactory = new WordSuffixFeatureFactory();
    this.wordShapeFactory = new WordShapeFeatureFactory();
    this.wordClusterFactory = new WordClusterFeatureFactory();

    this.withWordFeats = modelConfig.getBoolean(ConfigKeys.WITH_WORD_FEATS);
    this.withShapeFeats = modelConfig.getBoolean(ConfigKeys.WITH_SHAPE_FEATS);
    this.withSuffixFeats = modelConfig.getBoolean(ConfigKeys.WITH_SUFFIX_FEATS);
    this.withClusterFeats = modelConfig.getBoolean(ConfigKeys.WITH_CLUSTER_FEATS);
    this.withLabelFeats = modelConfig.getBoolean(ConfigKeys.WITH_LABEL_FEATS);
  }


  public WordDistributedFeatureFactory getWordVectorFactory() {

    return this.wordVectorFactory;
  }


  public WordSuffixFeatureFactory getWordSuffixFactory() {

    return this.wordSuffixFactory;
  }


  public WordShapeFeatureFactory getWordShapeFactory() {

    return this.wordShapeFactory;
  }


  public WordClusterFeatureFactory getWordClusterFactory() {

    return this.wordClusterFactory;
  }


  public boolean isWithWordFeats() {

    return this.withWordFeats;
  }


  public boolean isWithShapeFeats() {

    return this.withShapeFeats;
  }


  public boolean isWithSuffixFeats() {

    return this.withSuffixFeats;
  }


  public boolean isWithClusterFeats() {

    return this.withClusterFeats;
  }


  public boolean isWithLabelFeats() {

    return this.withLabelFeats;
  }


  /**
   * Load the precomputed features from file during training phase.
   * @param taggerName
   * @param dim
   */
  public void loadFeaturesFromFiles(int dim) {

    if (this.isWithWordFeats()) {
      this.wordVectorFactory.readDistributedWordFeaturesSparse(dim);
    }
    if (this.isWithSuffixFeats()) {
      this.wordSuffixFactory.readSuffixList();
    }
    if (this.isWithShapeFeats()) {
      this.wordShapeFactory.readShapeList();
    }
    if (this.isWithClusterFeats()) {
      this.wordClusterFactory.readClusterIdList();
    }
  }


  /**
   * Load the compressed features files from archive during tagging phase
   * @param archivator
   * @param dim
   */
  public void loadFeaturesFromFiles(Archivator archivator, int dim) {

    if (this.isWithWordFeats()) {
      this.wordVectorFactory.readDistributedWordFeaturesSparse(archivator, dim);
    }
    if (this.isWithSuffixFeats()) {
      this.wordSuffixFactory.readSuffixList(archivator);
    }
    if (this.isWithShapeFeats()) {
      this.wordShapeFactory.readShapeList(archivator);
    }
    if (this.isWithClusterFeats()) {
      this.wordClusterFactory.readClusterIdList(archivator);
    }
  }


  public void clean() {

    System.out.println("Cleaning word vectors ... ");
    this.wordVectorFactory.clean();

    System.out.println("Cleaning suffix list ... ");
    this.wordSuffixFactory.clean();

    System.out.println("Cleaning shape list ... ");
    this.wordShapeFactory.clean();

    System.out.println("Cleaning cluster ID list ... ");
    this.wordClusterFactory.clean();
  }


  public String toActiveFeatureString() {

    String output = "\nActive features\n";
    output += "withWordFeats=   " + this.isWithWordFeats() + "\n";
    output += "withSuffixFeats=  " + this.isWithSuffixFeats() + "\n";
    output += "withShapeFeats= " + this.isWithShapeFeats() + "\n";
    output += "withClusterFeats= " + this.isWithClusterFeats() + "\n";
    output += "withLabelFeats= " + this.isWithLabelFeats() + "\n";
    return output;
  }
}
