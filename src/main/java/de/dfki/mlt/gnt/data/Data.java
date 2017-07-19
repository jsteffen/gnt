package de.dfki.mlt.gnt.data;

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

  private SetIndexMap wordSet = new SetIndexMap();
  private SetIndexMap labelSet = new SetIndexMap();
  private int sentenceCnt = 0;
  private List<Window> instances = new ArrayList<Window>();
  private String labelMapFileName;
  private String wordMapFileName;


  public Data() {

    this.labelMapFileName = "labelSet.txt";
    this.wordMapFileName = "wordSet.txt";
  }


  public List<Window> getInstances() {

    return this.instances;
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


  public SetIndexMap getLabelSet() {

    return this.labelSet;
  }


  public String getLabelMapFileName() {

    return this.labelMapFileName;
  }


  public String getWordMapFileName() {

    return this.wordMapFileName;
  }


  private int updateWordMap(String word) {

    return this.wordSet.updateSetIndexMap(word);
  }


  private int updateLabelMap(String label) {

    return this.labelSet.updateSetIndexMap(label);
  }


  /**
   * If all conll lines of a sentence have been collected
   * extract the relevant information (here word and pos)
   * and make a sentence object of it (two parallel int[];)
   * as a side effect, word and pos SetIndexMaps are created
   * and stored in the data object
   * @param tokens
   */
  public Sentence generateSentenceObjectFromConllLabeledSentence(
      List<String[]> tokens, int wordFormIndex, int tagIndex) {

    // tokens are of form
    // "1  The  The  DT  DT  _  2  NMOD"
    // NOTE: No lower case here of word
    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // Extract word and pos from conll sentence, create index for both
      // and create sentence using word/pos index
      newSentence.addNextToken(i,
          updateWordMap(tokens.get(i)[wordFormIndex]),
          updateLabelMap(tokens.get(i)[tagIndex]));
    }
    this.sentenceCnt++;
    return newSentence;
  }


  /**
   * Tokens are a list of words in form of conll strings.
   * <li> the words are unlabeled
   * <li> No lower case here of word
   * <li> Using a dummy POS "UNK" encoded as -1
   * @param tokens
   */
  public Sentence generateSentenceObjectFromConllUnLabeledSentence(List<String[]> tokens, int wordFormIndex) {

    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // Extract word and pos from conll sentence, create index for both
      // and create sentence using word/pos index
      newSentence.addNextToken(i,
          updateWordMap(tokens.get(i)[wordFormIndex]),
          -1);
    }
    this.sentenceCnt++;
    return newSentence;
  }


  /**
   * Tokens are a vector of words in form of strings.
   * <li> the words are unlabeled
   * <li> No lower case here of word
   * <li> Using a dummy POS "UNK" encoded as -1
   * @param tokens
   */
  public Sentence generateSentenceObjectFromUnlabeledTokens(List<String> tokens) {

    this.wordSet.clean();

    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // tokens are strings
      // NOTE: No lower case here of word
      // Using a dummy POS -1
      newSentence.addNextToken(i,
          updateWordMap(tokens.get(i)),
          -1);
    }
    this.sentenceCnt++;
    return newSentence;
  }


  public void cleanInstances() {

    this.instances = new ArrayList<Window>();
  }


  public void saveLabelSet() {

    this.labelSet.writeSetIndexMap(GlobalConfig.getModelBuildFolder().resolve(this.labelMapFileName));
  }


  public void readLabelSet() {

    this.labelSet.readSetIndexMap(GlobalConfig.getModelBuildFolder().resolve(this.labelMapFileName));
  }


  public void readLabelSet(Archivator archivator) {

    this.labelSet.readSetIndexMap(archivator, this.labelMapFileName);
  }


  public void saveWordSet() {

    this.wordSet.writeSetIndexMap(GlobalConfig.getModelBuildFolder().resolve(this.wordMapFileName));
  }


  public void readWordSet() {

    this.wordSet.readSetIndexMap(GlobalConfig.getModelBuildFolder().resolve(this.wordMapFileName));
  }


  public void readWordSet(Archivator archivator) {

    this.wordSet.readSetIndexMap(archivator, this.wordMapFileName);
  }


  @Override
  public String toString() {

    String output = "";
    output += "Sentences: " + this.sentenceCnt
        + " words: " + this.wordSet.getLabelCnt()
        + " labels: " + this.labelSet.getLabelCnt() + "\n";
    return output;
  }
}
