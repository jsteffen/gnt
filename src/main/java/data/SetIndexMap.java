package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import archive.Archivator;

public class SetIndexMap {

  Map<Integer, String> num2label = new TreeMap<Integer, String>();
  private Map<String, Integer> label2num = new HashMap<String, Integer>();
  private int labelCnt = 0;


  // Setters and getters
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


  // Methods
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


  public void writeSetIndexMap(String targetFileName) {

    File file = new File(targetFileName);
    file.getParentFile().mkdirs();
    BufferedWriter writer;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
      for (int key : this.getNum2label().keySet()) {
        writer.write(this.getNum2label().get(key) + "\n");
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readSetIndexMap(String string) {

    BufferedReader reader;
    int cnt = 0;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(string), "UTF-8"));
      String line;
      while ((line = reader.readLine()) != null) {
        cnt++;
        this.getLabel2num().put(line, cnt);
        this.getNum2label().put(cnt, line);
      }
      this.labelCnt = cnt++;
      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readSetIndexMap(Archivator archivator, String string) {

    BufferedReader reader;
    int cnt = 0;
    try {
      InputStream inputStream = archivator.getArchiveMap().get(string);
      reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      String line;
      while ((line = reader.readLine()) != null) {
        cnt++;
        this.getLabel2num().put(line, cnt);
        this.getNum2label().put(cnt, line);
      }
      this.labelCnt = cnt++;
      reader.close();

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
