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

  private Map<Integer, String> index2label = new TreeMap<Integer, String>();
  private Map<String, Integer> label2index = new HashMap<String, Integer>();
  private int labelCnt = 0;


  public int size() {

    return this.labelCnt;
  }


  /**
   * Adds an entry for the given label 9if it doesn't exist yet)
   * and returns its index.
   *
   * @param label
   *          the label
   * @return the label's index
   */
  public int addLabel(String label) {

    int index = -1;
    if (this.label2index.containsKey(label)) {
      index = this.label2index.get(label);
    } else {
      this.labelCnt++;
      this.label2index.put(label, this.labelCnt);
      this.index2label.put(this.labelCnt, label);
      index = this.labelCnt;
    }
    return index;
  }


  public String getLabel(int index) {

    return this.index2label.get(index);
  }


  public int getIndex(String label) {

    Integer index = this.label2index.get(label);
    if (index == null) {
      return -1;
    }
    return index;
  }


  public void write(Path targetPath) {

    try {
      Files.createDirectories(targetPath.getParent());
      try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
          targetPath, StandardCharsets.UTF_8))) {
        for (int key : this.index2label.keySet()) {
          out.println(this.index2label.get(key));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readFromPath(Path path) {

    try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readFromArchive(Archivator archivator, String setFileName) {

    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(archivator.getInputStream(setFileName), "UTF-8"))) {
      read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void read(BufferedReader in)
      throws IOException {

    int cnt = 0;
    String line;
    while ((line = in.readLine()) != null) {
      cnt++;
      this.label2index.put(line, cnt);
      this.index2label.put(cnt, line);
    }
    this.labelCnt = cnt++;
  }


  public void clean() {

    this.index2label.clear();
    this.label2index.clear();
    this.labelCnt = 0;
  }


  @Override
  public String toString() {

    String output = "";
    for (int index : this.index2label.keySet()) {
      output += index + ": " + this.index2label.get(index) + "\n";
    }
    return output;
  }
}
