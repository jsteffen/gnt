package de.dfki.mlt.gnt.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.dfki.mlt.gnt.archive.Archivator;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class SetIndexMap {

  private Map<Integer, String> num2label = new TreeMap<Integer, String>();
  private Map<String, Integer> label2num = new HashMap<String, Integer>();
  private int labelCnt = 0;


  public Map<String, Integer> getLabel2num() {

    return this.label2num;
  }


  public void setLabel2num(Map<String, Integer> label2num) {

    this.label2num = label2num;
  }


  public Map<Integer, String> getNum2label() {

    return this.num2label;
  }


  public void setNum2label(Map<Integer, String> num2label) {

    this.num2label = num2label;
  }


  public int getLabelCnt() {

    return this.labelCnt;
  }


  public void setLabelCnt(int labelCnt) {

    this.labelCnt = labelCnt;
  }


  public int updateSetIndexMap(String label) {

    int index = -1;
    if (this.getLabel2num().containsKey(label)) {
      index = this.getLabel2num().get(label);
    } else {
      this.labelCnt++;
      this.getLabel2num().put(label, this.labelCnt);
      this.getNum2label().put(this.labelCnt, label);
      index = this.labelCnt;
    }
    return index;
  }


  public void writeSetIndexMap(Path targetPath) {

    try {
      Files.createDirectories(targetPath.getParent());
      try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
          targetPath, StandardCharsets.UTF_8))) {
        for (int key : this.getNum2label().keySet()) {
          out.println(this.getNum2label().get(key));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void readSetIndexMap(Path path) {

    try (BufferedReader in = Files.newBufferedReader(
        path, StandardCharsets.UTF_8)) {
      int cnt = 0;
      String line;
      while ((line = in.readLine()) != null) {
        cnt++;
        this.getLabel2num().put(line, cnt);
        this.getNum2label().put(cnt, line);
      }
      this.labelCnt = cnt++;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readSetIndexMap(Archivator archivator, String setFileName) {

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(archivator.getInputStream(setFileName), "UTF-8"))) {
      int cnt = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        cnt++;
        this.getLabel2num().put(line, cnt);
        this.getNum2label().put(cnt, line);
      }
      this.labelCnt = cnt++;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public String toString() {

    String output = "";
    for (int index : getNum2label().keySet()) {
      output += index + ": " + getNum2label().get(index) + "\n";
    }
    return output;
  }
}
