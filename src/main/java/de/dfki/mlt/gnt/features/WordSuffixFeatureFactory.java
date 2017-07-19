package de.dfki.mlt.gnt.features;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.GlobalConfig;

/**
 * Goal is to compute all lower-case suffixes from a training set of words.
 * For suffix s, we set the dimension corresponding to s in f_suffix(w) to 1 if lowercased w ends in s
 * and to 0 otherwise. Note that w is a suffix of itself.
 * <p>In FLORS: 91,161 suffix features from PTB - I guess - and PTB as about 32.500 words.
 * As far as I understand, a word should match just one suffix, so we have a very sparse vector in principle
 * but we can represent it for each word good, if we only have non-zero feature values.
 * For training it means we can determine, which suffix is particular for some POS tag, and for testing, we
 * simple compute it.
 * <p>
 * Current result when using PTB version three: #word: 32491 #suffixes: 98358
 * I have different numbers. I do not know how how exactly Flors defines word, but so maybe they have some filters.
 * I will use it, e.g., no number tokens or other specialized tokens.
 * I filter string which starts and ends with a digit.
 * This gives: #word: 28481 #suffixes: 91144
 * <p>
 * NOTE: in principle: file operation not really needed here, because will be later done in one training class
 *
 * @author Günter Neumann, DFKI
 */
public class WordSuffixFeatureFactory {

  // A simple flag for switching between suffix and ngram computation
  private static boolean ngram = false;
  private static int ngramSize = 3;

  // If true, then compute all substrings, else all suffixes
  private static final boolean subString = false;

  // stores indicator word -> rank -> is needed when computing the left/right bigrams of a word
  private Map<String, Integer> suffix2num = new HashMap<String, Integer>();
  // stores rank -> indicator word -> is needed for indexing the context vectors using index rank-1

  // later only used for ppPrint
  private Map<Integer, String> num2suffix = new TreeMap<Integer, String>();

  private int wordCnt = 0;
  private int suffixCnt = 0;


  public WordSuffixFeatureFactory() {
  }


  public static boolean isNgram() {

    return ngram;
  }


  public static void setNgram(boolean ngram) {

    WordSuffixFeatureFactory.ngram = ngram;
  }


  public static int getNgramSize() {

    return ngramSize;
  }


  public static void setNgramSize(int ngramSize) {

    WordSuffixFeatureFactory.ngramSize = ngramSize;
  }


  public Map<String, Integer> getSuffix2num() {

    return this.suffix2num;
  }


  public void setSuffix2num(Map<String, Integer> suffix2num) {

    this.suffix2num = suffix2num;
  }


  public Map<Integer, String> getNum2suffix() {

    return this.num2suffix;
  }


  public void setNum2suffix(Map<Integer, String> num2suffix) {

    this.num2suffix = num2suffix;
  }


  public int getWordCnt() {

    return this.wordCnt;
  }


  public void setWordCnt(int wordCnt) {

    this.wordCnt = wordCnt;
  }


  public int getSuffixCnt() {

    return this.suffixCnt;
  }


  public void setSuffixCnt(int suffixCnt) {

    this.suffixCnt = suffixCnt;
  }


  public void clean() {

    this.num2suffix.clear();
  }


  // ************************** Inserting or Updating extracted suffix/ngram **************************

  /**
   * A number is a string which starts and ends with a digit.
   * This is used to filter out strings for which we do not want to compute suffixes, e.g., numbers
   *
   * @param word
   * @return
   */
  private boolean isNumber(String word) {

    char lastChar = word.charAt(word.length() - 1);
    char firstChar = word.charAt(0);
    return (Character.isDigit(lastChar)
        && Character.isDigit(firstChar));
  }
  // ************************** Inserting or Updating extracted suffix/ngram **************************


  private boolean hasLastNonLetter(String word) {

    char lastChar = word.charAt(word.length() - 1);
    return !Character.isLetter(lastChar);
  }
  // ************************** Inserting or Updating extracted suffix/ngram **************************


  private boolean hasOnlyNonLetters(String token) {

    boolean isValid = true;
    for (int i = 0; i < token.length(); i++) {
      char curChar = token.charAt(i);
      if (Character.isLetter(curChar)) {
        isValid = false;
        break;
      }
    }
    return isValid;
  }
  // ************************** Inserting or Updating extracted suffix/ngram **************************


  /**
   * Returns true if token is not a word.
   * @param token
   * @return
   */
  private boolean isNonWord(String token) {

    return (false
        //        (token.length() < 3) ||
        //         hasLastNonLetter(token)
        //                || hasOnlyNonLetters(token)
        //                || isNumber(token)
        );
  }
  // ************************** Inserting or Updating extracted suffix/ngram **************************


