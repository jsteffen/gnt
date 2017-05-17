package de.dfki.mlt.gnt.features;

import java.util.ArrayList;
import java.util.List;

import de.dfki.mlt.gnt.data.Alphabet;
import de.dfki.mlt.gnt.data.OffSets;
import de.dfki.mlt.gnt.data.Pair;

/**
 * A word features consists  of its components:
 * <li> left distributed word features
 * <li> right distributed word features
 * <li> shape features
 * <li> suffix features
 * <li> cluster features
 *
 * They are represented by a list of pairs using relative index and value.
 * Where value can be double or boolean
 *
 * @author Günter Neumann, DFKI
 */
public class WordFeatures {

  private String word = "";
  private String leftWord = "";
  private String rightWord = "";
  private int index = 0;
  private int elementOffset = 0;
  private int leftOffset = 0;
  private int rightOffset = 0;
  private int shapeOffset = 0;
  private int suffixOffset = 0;
  private int clusterIdOffset = 0;
  private int labelOffset = 0;
  private boolean adjust = false;
  private int length = 0;


  private int labelIndex = -1;
  private OffSets offSets;


  private List<Pair<Integer, Double>> left = new ArrayList<Pair<Integer, Double>>();
  private List<Pair<Integer, Double>> right = new ArrayList<Pair<Integer, Double>>();
  private List<Pair<Integer, Boolean>> suffix = new ArrayList<Pair<Integer, Boolean>>();
  // I am using here lists although we only have always one element
  private List<Pair<Integer, Boolean>> shape = new ArrayList<Pair<Integer, Boolean>>();
  private List<Pair<Integer, Boolean>> cluster = new ArrayList<Pair<Integer, Boolean>>();
  private List<Pair<Integer, Boolean>> label = new ArrayList<Pair<Integer, Boolean>>();


  public WordFeatures(String word) {
    this.word = word;
  }


  public WordFeatures(String word2, String l, String r) {
    this.word = word2;
    this.leftWord = l;
    this.rightWord = r;
  }


  public List<Pair<Integer, Double>> getLeft() {

    return this.left;
  }


  public void setLeft(List<Pair<Integer, Double>> left) {

    this.left = left;
  }


  public OffSets getOffSets() {

    return this.offSets;
  }


  public void setOffSets(OffSets offSets) {

    this.offSets = offSets;
  }


  public int getLabelIndex() {

    return this.labelIndex;
  }


  public void setLabelIndex(int labelIndex) {

    this.labelIndex = labelIndex;
  }


  public List<Pair<Integer, Boolean>> getLabel() {

    return this.label;
  }


  public void setLabel(List<Pair<Integer, Boolean>> label) {

    this.label = label;
  }


  public List<Pair<Integer, Double>> getRight() {

    return this.right;
  }


  public void setRight(List<Pair<Integer, Double>> right) {

    this.right = right;
  }


  public List<Pair<Integer, Boolean>> getSuffix() {

    return this.suffix;
  }


  public void setSuffix(List<Pair<Integer, Boolean>> suffix) {

    this.suffix = suffix;
  }


  public List<Pair<Integer, Boolean>> getShape() {

    return this.shape;
  }


  public void setShape(List<Pair<Integer, Boolean>> shape) {

    this.shape = shape;
  }


  public List<Pair<Integer, Boolean>> getCluster() {

    return this.cluster;
  }


  public void setCluster(List<Pair<Integer, Boolean>> cluster) {

    this.cluster = cluster;
  }


  public int getIndex() {

    return this.index;
  }


  public void setIndex(int index) {

    this.index = index;
  }


  public boolean isAdjust() {

    return this.adjust;
  }


  public void setAdjust(boolean adjust) {

    this.adjust = adjust;
  }


  public int getLength() {

    return this.length;
  }


  public void setLength(int length) {

    this.length = length;
  }


  public String getWord() {

    return this.word;
  }


  public void setWord(String word) {

    this.word = word;
  }


  public String getLeftWord() {

    return this.leftWord;
  }


  public void setLeftWord(String leftWord) {

    this.leftWord = leftWord;
  }


  public String getRightWord() {

    return this.rightWord;
  }


  public void setRightWord(String rightWord) {

    this.rightWord = rightWord;
  }


  // GN on 14.10.2015
  // I have to shift offset by OffSets.tokenVectorSize + 1, and so later have to remove the +1
  // NOTE: I need to know which offset I need to substract the final -1
  public void setOffSetsOld(Alphabet alphabet, OffSets offSets) {

    this.elementOffset = (this.index * offSets.getTokenVectorSize()) + 1;
    this.leftOffset = this.elementOffset;
    this.rightOffset = this.leftOffset + offSets.getWvLeftSize();
    this.shapeOffset = this.rightOffset + offSets.getWvRightSize();
    this.suffixOffset = this.shapeOffset + offSets.getShapeSize();
    this.suffixOffset = (alphabet.isWithClusterFeats()) ? this.suffixOffset : this.suffixOffset - 1;
    this.clusterIdOffset = this.suffixOffset + offSets.getSuffixSize() - 1;
  }


