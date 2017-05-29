package de.dfki.mlt.gnt.data;

import de.dfki.mlt.gnt.archive.Archivator;
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

  private WordDistributedFeatureFactory wordVectorFactory = new WordDistributedFeatureFactory();
  private WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
  private WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
  private WordClusterFeatureFactory wordClusterFactory = new WordClusterFeatureFactory();

  private boolean withWordFeats = true;
  private boolean withShapeFeats = true;
  private boolean withSuffixFeats = true;
  private boolean withClusterFeats = false;
  private boolean withLabelFeats = false;


  public WordDistributedFeatureFactory getWordVectorFactory() {

    return this.wordVectorFactory;
  }


  public void setWordVectorFactory(WordDistributedFeatureFactory wordVectorFactory) {

    this.wordVectorFactory = wordVectorFactory;
  }


  public WordSuffixFeatureFactory getWordSuffixFactory() {

    return this.wordSuffixFactory;
  }


  public void setWordSuffixFactory(WordSuffixFeatureFactory wordSuffixFactory) {

    this.wordSuffixFactory = wordSuffixFactory;
  }


  public WordShapeFeatureFactory getWordShapeFactory() {

    return this.wordShapeFactory;
  }


  public void setWordShapeFactory(WordShapeFeatureFactory wordShapeFactory) {

    this.wordShapeFactory = wordShapeFactory;
  }


  public WordClusterFeatureFactory getWordClusterFactory() {

    return this.wordClusterFactory;
  }


  public void setWordClusterFactory(WordClusterFeatureFactory wordClusterFactory) {

    this.wordClusterFactory = wordClusterFactory;
  }


  public boolean isWithWordFeats() {

    return this.withWordFeats;
  }


  public void setWithWordFeats(boolean withWordFeats) {

    this.withWordFeats = withWordFeats;
  }


  public boolean isWithShapeFeats() {

    return this.withShapeFeats;
  }


  public void setWithShapeFeats(boolean withShapeFeats) {

    this.withShapeFeats = withShapeFeats;
  }


  public boolean isWithSuffixFeats() {

    return this.withSuffixFeats;
  }


  public void setWithSuffixFeats(boolean withSuffixFeats) {

    this.withSuffixFeats = withSuffixFeats;
  }


  public boolean isWithClusterFeats() {

    return this.withClusterFeats;
  }


  public void setWithClusterFeats(boolean withClusterFeats) {

    this.withClusterFeats = withClusterFeats;
  }


  public boolean isWithLabelFeats() {

    return this.withLabelFeats;
  }


  public void setWithLabelFeats(boolean withLabelFeats) {

    this.withLabelFeats = withLabelFeats;
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