  // parameter i is just used as a flag for increasing the word counter
  private void updateSuffixTable(String suffix, int i) {

    if (!this.getSuffix2num().containsKey(suffix)) {
      if (i == 0) {
        this.wordCnt++;
      }

      this.suffixCnt++;
      this.getSuffix2num().put(suffix, this.suffixCnt);
      this.num2suffix.put(this.suffixCnt, suffix);
    }
  }


  //*********************** Computation of ngrams ***********************
  /**
   * Compute ngrams from given word
   */
  private Set<String> generateNgrams(String word, int gramSize) {

    String realWord = "$$" + word + "$$";
    int start = 0;
    int end = start + gramSize - 1;
    Set<String> ngrams = new HashSet<String>();

    while (end < realWord.length()) {
      ngrams.add(realWord.substring(start, end + 1));
      start++;
      end++;
    }
    return ngrams;
  }


  public List<Integer> getAllKnownNgramsForWord(String word) {

    List<Integer> indices = new ArrayList<Integer>();
    Set<String> ngrams = this.generateNgrams(word, WordSuffixFeatureFactory.ngramSize);
    for (String oneNgram : ngrams) {
      if (!isNonWord(oneNgram)) {
        if (this.getSuffix2num().containsKey(oneNgram)) {
          indices.add(this.getSuffix2num().get(oneNgram));
        }
      }
    }
    indices.sort(null);
    return indices;
  }


  private void computeNgramsAndStore(String word) {

    int i = 0;
    Set<String> ngrams = this.generateNgrams(word, WordSuffixFeatureFactory.ngramSize);
    for (String oneNgram : ngrams) {
      if (!isNonWord(oneNgram)) {
        updateSuffixTable(oneNgram, i);
      }
      i++;
    }
  }


  //*********************** Computation of suffixes ***********************

  /**
   * Given a word, find all matching suffixes from the known suffix list.
   * Used in {@link features.WordSuffixFeatureFactory#getAllKnownSuffixForWord(String)}
   * @param word
   * @return
   */

  private List<Integer> getAllKnownSuffixForWordIntern(String word) {

    List<Integer> indices = new ArrayList<Integer>();
    for (int i = 0; i < word.length(); i++) {
      String suffix = word.substring(i);
      if (!isNonWord(suffix)) {
        if (this.getSuffix2num().containsKey(suffix)) {
          indices.add(this.getSuffix2num().get(suffix));
        }
      }
    }
    indices.sort(null);
    return indices;
  }


  /**
   * Compute all suffixes of a word starting from 0, which means the word is a suffix of itself.
   * If suffix is not a word, then do not store it.
   * @param word
   */
  private void computeSuffixesAndStore(String word) {

    // Smallest suffix is just last character of a word
    for (int i = 0; i < word.length(); i++) {
      String suffix = word.substring(i);
      if (!isNonWord(suffix)) {
        updateSuffixTable(suffix, i);
      }
    }
  }

  //*********************** Computation of substring ***********************

  // Only triggered if substring = true;

  // TODO: these two methods are used to create all possible substrings and to use them
  // instead of all suffixes


  // Currently not used, because not clear whether it is properly implemented
  private List<Integer> getAllKnownSubstringsForWordIntern(String word) {

    List<Integer> indices = new ArrayList<Integer>();
    for (int i = 0; i < word.length(); i++) {
      for (int j = i + 1; j <= word.length(); j++) {
        String substring = word.substring(i, j);
        if (!isNonWord(substring)) {
          if (this.getSuffix2num().containsKey(substring)) {
            int index = this.getSuffix2num().get(substring);
            // to run GNT technically, I need this test, but I do not know why
            if (indices.contains(index)) {
              indices.add(index);
            }
          }
        }
      }
    }
    indices.sort(null);
    return indices;
  }


  // Triggered if substring=true;
  private void computeAllSubstringsAndStore(String word) {

    for (int i = 0; i < word.length(); i++) {
      for (int j = i + 1; j <= word.length(); j++) {
        String substring = word.substring(i, j);
        if (!isNonWord(substring)) {
          updateSuffixTable(substring, i);
        }
      }
    }
  }

  // ************************** Inserting or Updating extracted suffix/ngram **************************


  //*********************** generic caller ***********************

