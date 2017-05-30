package de.dfki.mlt.gnt.corpus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.dfki.mlt.gnt.data.Data;
import de.dfki.mlt.gnt.data.GlobalParams;

/**
 * This class takes as input a conll file of gold tags and predicted tags
 * <li> computes accuracy
 * <li> and writes out a file with extension .debug of all false tags in form of line number: word gold-tag
 * predicted-tag
 * <li> a file with extension .errs of all wrong tag-pairs gold-tag predicted-tag together with ist frequency
 * <li> a file with extension .iob containing just the words and their predicetd tags
 *
 * @author Günter Neumann, DFKI
 */
public class ConllEvaluator {

  // evaluation results
  private double acc;
  private double accOOV;
  private double accInV;

  private Data data;


  public ConllEvaluator(Data data) {

    this.data = data;
  }


  public double getAcc() {

    return this.acc;
  }


  public double getAccOOV() {

    return this.accOOV;
  }


  public double getAccInV() {

    return this.accInV;
  }


  public void computeAccuracy(Path evalPath, boolean debug) throws IOException {

    BufferedReader conllReader = Files.newBufferedReader(evalPath, StandardCharsets.UTF_8);

    // the following writers are only initialized when in debug mode
    PrintWriter debugWriter = null;
    PrintWriter errorWriter = null;
    PrintWriter iobWriter = null;
    if (debug) {
      debugWriter = new PrintWriter(Files.newBufferedWriter(
          Paths.get(evalPath + ".debug"), StandardCharsets.UTF_8));
      errorWriter = new PrintWriter(Files.newBufferedWriter(
          Paths.get(evalPath + ".errs"), StandardCharsets.UTF_8));
      iobWriter = new PrintWriter(Files.newBufferedWriter(
          Paths.get(evalPath + ".iob"), StandardCharsets.UTF_8));
    }

    int goldPosCnt = 0;
    int correctPosCnt = 0;
    int goldOOVCnt = 0;
    int correctOOVCnt = 0;
    String line = "";
    int lineCnt = 0;
    // count frequency of wrong tags in form of "GOLDTAG-PREDICTEDTAG"
    Map<String, Integer> wrongTags = new HashMap<String, Integer>();
    while ((line = conllReader.readLine()) != null) {
      lineCnt++;
      if (!line.isEmpty()) {
        if (!line.equals("-X- -X- -X- -X-")) {
          String[] tokenizedLine = line.split(" ");
          String word = tokenizedLine[1];

          String goldPos = tokenizedLine[2];
          String predPos = tokenizedLine[3];

          if (iobWriter != null) {
            // Write out basic information only: word + predicted tag
            iobWriter.println(word + "\t" + predPos);
          }

          goldPosCnt++;
          if (predPos.equals(goldPos)) {
            correctPosCnt++;
          } else if (debugWriter != null) {
            debugWriter.write(lineCnt + ": " + line + "\n");
            // increase wrong tag count
            String wrongTag = goldPos + ":" + predPos;
            Integer count = wrongTags.get(wrongTag);
            if (count != null) {
              wrongTags.put(wrongTag, count + 1);
            } else {
              wrongTags.put(wrongTag, 1);
            }
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
        if (iobWriter != null) {
          iobWriter.println();
        }
      }
    }
    if (errorWriter != null) {
      wrongTags = sortByValue(wrongTags);
      for (Map.Entry<String, Integer> entry : wrongTags.entrySet()) {
        errorWriter.println(entry.getKey() + "\t" + entry.getValue());
      }
    }

    // close reader/writers
    conllReader.close();
    if (debugWriter != null) {
      debugWriter.close();
    }
    if (errorWriter != null) {
      errorWriter.close();
    }
    if (iobWriter != null) {
      iobWriter.close();
    }

    // accuracy for all words of test file
    this.acc = (double)correctPosCnt / (double)goldPosCnt;
    // accuracy for all out of vocabulary words of test file
    this.accOOV = (double)correctOOVCnt / (double)goldOOVCnt;
    // accuracy for known vocabulary words of test file
    int correctKnownWords = (goldPosCnt - goldOOVCnt);
    int correctFoundKnownWords = (correctPosCnt - correctOOVCnt);
    this.accInV = (double)correctFoundKnownWords / (double)correctKnownWords;

    DecimalFormat formatter = new DecimalFormat("#0.00");

    System.out.println("All pos: " + goldPosCnt + " Correct: " + correctPosCnt + " Accuracy: "
        + formatter.format(this.acc * 100) + "%");
    System.out.println("All OOV pos: " + goldOOVCnt + " Correct: " + correctOOVCnt + " Accuracy: "
        + formatter.format(this.accOOV * 100) + "%");
    System.out.println("All InV pos: " + correctKnownWords + " Correct: " + correctFoundKnownWords + " Accuracy: "
        + formatter.format(this.accInV * 100) + "%");
  }


  // sort hash according to value in decreasing order;
  // transform hashmap to treemap by using ValueComparator
  private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {

    Map<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(unsortedMap));
    sortedMap.putAll(unsortedMap);
    return sortedMap;
  }


  public static void main(String[] args) {

    // This is for testing
    // This reads saved vocabulary from training corpus
    GlobalParams globals = new GlobalParams();
    globals.setTaggerName("DEPOSMORPH");
    Data data = new Data();
    data.readWordSet();
    ConllEvaluator evalFile = new ConllEvaluator(data);

    try {
      evalFile.computeAccuracy(Paths.get("resources/eval/tiger2_posmorph_devel.txt"), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
