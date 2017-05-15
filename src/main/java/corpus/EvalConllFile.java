package corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import data.Data;
import data.GlobalParams;

/**
 * This class takes as input a conll file of gold tags and predicted tags
 * - computes accuracy
 * - and writes out a file with extension .debug of all false tags in form of
 *   line number: word gold-tag predicted-tag
 * - a file with extension .errs of all wrong tag-pairs gold-tag predicted-tag together with ist frequency
 * - a file with extension .iob containing just the words and their predicetd tags
 * @author gune00
 *
 */
public class EvalConllFile {

  private static double acc;
  private static double accOOV;
  private static double accInV;

  private Data data = new Data();
  private Map<String, Integer> wrongTagsHash = new HashMap<String, Integer>();


  public EvalConllFile() {
  }


  public EvalConllFile(String featureFilePathname, String taggerName) {
    this.setData(new Data(featureFilePathname, taggerName));
  }


  public Data getData() {

    return this.data;
  }


  public Map<String, Integer> getWrongTagsHash() {

    return this.wrongTagsHash;
  }


  public void setWrongTagsHash(Map<String, Integer> wrongTagsHash) {

    this.wrongTagsHash = wrongTagsHash;
  }


  public void setData(Data data) {

    this.data = data;
  }


  public double getAcc() {

    return acc;
  }


  public double getAccOOV() {

    return accOOV;
  }


  public double getAccInV() {

    return accInV;
  }


  private void resetWrongTagsHash() {

    this.wrongTagsHash = new HashMap<String, Integer>();
  }


  // Count frequency of wrong tags in form of "GOLDTAG-PREDICTEDTAG"
  private void countWrongTag(String wrongTag) {

    if (this.getWrongTagsHash().containsKey(wrongTag)) {
      this.getWrongTagsHash().put(wrongTag, this.getWrongTagsHash().get(wrongTag) + 1);
    } else {
      this.getWrongTagsHash().put(wrongTag, 1);
    }
  }


  // Sort hash according to value in decreasing order
  // transform hashmap to treemap by using ValueComparator
  private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {

    Map<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(unsortedMap));
    sortedMap.putAll(unsortedMap);
    return sortedMap;
  }


  private void writeWrongTagsHash(BufferedWriter errorWriter) {

    try {
      for (Map.Entry<String, Integer> entry : this.getWrongTagsHash().entrySet()) {
        errorWriter.write(entry.getKey() + "\t" + entry.getValue() + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void computeAccuracy(String sourceFileName, boolean debug) throws IOException {

    BufferedReader conllReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(sourceFileName), "UTF-8"));
    File debugFileName = new File(sourceFileName + ".debug");
    File iobFileName = new File(sourceFileName + ".iob");
    File errorFileName = new File(sourceFileName + ".errs");
    BufferedWriter debugWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(debugFileName), "UTF-8"));
    BufferedWriter errorWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(errorFileName), "UTF-8"));
    BufferedWriter iobWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(iobFileName), "UTF-8"));

    int goldPosCnt = 0;
    int correctPosCnt = 0;
    int goldOOVCnt = 0;
    int correctOOVCnt = 0;
    String line = "";
    int lineCnt = 0;
    this.resetWrongTagsHash();
    while ((line = conllReader.readLine()) != null) {
      lineCnt++;
      if (!line.isEmpty()) {
        if (!line.equals("-X- -X- -X- -X-")) {
          String[] tokenizedLine = line.split(" ");
          String word = tokenizedLine[1];

          String goldPos = tokenizedLine[2];
          String predPos = tokenizedLine[3];

          // Write out basic information only: word + predicted tag
          iobWriter.write(word + "\t" + predPos + "\n");

          goldPosCnt++;
          if (predPos.equals(goldPos)) {
            correctPosCnt++;
          } else if (debug) {
            debugWriter.write(lineCnt + ": " + line + "\n");
            this.countWrongTag(goldPos + ":" + predPos);
          }

          // Counting our of vocabulary words
          // TODO: note I do not lower case words when counting OOV -> correct?
          // I guess so, because words in getWordSet() are also not lower-cased
          // -> not sure, better try lowercase it as well
          boolean knownWord = this.data.getWordSet().getLabel2num().containsKey(word);
          if (!knownWord) {
            goldOOVCnt++;
          }
          if (!knownWord && predPos.equals(goldPos)) {
            correctOOVCnt++;
          }

        }
      } else {
        iobWriter.write("\n");
      }
    }
    if (debug) {
      this.setWrongTagsHash(sortByValue(this.getWrongTagsHash()));
      this.writeWrongTagsHash(errorWriter);
    }
    conllReader.close();
    debugWriter.close();
    errorWriter.close();
    iobWriter.close();
    if (!debug) {
      debugFileName.delete();
      errorFileName.delete();
      iobFileName.delete();
    }

    // accuracy for all words of test file
    acc = (double)correctPosCnt / (double)goldPosCnt;
    // accuracy for all out of vocabulary words of test file
    accOOV = (double)correctOOVCnt / (double)goldOOVCnt;
    // accuracy for known vocabulary words of test file
    int correctKnownWords = (goldPosCnt - goldOOVCnt);
    int correctFoundKnownWords = (correctPosCnt - correctOOVCnt);
    accInV = (double)correctFoundKnownWords / (double)correctKnownWords;


    DecimalFormat formatter = new DecimalFormat("#0.00");

    System.out.println("All pos: " + goldPosCnt + " Correct: " + correctPosCnt + " Accuracy: "
        + formatter.format(acc * 100) + "%");
    System.out.println("All OOV pos: " + goldOOVCnt + " Correct: " + correctOOVCnt + " Accuracy: "
        + formatter.format(accOOV * 100) + "%");
    System.out.println("All InV pos: " + correctKnownWords + " Correct: " + correctFoundKnownWords + " Accuracy: "
        + formatter.format(accInV * 100) + "%");
  }


  public static void main(String[] args) throws IOException {

    // This is for testing
    // This reads saved vocabulary from training corpus
    GlobalParams globals = new GlobalParams();
    globals.setTaggerName("DEPOSMORPH");
    EvalConllFile evalFile = new EvalConllFile();
    evalFile.data.readWordSet();

    evalFile.computeAccuracy(globals.getEvalFilePathname() + "/tiger2_posmorph_devel.txt", true);
  }
}