  /**
   * Receives a list of token, and computes suffixes for each token.
   * @param words
   */
  private void computeSubstringsFromWords(String[] words) {

    for (String word : words) {
      if (WordSuffixFeatureFactory.ngram) {
        computeNgramsAndStore(word);
      } else {
        if (WordSuffixFeatureFactory.subString) {
          this.computeAllSubstringsAndStore(word);
        } else {
          computeSuffixesAndStore(word);
        }
      }
    }
  }


  /**
   * Main caller that switches between suffix computation and ngram computation
   * @param word
   * @return
   */
  //TODO after adding also ngrams, indices must be sorted again!!
  public List<Integer> getAllKnownSubstringsForWord(String word) {

    List<Integer> indices = new ArrayList<Integer>();
    if (WordSuffixFeatureFactory.ngram) {
      indices = getAllKnownNgramsForWord(word);
    } else {
      if (WordSuffixFeatureFactory.subString) {
        indices = getAllKnownSubstringsForWordIntern(word);
      } else {
        indices = getAllKnownSuffixForWordIntern(word);
      }
    }
    return indices;
  }


  //*********************** creating and storing ***********************

  public void createAndSaveSuffixFeatures(List<String> trainingFileNames) {

    for (String oneTrainingFileName : trainingFileNames) {
      String trainingSentsFileName = oneTrainingFileName.split("\\.conll")[0] + "-sents.txt";
      System.out.println("Create suffix list from: " + trainingSentsFileName);
      this.createSuffixListFromFile(trainingSentsFileName, -1);

      System.out.println("#word: " + this.getWordCnt() + " #suffixes: " + this.getSuffixCnt());
    }

    Path suffixPath = GlobalConfig.getModelBuildFolder().resolve("suffixList.txt");
    System.out.println("Writing suffix list to: " + suffixPath);
    this.writeSuffixFile(suffixPath);
    System.out.println("... done");
  }


  private void createSuffixListFromFile(String file, int max) {

    BufferedReader reader;
    int lineCnt = 0;
    int mod = 10000;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

      // Each line consists of a sequence of words
      String line;
      while ((line = reader.readLine()) != null) {
        if ((max > 0) && (lineCnt >= max)) {
          break;
        }
        lineCnt++;
        // lower case line and split off words
        String[] words = line.toLowerCase().split(" ");
        // then compute suffixes
        computeSubstringsFromWords(words);
        if ((lineCnt % mod) == 0) {
          System.out.println(lineCnt);
        }
      }
      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // after the above has been done, write out vocabulary into files:
  // Firstly, sort num2word according to natural order, and write value of entry key.
  private void writeSuffixFile(Path targetPath) {

    try {
      Files.createDirectories(targetPath.getParent());
      try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
          targetPath, StandardCharsets.UTF_8))) {
        for (int key : this.num2suffix.keySet()) {
          out.println(this.num2suffix.get(key));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // read preprocessed suffix list from file
  private void readSuffixFile(Path path) {

    try (BufferedReader in = Files.newBufferedReader(
        path, StandardCharsets.UTF_8)) {
      String line;
      int cnt = 1;
      while ((line = in.readLine()) != null) {
        this.getSuffix2num().put(line, cnt);
        this.getNum2suffix().put(cnt, line);
        cnt++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void readSuffixFile(Archivator archivator, String suffixFileName) {

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(archivator.getInputStream(suffixFileName), "UTF-8"))) {
      int cnt = 1;
      String line;
      while ((line = reader.readLine()) != null) {
        this.getSuffix2num().put(line, cnt);
        this.getNum2suffix().put(cnt, line);
        cnt++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readSuffixList() {

    Path suffixPath = GlobalConfig.getModelBuildFolder().resolve("suffixList.txt");
    System.out.println("Reading suffix list from: " + suffixPath);
    this.readSuffixFile(suffixPath);
    System.out.println("... done");
  }


  public void readSuffixList(Archivator archivator) {

    String suffixFileName = "suffixList.txt";
    System.out.println("Reading suffix list from archive: " + suffixFileName);
    this.readSuffixFile(archivator, suffixFileName);
    System.out.println("... done");
  }


  private void computeSuffixesTest(String word) {

    int cnt = 0;
    for (int i = 0; i < word.length(); i++) {
      for (int j = i + 1; j <= word.length(); j++) {
        String substring = word.substring(i, j);
        System.out.println("substring: " + substring + " cnt: " + cnt);
        if (!isNonWord(substring)) {
          // do nothing
        }
        cnt++;
      }
    }
  }


  public static void main(String[] args) {

    WordSuffixFeatureFactory wsf = new WordSuffixFeatureFactory();

    wsf.computeSuffixesTest("Hausmann");
  }
}