  public void setOffSets(Alphabet alphabet, OffSets offSets) {

    this.elementOffset = (this.index * offSets.getTokenVectorSize()) + 1;
    this.leftOffset = this.elementOffset;
    this.rightOffset = this.leftOffset + offSets.getWvLeftSize();
    this.shapeOffset = this.rightOffset + offSets.getWvRightSize();
    this.suffixOffset = this.shapeOffset + offSets.getShapeSize();
    this.clusterIdOffset = this.suffixOffset + offSets.getSuffixSize();
    this.clusterIdOffset = (alphabet.isWithLabelFeats()) ? this.clusterIdOffset : this.clusterIdOffset - 1;
    this.labelOffset = this.clusterIdOffset + offSets.getClusterIdSize();
  }


  public void fillWordFeatures(String wordParam, int indexParam, Alphabet alphabet, boolean train) {

    // if word is a sentence padding element, then just return an empty WordFeatures
    if (wordParam.endsWith("<BOUNDARY>")) {
      return;
    }

    if (alphabet.isWithWordFeats()) {
      fillLeftDistributedWordFeatures(wordParam, alphabet, train, true);
      fillRightDistributedWordFeatures(wordParam, alphabet, train, true);
    }
    if (alphabet.isWithShapeFeats()) {
      fillShapeFeatures(wordParam, indexParam, alphabet, true);
    }
    if (alphabet.isWithSuffixFeats()) {
      fillSuffixFeatures(wordParam, alphabet, true);
    }
    if (alphabet.isWithClusterFeats()) {
      fillClusterIdFeatures(wordParam, alphabet, true);
    }
    if (alphabet.isWithLabelFeats()) {
      fillLabelFeatures(wordParam, alphabet, true);
    }
  }


  // boolean flag offline means: assume that features have been pre-loaded into to memory
  // boolean train means: training phase, which means do not handle unknown words
  // NOTE: since word is from input stream, need to lower-case it first
  private void fillLeftDistributedWordFeatures(String wordParam,
      Alphabet alphabet, boolean train, boolean offline) {

    String lowWord = wordParam.toLowerCase();
    String lowLeftWord = this.getLeftWord().toLowerCase();
    String lowRightWord = this.getRightWord().toLowerCase();
    // This may return a dynamically created word vector for unknown words
    WordDistributedFeature distributedWordVector =
        alphabet.getWordVectorFactory().getWordVector(lowWord, lowLeftWord, lowRightWord, train);
    for (int i = 0; i < distributedWordVector.getLeftContext().length; i++) {
      int localIndex = ((this.isAdjust()) ? (this.leftOffset + i) : i);
      double value = distributedWordVector.getLeftContext()[i];
      if (value > 0) {
        Pair<Integer, Double> node = new Pair<Integer, Double>(localIndex, value);
        this.left.add(node);
      }
    }
    this.length += this.left.size();
  }


  private void fillRightDistributedWordFeatures(String wordParam,
      Alphabet alphabet, boolean train, boolean offline) {

    // since word is from input stream, need to lower-case it first
    String lowWord = wordParam.toLowerCase();
    String lowLeftWord = this.getLeftWord().toLowerCase();
    String lowRightWord = this.getRightWord().toLowerCase();
    WordDistributedFeature distributedWordVector =
        alphabet.getWordVectorFactory().getWordVector(lowWord, lowLeftWord, lowRightWord, train);
    for (int i = 0; i < distributedWordVector.getRightContext().length; i++) {
      int localIndex = ((this.isAdjust()) ? (this.rightOffset + i) : i);
      double value = distributedWordVector.getRightContext()[i];
      if (value > 0) {
        Pair<Integer, Double> node = new Pair<Integer, Double>(localIndex, value);
        this.right.add(node);
      }
    }
    this.length += this.right.size();
  }


  /**
   * boolean flag offline means: assume that known signatures have been pre-loaded into to memory
   * NOTE: even in training phase, signature are computed dynamically
   * NOTE: we assume that a word has a unique signature so the list shape actually only contains a single element.
   * NOTE: word is case-sensitive, because otherwise shape feature can be computed reliable!
   * @param word
   * @param index
   * @param alphabet
   * @param offline
   */
  // NOTE: it is an overhead to keep a list of shapes, because we always have a single element,
  // but it keeps code more transparent
  private void fillShapeFeatures(String wordParam, int indexParam, Alphabet alphabet, boolean offline) {

    int wordShapeIndex = alphabet.getWordShapeFactory().getShapeFeature(wordParam, indexParam);
    if (wordShapeIndex > -1) {
      /*
      System.out.println("Word: " + word + " Shape: "
          + alphabet.getWordShapeFactory().getIndex2signature().get(wordShapeIndex));
      */
      int realIndex = (this.isAdjust()) ? (this.shapeOffset + wordShapeIndex) : wordShapeIndex;
      Pair<Integer, Boolean> node = new Pair<Integer, Boolean>(realIndex, true);
      this.shape.add(node);
    } else {
      // we have an unknown signature, so we cannot add it to the list
      // which basically means that shape-size() will remain 0
      System.err.println("Word: " + wordParam + " at loc: " + indexParam + ": unknown signature!");

    }
    // should be always 1
    this.length += this.shape.size();
  }


