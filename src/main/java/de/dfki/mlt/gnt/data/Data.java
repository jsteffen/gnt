package de.dfki.mlt.gnt.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.GlobalConfig;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class Data {

  // counted from 0, 2nd column in conll in case of POS, else 0 for NER
  /**
   * Index of wordform in conll format
   */
  private static int wordFormIndex = 1;
  // counted from 0, 5th column in conll
  private static int posTagIndex = 4;

  private SetIndexMap wordSet = new SetIndexMap();
  private SetIndexMap labelSet = new SetIndexMap();
  private Sentence sentence = new Sentence();
  private int sentenceCnt = 0;
  private List<Window> instances = new ArrayList<Window>();
  private Path labelMapPath;
  private Path wordMapPath;


  public Data() {
    this.labelMapPath = GlobalConfig.getModelBuildFolder().resolve("labelSet.txt");
    this.wordMapPath = GlobalConfig.getModelBuildFolder().resolve("wordSet.txt");
  }


  public static int getWordFormIndex() {

    return wordFormIndex;
  }


  public static void setWordFormIndex(int wordFormIndex) {

    Data.wordFormIndex = wordFormIndex;
  }


  public static int getPosTagIndex() {

    return posTagIndex;
  }


  public static void setPosTagIndex(int posTagIndex) {

    Data.posTagIndex = posTagIndex;
  }


  public List<Window> getInstances() {

    return this.instances;
  }


  public void setInstances(List<Window> instances) {

    this.instances = instances;
  }


  public int getSentenceCnt() {

    return this.sentenceCnt;
  }


  public void setSentenceCnt(int sentenceCnt) {

    this.sentenceCnt = sentenceCnt;
  }


  public SetIndexMap getWordSet() {

    return this.wordSet;
  }


  public void setWordSet(SetIndexMap wordSet) {

    this.wordSet = wordSet;
  }


  public SetIndexMap getLabelSet() {

    return this.labelSet;
  }


  public void setLabelSet(SetIndexMap labelSet) {

    this.labelSet = labelSet;
  }


  public Sentence getSentence() {

    return this.sentence;
  }


  public void setSentence(Sentence sentence) {

    this.sentence = sentence;
  }


  public Path getLabelMapPath() {

    return this.labelMapPath;
  }


  public Path getWordMapPath() {

    return this.wordMapPath;
  }


  private int updateWordMap(String word) {

    return this.getWordSet().updateSetIndexMap(word);
  }


  private int updateLabelMap(String label) {

    return this.getLabelSet().updateSetIndexMap(label);
  }


  /**
   * If all conll lines of a sentence have been collected
   * extract the relevant information (here word and pos)
   * and make a sentence object of it (two parallel int[];)
   * as a side effect, word and pos SetIndexMaps are created
   * and stored in the data object
   * @param tokens
   */
  public void generateSentenceObjectFromConllLabeledSentence(List<String[]> tokens) {

    // tokens are of form
    // "1  The  The  DT  DT  _  2  NMOD"
    // NOTE: No lower case here of word
    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // Extract word and pos from conll sentence, create index for both
      // and create sentence using word/pos index
      newSentence.addNextToken(i,
          updateWordMap(tokens.get(i)[Data.wordFormIndex]),
          updateLabelMap(tokens.get(i)[Data.posTagIndex]));
    }
    this.setSentence(newSentence);
    this.sentenceCnt++;
  }


  /**
   * Tokens are a list of words in form of conll strings.
   * <li> the words are unlabeled
   * <li> No lower case here of word
   * <li> Using a dummy POS "UNK" encoded as -1
   * @param tokens
   */
  public void generateSentenceObjectFromConllUnLabeledSentence(List<String[]> tokens) {

    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // Extract word and pos from conll sentence, create index for both
      // and create sentence using word/pos index
      newSentence.addNextToken(i,
          updateWordMap(tokens.get(i)[Data.wordFormIndex]),
          -1);
    }
    this.setSentence(newSentence);
    this.sentenceCnt++;
  }


  /**
   * Tokens are a vector of words in form of strings.
   * <li> the words are unlabeled
   * <li> No lower case here of word
   * <li> Using a dummy POS "UNK" encoded as -1
   * @param tokens
   */
  public void generateSentenceObjectFromUnlabeledTokens(List<String> tokens) {

    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // tokens are strings
      // NOTE: No lower case here of word
      // Using a dummy POS -1
      newSentence.addNextToken(i,
          updateWordMap(tokens.get(i)),
          -1);
    }
    this.setSentence(newSentence);
    this.sentenceCnt++;

  }


  public void cleanWordSet() {

    this.wordSet = new SetIndexMap();
  }


  public void cleanLabelSet() {

    this.labelSet = new SetIndexMap();
  }


  public void cleanInstances() {

    this.instances = new ArrayList<Window>();
  }


  public void saveLabelSet() {

    this.getLabelSet().writeSetIndexMap(this.getLabelMapPath());
  }


  public void readLabelSet() {

    this.getLabelSet().readSetIndexMap(this.getLabelMapPath());
  }


  public void readLabelSet(Archivator archivator) {

    this.getLabelSet().readSetIndexMap(archivator, this.getLabelMapPath());
  }


  public void saveWordSet() {

    this.getWordSet().writeSetIndexMap(this.getWordMapPath());
  }


  public void readWordSet() {

    this.getWordSet().readSetIndexMap(this.getWordMapPath());
  }


  public void readWordSet(Archivator archivator) {

    this.getWordSet().readSetIndexMap(archivator, this.getWordMapPath());
  }


  @Override
  public String toString() {

    String output = "";
    output += "Sentences: " + this.sentenceCnt
        + " words: " + this.getWordSet().getLabelCnt()
        + " labels: " + this.getLabelSet().getLabelCnt() + "\n";
    return output;
  }
}
