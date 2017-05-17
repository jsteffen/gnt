package de.dfki.mlt.gnt.recodev;

import de.dfki.mlt.gnt.data.SetIndexMap;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class Data {

  private SetIndexMap wordSet = new SetIndexMap();
  private SetIndexMap labelSet = new SetIndexMap();
  private String labelMapFileName = "resources/recodev/labelSet.txt";
  private String wordMapFileName = "resources/recodev/wordSet.txt";


  public Data() {
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


  public String getLabelMapFileName() {

    return this.labelMapFileName;
  }


  public void setLabelMapFileName(String labelMapFileName) {

    this.labelMapFileName = labelMapFileName;
  }


  public String getWordMapFileName() {

    return this.wordMapFileName;
  }


  public void setWordMapFileName(String wordMapFileName) {

    this.wordMapFileName = wordMapFileName;
  }


  public int updateWordMap(String word) {

    return this.getWordSet().updateSetIndexMap(word);
  }


  public int updateLabelMap(String label) {

    return this.getLabelSet().updateSetIndexMap(label);
  }


  public void cleanWordSet() {

    this.wordSet = new SetIndexMap();
  }


  public void cleanLabelSet() {

    this.labelSet = new SetIndexMap();
  }


  public void saveLabelSet() {

    this.getLabelSet().writeSetIndexMap(this.getLabelMapFileName());
  }


  public void readLabelSet() {

    this.getLabelSet().readSetIndexMap(this.getLabelMapFileName());
  }


  public void saveWordSet() {

    this.getWordSet().writeSetIndexMap(this.getWordMapFileName());
  }


  public void readWordSet() {

    this.getWordSet().readSetIndexMap(this.getWordMapFileName());
  }


  @Override
  public String toString() {

    String output = "";
    output += " words: " + this.getWordSet().getLabelCnt()
        + " labels: " + this.getLabelSet().getLabelCnt() + "\n";
    return output;
  }
}
