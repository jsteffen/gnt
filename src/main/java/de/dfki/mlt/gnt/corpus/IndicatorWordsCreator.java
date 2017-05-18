package de.dfki.mlt.gnt.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.dfki.mlt.gnt.archive.Archivator;

/**
 * A indicator word is used to define a dimension of distributed word vectors
 * a indicator word is selected on basis of its rank.
 *<p>
 * A class for creating the indicator words for a given text corpus
 * <li> each line of a text file corresponds to a sentence of lower-cased words
 * <li> depending on the corpus sources, some cleaning has to be done
 * <li> then term frequency TF is computed and finally a ranked list is computed in decreasing order of TF
 * <li> the final list is then output - either completely or only N highest terms
 * <p>
 * NOTE: it is open for other corpora as well
 *
 * @author Günter Neumann, DFKI
 */
public class IndicatorWordsCreator {

  private static int lineCnt = 0;
  private static int tokenCnt = 0;
  private Map<String, Integer> wordToNum = new HashMap<String, Integer>();
  private String featureFilePathname = "";


  public IndicatorWordsCreator() {
  }


  public IndicatorWordsCreator(String featureFilePathname) {
    this.setFeatureFilePathname(featureFilePathname);
  }


  public String getFeatureFilePathname() {

    return this.featureFilePathname;
  }


  public void setFeatureFilePathname(String featureFilePathname) {

    this.featureFilePathname = featureFilePathname;
  }


  public Map<String, Integer> getWordToNum() {

    return this.wordToNum;
  }


  public void setWordToNum(Map<String, Integer> wordToNum) {

    this.wordToNum = wordToNum;
  }


  // Clean text line according to given type
  // AND lower case text
  // It is assumed that line is a tokenized sentence
  private String[] cleanTextLine(String line, String type) {

    String[] words = {};
    switch (type) {
      case "ptb":
        words = cleanPTBLine(line);
        break;
      case "eng_web_tbk":
        words = cleanWebTBKLine(line);
        break;
      default:
        words = line.toLowerCase().split(" ");
        break;
    }

    tokenCnt = tokenCnt + words.length;
    return words;
  }


  private String[] cleanPTBLine(String line) {

    String[] words = line.toLowerCase().split(" ");
    return words;
  }


  private String[] cleanWebTBKLine(String line) {

    String[] words = line.toLowerCase().split(" ");
    String[] firstWord = words[0].split(">");
    //for (String x : firstWord) System.out.print(x+" --- ");System.out.println("\n");
    words[0] = firstWord[firstWord.length - 1];
    return words;
  }


  /**
   * Count frequency of words
   * @param words
   */
  public void countWords(String[] words) {

    for (String word : words) {
      if (this.getWordToNum().containsKey(word)) {
        this.getWordToNum().put(word, this.getWordToNum().get(word) + 1);
      } else {
        this.getWordToNum().put(word, 1);
      }
    }
  }


  /**
   * Prints size of hash.
   */
  public void printWordNumSize() {

    System.out.println("Token: " + tokenCnt + " Types: " + this.wordToNum.size());
  }


  /**
   * <li> iterate through input file linewise
   * <li> clean line according to given type (depends on corpus encoding)
   * <li> count words according to term frequency
   * @param fileName
   * @param type
   * @param max
   */
  public void readAndProcessInputTextLineWise(String fileName, String type, int max) {

    BufferedReader reader;
    int mod = 100000;
    int myLineCnt = 0;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

      String line;
      while ((line = reader.readLine()) != null) {
        if ((max > 0) && (myLineCnt >= max)) {
          break;
        }
        lineCnt++;
        myLineCnt++;
        if (!line.isEmpty()) {
          String[] words = cleanTextLine(line, type);
          countWords(words);
        }
        if ((lineCnt % mod) == 0) {
          System.out.println(lineCnt);
        }
      }
      System.out.println("+++" + myLineCnt);
      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // Sort wordToNum according to value in decreasing order
  // transform hashmap to treemap by using ValueComparator
  private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {

    Map<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(unsortedMap));
    sortedMap.putAll(unsortedMap);
    return sortedMap;
  }


  // This method helps subsampling based on Thomas Mikolov
  private Map<String, Integer> subSamplingEntries(double threshold) {

    Map<String, Integer> newMap = new TreeMap<String, Integer>();
    for (Map.Entry<String, Integer> entry : this.getWordToNum().entrySet()) {
      String key = entry.getKey();
      Integer value = entry.getValue();
      double p = 1 - Math.sqrt(threshold / value);
      if ((1 - p) > threshold) {
        //System.out.println("Key: " + key + " Value: " + value + " p: " + p);
        newMap.put(key, value);
      }
    }
    return newMap;
  }


  public void postProcessWords(double threshold) {

    this.setWordToNum(this.subSamplingEntries(threshold));
    this.setWordToNum(sortByValue(this.getWordToNum()));
  }


  private void writeSortedMap(int n, BufferedWriter writer) {

    int cnt = 1;
    //Map<String, Integer> sortedMap = sortByValue(wordToNum);
    try {
      writer.write(tokenCnt + "\t" + this.getWordToNum().size());
      for (Map.Entry<String, Integer> entry : this.getWordToNum().entrySet()) {
        writer.write("\n" + entry.getKey() + "\t" + entry.getValue());
        if (cnt == n) {
          break;
        }
        cnt++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void writeSortedIndicatorWords(String targetFileName, int cnt) {

    File file = new File(targetFileName);
    file.getParentFile().mkdirs();
    BufferedWriter writer;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
      this.writeSortedMap(cnt, writer);

      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * The following two functions are used to create word vectors from a list of relevant source files defined in Corpus
   * Class for the selected tagger with name taggerName.
   * @param corpus
   */
  private void readUnlabeledDataFromCorpus(Corpus corpus) {

    for (String fileName : corpus.getTrainingUnLabeledData()) {
      System.out.println(fileName);
      // read in first 100.000 sentences from each file
      readAndProcessInputTextLineWise(fileName, "ptb", 100000);
    }
    for (String fileName : corpus.getDevUnLabeledData()) {
      System.out.println(fileName);
      // read in first 100.000 sentences from each file
      readAndProcessInputTextLineWise(fileName, "ptb", 100000);
    }
    for (String fileName : corpus.getTestUnLabeledData()) {
      System.out.println(fileName);
      // read in first 100.000 sentences from each file
      readAndProcessInputTextLineWise(fileName, "ptb", 100000);
    }
  }


  public void createIndicatorTaggerNameWordsFromCorpus(Corpus corpus) {

    this.readUnlabeledDataFromCorpus(corpus);
  }


  public void createAndWriteIndicatorTaggerNameWordsFromCorpus(Archivator archivator, String taggerName, Corpus corpus,
      double subSamplingThreshold) {

    String iwFilename = this.getFeatureFilePathname() + taggerName + "/iw_all.txt";
    System.out.println("Create indictor words and save in file: " + iwFilename);
    IndicatorWordsCreator iwp = new IndicatorWordsCreator();
    iwp.createIndicatorTaggerNameWordsFromCorpus(corpus);

    iwp.postProcessWords(subSamplingThreshold);
    iwp.writeSortedIndicatorWords(iwFilename, 10000);
    // Add file to archivator
    archivator.getFilesToPack().add(iwFilename);
  }
}
