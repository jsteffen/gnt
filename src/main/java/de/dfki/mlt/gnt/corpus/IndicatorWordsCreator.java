package de.dfki.mlt.gnt.corpus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.CorpusConfig;
import de.dfki.mlt.gnt.config.GlobalConfig;

/**
 * A indicator word is used to define a dimension of distributed word vectors;
 * an indicator word is selected on basis of its rank.
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

  private static final Logger logger = LoggerFactory.getLogger(IndicatorWordsCreator.class);

  private int lineCnt;
  private int tokenCnt;
  private Map<String, Integer> wordToNum;


  public IndicatorWordsCreator() {

    this.lineCnt = 0;
    this.tokenCnt = 0;
    this.wordToNum = new HashMap<String, Integer>();
  }


  /**
   * @return indicator words statistics
   */
  public String getIndicatorWordStats() {

    return "Token: " + this.tokenCnt + " Types: " + this.wordToNum.size();
  }


  public void createIndicatorTaggerNameWords(CorpusConfig corpusConfig, double subSamplingThreshold) {

    // reset instance
    this.lineCnt = 0;
    this.tokenCnt = 0;
    this.wordToNum.clear();

    Path iwPath = GlobalConfig.getModelBuildFolder().resolve("iw_all.txt");
    logger.info("create indictor words and save them in file: " + iwPath);

    this.readUnlabeledDataFromCorpus(corpusConfig);
    this.postProcessWords(subSamplingThreshold);
    this.writeIndicatorWords(iwPath, 10000);
  }


  /**
   * Creates word vectors from a list of relevant source files defined in given corpus.
   *
   * @param corpus
   */
  private void readUnlabeledDataFromCorpus(CorpusConfig corpusConfig) {

    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.TRAINING_UNLABELED_DATA, Collections.emptyList())) {
      // read in first 100.000 sentences from each file
      readUnlabeledDataFromFile(fileName, "ptb", 100000);
    }
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.DEV_UNLABELED_DATA, Collections.emptyList())) {
      // read in first 100.000 sentences from each file
      readUnlabeledDataFromFile(fileName, "ptb", 100000);
    }
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.TEST_UNLABELED_DATA, Collections.emptyList())) {
      // read in first 100.000 sentences from each file
      readUnlabeledDataFromFile(fileName, "ptb", 100000);
    }
  }


  private void postProcessWords(double threshold) {

    this.wordToNum = subSamplingEntries(threshold);
    this.wordToNum = sortByValue(this.wordToNum);
  }


  /**
   * <li> iterate through input file linewise
   * <li> clean line according to given type (depends on corpus encoding)
   * <li> count words according to term frequency
   *
   * @param fileName
   * @param type
   * @param max
   */
  private void readUnlabeledDataFromFile(String fileName, String type, int max) {

    logger.info("processing " + fileName);
    try (BufferedReader in = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
      int mod = 100000;
      int localLineCnt = 0;
      String line;
      while ((line = in.readLine()) != null) {

        if ((max > 0) && (localLineCnt >= max)) {
          break;
        }
        this.lineCnt++;
        localLineCnt++;
        if (!line.isEmpty()) {
          String[] words = cleanTextLine(line, type);
          this.tokenCnt = this.tokenCnt + words.length;
          updateWordsCount(words);
        }
        if ((this.lineCnt % mod) == 0) {
          logger.info(String.format("total line count: %,d", this.lineCnt));
        }

      }
      logger.info(String.format("%,d lines processed", localLineCnt));
      logger.info(String.format("total line count: %,d", this.lineCnt));
    } catch (IOException e) {
      logger.error(e.getLocalizedMessage(), e);
    }
  }


  private void writeIndicatorWords(Path targetPath, int maxCnt) {

    try {
      Files.createDirectories(targetPath.getParent());
      try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
          targetPath, StandardCharsets.UTF_8))) {
        int cnt = 1;
        out.println(this.tokenCnt + "\t" + this.wordToNum.size());
        for (Map.Entry<String, Integer> entry : this.wordToNum.entrySet()) {
          out.println(entry.getKey() + "\t" + entry.getValue());
          if (cnt == maxCnt) {
            break;
          }
          cnt++;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Update frequency of given words.
   * @param words
   */
  private void updateWordsCount(String[] words) {

    for (String oneWord : words) {
      Integer count = this.wordToNum.get(oneWord);
      if (count != null) {
        this.wordToNum.put(oneWord, count + 1);
      } else {
        this.wordToNum.put(oneWord, 1);
      }
    }
  }


  // This method helps subsampling based on Thomas Mikolov
  private Map<String, Integer> subSamplingEntries(double threshold) {

    Map<String, Integer> newMap = new TreeMap<String, Integer>();
    for (Map.Entry<String, Integer> entry : this.wordToNum.entrySet()) {
      String key = entry.getKey();
      Integer value = entry.getValue();
      double p = 1 - Math.sqrt(threshold / value);
      if ((1 - p) > threshold) {
        logger.debug("Key: " + key + " Value: " + value + " p: " + p);
        newMap.put(key, value);
      }
    }
    return newMap;
  }


  // Clean text line according to given type
  // AND lower case text
  // It is assumed that line is a tokenized sentence
  private static String[] cleanTextLine(String line, String type) {

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

    return words;
  }


  private static String[] cleanPTBLine(String line) {

    String[] words = line.toLowerCase().split(" ");
    return words;
  }


  private static String[] cleanWebTBKLine(String line) {

    String[] words = line.toLowerCase().split(" ");
    String[] firstWord = words[0].split(">");
    words[0] = firstWord[firstWord.length - 1];
    return words;
  }


  // Sort wordToNum according to value in decreasing order
  // transform hashmap to treemap by using ValueComparator
  private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {

    Map<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(unsortedMap));
    sortedMap.putAll(unsortedMap);
    return sortedMap;
  }
}