  /**
   * boolean flag offline means: assume that suffixes have been preprocessed
   * and pre-loaded into to memory
   * NOTE: word is lowerCased!
   */
  private void fillSuffixFeatures(String wordParam, Alphabet alphabet, boolean offline) {

    /*
     * Lowercase word
     * Create all suffixes for word
     * Loop them up
     * Return suffixes list of Pairs (index, true)
     */
    // since word is from input stream, need to lower-case it first
    String lowWord = wordParam.toLowerCase();
    List<Integer> suffixIndices = alphabet.getWordSuffixFactory().getAllKnownSubstringsForWord(lowWord);
    //if (suffixIndices.isEmpty()) System.err.println("No known suffixes: " + word);
    for (int x : suffixIndices) {
      int realIndex = (this.isAdjust()) ? (this.suffixOffset + x) : x;
      Pair<Integer, Boolean> node = new Pair<Integer, Boolean>(realIndex, true);
      this.suffix.add(node);
    }
    // this means that if word has no known suffix, then suffix list is 0
    this.length += this.suffix.size();
  }


  /**
   * boolean flag offline means: assume that known cluster IDs have been pre-loaded into to memory
   * NOTE: even in training phase, signature are computed dynamically
   * NOTE: we assume that a word has a unique signature so the list cluster IDs actually only contains a single element.
   * NOTE: word is case-sensitive, because otherwise cluster IDs feature can be computed reliable!
   * @param word
   * @param alphabet
   * @param offline
   */
  // NOTE: it is an overhead to keep a list of cluster IDs, because we always have a single element,
  // but it keeps code more transparent
  private void fillClusterIdFeatures(String wordParam, Alphabet alphabet, boolean offline) {

    int wordClusterIndex = alphabet.getWordClusterFactory().getClusterIdFeature(wordParam);
    if (wordClusterIndex > -1) {
      int realIndex = (this.isAdjust()) ? (this.clusterIdOffset + wordClusterIndex) : wordClusterIndex;
      //System.out.println("Word: " + word + " ClusterId: " + wordClusterIndex + " Realindex: " + realIndex);
      Pair<Integer, Boolean> node = new Pair<Integer, Boolean>(realIndex, true);
      this.cluster.add(node);
    } else {
      // we have an unknown word with no cluster Id,
      // This should not happen, because unknown words are matched to <RARE> dummy word, if
      // not found
      System.err.println("Word: " + wordParam + ": unknown clusterID!");
    }
    // should be always 1
    this.length += this.cluster.size();
  }


  private void fillLabelFeatures(String wordParam, Alphabet alphabet, boolean offline) {

    int localLabelIndex = (this.getLabelIndex() > -1) ? this.getLabelIndex() : this.getOffSets().getLabelVectorSize();
    int realIndex = (this.isAdjust()) ? (this.labelOffset + localLabelIndex) : localLabelIndex;

    /*
    System.out.println("Word: " + wordParam + " LabelId: " + this.getLabelIndex()
        + " LabelIndex: " + localLabelIndex + " Realindex: " + realIndex);
    */

    Pair<Integer, Boolean> node = new Pair<Integer, Boolean>(realIndex, true);
    this.label.add(node);
    // should be always 1
    this.length += this.label.size();
  }


  public String toOffSetsString() {

    String output = "\nElement-" + this.getIndex() + "\n";
    output += "OffSets:\n";
    output += "Element: " + this.elementOffset + "\n";
    output += "Length: " + this.length + "\n";
    output += "Left: " + this.leftOffset + "\n";
    output += "Right: " + this.rightOffset + "\n";
    output += "Shape: " + this.shapeOffset + "\n";
    output += "Suffix: " + this.suffixOffset + "\n";
    output += "ClusterId: " + this.clusterIdOffset + "\n";
    output += "Label: " + this.labelOffset + "\n";
    return output;
  }


  @Override
  public String toString() {

    String output = "\nElement-" + this.getIndex();
    output += this.toOffSetsString();
    output += "\nLeft: ";
    for (Pair<Integer, Double> pair : this.left) {
      output += pair.toString();
    }
    output += "\nRight: ";
    for (Pair<Integer, Double> pair : this.right) {
      output += pair.toString();
    }
    output += "\nSuffix: ";
    for (Pair<Integer, Boolean> pair : this.suffix) {
      output += pair.toString();
    }
    output += "\nShape: ";
    for (Pair<Integer, Boolean> pair : this.shape) {
      output += pair.toString();
    }

    output += "\nCluster: ";
    for (Pair<Integer, Boolean> pair : this.cluster) {
      output += pair.toString();
    }

    output += "\nLabel: ";
    for (Pair<Integer, Boolean> pair : this.label) {
      output += pair.toString();
    }
    return output;
  }
}
