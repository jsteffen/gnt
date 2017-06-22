package de.dfki.mlt.gnt.recodev;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.dfki.mlt.gnt.data.SetIndexMap;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class Data {

  private SetIndexMap wordSet = new SetIndexMap();
  private SetIndexMap labelSet = new SetIndexMap();
  private Path labelMapPath = Paths.get("resources/recodev/labelSet.txt");
  private Path wordMapPath = Paths.get("resources/recodev/wordSet.txt");


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

    this.getLabelSet().writeSetIndexMap(this.labelMapPath);
  }


  public void readLabelSet() {

    this.getLabelSet().readSetIndexMap(this.labelMapPath);
  }


  public void saveWordSet() {

    this.getWordSet().writeSetIndexMap(this.wordMapPath);
  }


  public void readWordSet() {

    this.getWordSet().readSetIndexMap(this.wordMapPath);
  }


  @Override
  public String toString() {

    String output = "";
    output += " words: " + this.getWordSet().getLabelCnt()
        + " labels: " + this.getLabelSet().getLabelCnt() + "\n";
    return output;
  }
}
