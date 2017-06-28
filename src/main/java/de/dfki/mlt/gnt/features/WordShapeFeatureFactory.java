package de.dfki.mlt.gnt.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.GlobalConfig;

/**
 * For each word given, check all shape features and set bit vector map accordingly.
 * <p>Maintain a map of word - bit vector, so that a output file for the training set can be created - really needed ?
 * Anyway, processing PTB should give me 50 different bit vectors instances (or signatures).
 * -> Basically I get only 49 !
 * <p>
 * NOTE: in principle: file operation not really needed here, because will be later done in one training class.
 * BUT then: take care that words are not lower cased, before calling this.
 *
 * @author Günter Neumann, DFKI
 */
public class WordShapeFeatureFactory {

  // A mapping from word to Shape vector; used as a cache to avoid redundant computation
  // NOTE: need to distinguish whether word is at sentence initial position or not
  // I do this by adding 0 or 1 as key value (but NOT when creating the shape, because otherwise I get wrong results)

  //TODO only used in ppPrint
  private Map<String, WordShapeFeature> word2signature = new HashMap<String, WordShapeFeature>();
  // A mapping from the string of a ShapeVector (signature) to its ShapeVector; also used as cache
  private Map<String, Integer> signature2index = new HashMap<String, Integer>();

  //TODO only used in ppPrint
  private Map<Integer, String> index2signature = new TreeMap<Integer, String>();


  /* process file line wise
   * for each line (sentence) do:
   * - extract words
   * - for each word, compute signature, if not done so
   * - store it in hash
   * - NOTE: before lower casing word (as usual) first check Upercase feature and the like
   */

  // private int wordCnt = 0;
  private int signatureCnt = 0;


  public WordShapeFeatureFactory() {
  }


  public Map<String, WordShapeFeature> getWord2signature() {

    return this.word2signature;
  }


  public void setWord2signature(Map<String, WordShapeFeature> word2signature) {

    this.word2signature = word2signature;
  }


  public Map<String, Integer> getSignature2index() {

    return this.signature2index;
  }


  public void setSignature2index(Map<String, Integer> signature2index) {

    this.signature2index = signature2index;
  }


  public Map<Integer, String> getIndex2signature() {

    return this.index2signature;
  }


  public void setIndex2signature(Map<Integer, String> index2signature) {

    this.index2signature = index2signature;
  }


  public void clean() {

    this.word2signature = new HashMap<String, WordShapeFeature>();
    this.index2signature = new TreeMap<Integer, String>();
  }


  public void createAndSaveShapeFeature(String trainingFileName) {

    System.out.println("Create shape list from: " + trainingFileName);
    this.createShapeVectorsFromFile(trainingFileName, -1);

    Path shapePath = GlobalConfig.getModelBuildFolder().resolve("shapeList.txt");
    System.out.println("Writing shape list to: " + shapePath);
    this.writeShapeFeatureFile(shapePath);
    System.out.println("... done");
  }


  private void createShapeVectorsFromFile(String targetFileName, int max) {

    File file = new File(targetFileName);
    if (file.getParentFile() != null) {
      file.getParentFile().mkdirs();
    }
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
        // split off words -> it will be lower-cased as part of the process that computes the signatures
        String[] words = line.split(" ");
        // then compute suffixes
        computeShapeVectorsFromWords(words);
        if ((lineCnt % mod) == 0) {
          System.out.println(lineCnt);
        }
      }
      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void computeShapeVectorsFromWords(String[] words) {

    int wordIndex = 0;
    for (String word : words) {
      computeShapeVectorAndStore(word, wordIndex);
      determineSignatureType(word, wordIndex);
      wordIndex++;
    }
  }


  /**
   * For a given word and its index, compute its signature string and then
   * determine its integer representation .
   * This means: we assume a fixed set of signatures.
   * @param word
   * @param index
   * @param train
   * @return
   */
  public int getShapeFeature(String word, int index) {

    WordShapeFeature newSignature = new WordShapeFeature(word, index);
    String binaryString = newSignature.getBitVectorString();
    // If signature for word is a known one, return it
    if (this.getSignature2index().containsKey(binaryString)) {
      return this.getSignature2index().get(binaryString);
    } else {
      // signal unknown signature occured and return it as -1
      System.err.println("Unknown signature: " + binaryString);
      return -1;
    }
  }


  // NOTE: I need to take into account the loc value, either 0 or 1 to distinguish the position of a word
  private void determineSignatureType(String wordIn, int wordIndex) {

    String word = (wordIndex == 0) ? wordIn + "0" : wordIn + "1";
    WordShapeFeature wordShapeVector = this.word2signature.get(word);
    if (!this.signature2index.containsKey(wordShapeVector.getBitVectorString())) {
      this.signatureCnt++;
      this.signature2index.put(wordShapeVector.getBitVectorString(), this.signatureCnt);
      this.index2signature.put(this.signatureCnt, wordShapeVector.getBitVectorString());
    }
  }


  // NOTE: I need to take into account the loc value, either 0 or 1 to distinguish the position of a word
  private void computeShapeVectorAndStore(String wordIn, int wordIndex) {

    String word = (wordIndex == 0) ? wordIn + "0" : wordIn + "1";
    if (!this.word2signature.containsKey(word)) {
      // NOTE: here, I need to use the original word for creating its shape !
      WordShapeFeature wordShapeVector = new WordShapeFeature(wordIn, wordIndex);
      // this.wordCnt++;
      //System.out.println("Word: " + word + " and Loc: " + wordIndex + " Sign: " + wordShapeVector.toString());
      this.word2signature.put(word, wordShapeVector);
    }
  }


  public void printSignaturesMap() {

    for (Integer key : this.index2signature.keySet()) {
      System.out.println(key + " " + this.index2signature.get(key));
    }
  }


  public void writeShapeFeatureFile(Path targetPath) {

    try {
      Files.createDirectories(targetPath.getParent());
      try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
          targetPath, StandardCharsets.UTF_8))) {
        for (int key : this.index2signature.keySet()) {
          out.println(this.index2signature.get(key));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void readShapeFeatureFile(Path path) {


    try (BufferedReader in = Files.newBufferedReader(
        path, StandardCharsets.UTF_8)) {
      String line;
      int cnt = 1;
      while ((line = in.readLine()) != null) {
        this.signature2index.put(line, cnt);
        this.index2signature.put(cnt, line);
        cnt++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void readShapeFeatureFile(Archivator archivator, Path path) {

    BufferedReader reader;
    int cnt = 1;
    try {
      InputStream inputStream = archivator.getArchiveMap().get(path.toString());
      reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      String line;
      while ((line = reader.readLine()) != null) {
        this.signature2index.put(line, cnt);
        this.index2signature.put(cnt, line);
        cnt++;
      }
      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readShapeList() {

    Path shapePath = GlobalConfig.getModelBuildFolder().resolve("shapeList.txt");
    System.out.println("Reading shape list from: " + shapePath);
    this.readShapeFeatureFile(shapePath);
    System.out.println("... done");
  }


  public void readShapeList(Archivator archivator) {

    Path shapePath = GlobalConfig.getModelBuildFolder().resolve("shapeList.txt");
    System.out.println("Reading shape list from archive: " + shapePath);
    this.readShapeFeatureFile(archivator, shapePath);
    System.out.println("... done");
  }
}
