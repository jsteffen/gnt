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


  public SetIndexMap getLabelSet() {

    return this.labelSet;
  }


  public void saveLabelSet() {

    this.getLabelSet().write(this.labelMapPath);
  }


  public void readLabelSet() {

    this.getLabelSet().readFromPath(this.labelMapPath);
  }


  public void saveWordSet() {

    this.getWordSet().write(this.wordMapPath);
  }


  public void readWordSet() {

    this.getWordSet().readFromPath(this.wordMapPath);
  }


  @Override
  public String toString() {

    String output = "";
    output += " words: " + this.getWordSet().size()
        + " labels: " + this.getLabelSet().size() + "\n";
    return output;
  }
}
