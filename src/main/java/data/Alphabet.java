package data;

import archive.Archivator;
import features.WordClusterFeatureFactory;
import features.WordDistributedFeatureFactory;
import features.WordShapeFeatureFactory;
import features.WordSuffixFeatureFactory;

/**
 * This class holds storage for the feature to liblinear indexing mappings.
 * It also defines which features are active/inactive
 * 
 * @author gune00
 *
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

  // Setters and getters
  public WordDistributedFeatureFactory getWordVectorFactory() {
    return wordVectorFactory;
  }
  public void setWordVectorFactory(WordDistributedFeatureFactory wordVectorFactory) {
    this.wordVectorFactory = wordVectorFactory;
  }
  public WordSuffixFeatureFactory getWordSuffixFactory() {
    return wordSuffixFactory;
  }
  public void setWordSuffixFactory(WordSuffixFeatureFactory wordSuffixFactory) {
    this.wordSuffixFactory = wordSuffixFactory;
  }
  public WordShapeFeatureFactory getWordShapeFactory() {
    return wordShapeFactory;
  }
  public void setWordShapeFactory(WordShapeFeatureFactory wordShapeFactory) {
    this.wordShapeFactory = wordShapeFactory;
  }
  public WordClusterFeatureFactory getWordClusterFactory() {
    return wordClusterFactory;
  }
  public void setWordClusterFactory(WordClusterFeatureFactory wordClusterFactory) {
    this.wordClusterFactory = wordClusterFactory;
  }
  public boolean isWithWordFeats() {
    return withWordFeats;
  }
  public void setWithWordFeats(boolean withWordFeats) {
    this.withWordFeats = withWordFeats;
  }
  public boolean isWithShapeFeats() {
    return withShapeFeats;
  }
  public void setWithShapeFeats(boolean withShapeFeats) {
    this.withShapeFeats = withShapeFeats;
  }
  public boolean isWithSuffixFeats() {
    return withSuffixFeats;
  }
  public void setWithSuffixFeats(boolean withSuffixFeats) {
    this.withSuffixFeats = withSuffixFeats;
  }
  public boolean isWithClusterFeats() {
    return withClusterFeats;
  }
  public void setWithClusterFeats(boolean withClusterFeats) {
    this.withClusterFeats = withClusterFeats;
  }
  public boolean isWithLabelFeats() {
    return withLabelFeats;
  }
  public void setWithLabelFeats(boolean withLabelFeats) {
    this.withLabelFeats = withLabelFeats;
  }
  
  
  // Methods
  
  /**
   * Load the precomputed features from file during training phase.
   * @param taggerName
   * @param dim
   */
  public void loadFeaturesFromFiles(String taggerName, int dim, String featureFilePath){
    if (this.isWithWordFeats()) this.wordVectorFactory.readDistributedWordFeaturesSparse(taggerName, dim, featureFilePath);
    if (this.isWithSuffixFeats()) this.wordSuffixFactory.readSuffixList(taggerName, featureFilePath);
    if (this.isWithShapeFeats()) this.wordShapeFactory.readShapeList(taggerName, featureFilePath);
    if (this.isWithClusterFeats()) this.wordClusterFactory.readClusterIdList(taggerName, featureFilePath);
  }
  
  /**
   * Load the compressed features files from archive during tagging phase
   * @param archivator
   * @param taggerName
   * @param dim
   */
  public void loadFeaturesFromFiles(Archivator archivator, String taggerName, int dim, String featureFilePath){
    if (this.isWithWordFeats()) this.wordVectorFactory.readDistributedWordFeaturesSparse(archivator, taggerName, dim, featureFilePath);
    if (this.isWithSuffixFeats()) this.wordSuffixFactory.readSuffixList(archivator, taggerName, featureFilePath);
    if (this.isWithShapeFeats()) this.wordShapeFactory.readShapeList(archivator, taggerName, featureFilePath);
    if (this.isWithClusterFeats()) this.wordClusterFactory.readClusterIdList(archivator, taggerName, featureFilePath);
  }

  public void clean(){
    System.out.println("Cleaning word vectors ... ");
    this.wordVectorFactory.clean();
    
    System.out.println("Cleaning suffix list ... ");
    this.wordSuffixFactory.clean();
    
    System.out.println("Cleaning shape list ... ");
    this.wordShapeFactory.clean();
    
    System.out.println("Cleaning cluster ID list ... ");
    this.wordClusterFactory.clean();
  }
  
  public String toActiveFeatureString(){
    String output = "\nActive features\n";
    output += "withWordFeats=   " + this.isWithWordFeats() +"\n";
    output += "withSuffixFeats=  " + this.isWithSuffixFeats() +"\n";
    output += "withShapeFeats= " + this.isWithShapeFeats() +"\n";
    output += "withClusterFeats= " + this.isWithClusterFeats() +"\n";
    output += "withLabelFeats= " + this.isWithLabelFeats() +"\n";
    return output;  
  }

}
