package de.dfki.mlt.gnt.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.GlobalConfig;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class Data {

  private Set<String> wordSet = new HashSet<>();
  private SetIndexMap labelSet = new SetIndexMap();
  private int sentenceCnt = 0;
  private List<Window> instances = new ArrayList<Window>();
  private String labelMapFileName;
  private String wordMapFileName;


  public Data() {

    this.labelMapFileName = "labelSet.txt";
    this.wordMapFileName = "wordSet.txt";
  }


  public List<Window> getInstances() {

    return this.instances;
  }


  public int getSentenceCnt() {

    return this.sentenceCnt;
  }


  public void setSentenceCnt(int sentenceCnt) {

    this.sentenceCnt = sentenceCnt;
  }


  public Set<String> getWordSet() {

    return this.wordSet;
  }


  public SetIndexMap getLabelSet() {

    return this.labelSet;
  }


  public String getWordMapFileName() {

    return this.wordMapFileName;
  }


  /**
   * If all conll lines of a sentence have been collected
   * extract the relevant information (here word and pos)
   * and make a sentence object of it (two parallel int[];)
   * as a side effect, word and pos SetIndexMaps are created
   * and stored in the data object
   * <p>
   * Used in training and evaluation only
   *
   * @param tokens
   */
  public Sentence generateSentenceObjectFromConllLabeledSentence(
      List<String[]> tokens, int wordFormIndex, int tagIndex) {

    // tokens are of form
    // "1  The  The  DT  DT  _  2  NMOD"
    // NOTE: No lower case here of word
    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // Extract word and pos from conll sentence, create index for both
      // and create sentence using word/pos index
      String token = tokens.get(i)[wordFormIndex];
      String tag = tokens.get(i)[tagIndex];
      newSentence.addNextToken(i, token, tag);
      this.wordSet.add(token);
      this.labelSet.addLabel(tag);
    }
    this.sentenceCnt++;
    return newSentence;
  }


  /**
   * Tokens are a vector of words in form of strings.
   * <li> the words are unlabeled
   * <li> No lower case here of word
   * <li> Using a dummy POS "UNK" encoded as -1
   * <p>
   * Used in tagging only
   *
   * @param tokens
   */
  public Sentence generateSentenceObjectFromUnlabeledTokens(List<String> tokens) {

    Sentence newSentence = new Sentence(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      // tokens are strings
      // NOTE: No lower case here of word
      // Using a dummy tag null
      String token = tokens.get(i);
      newSentence.addNextToken(i, token, null);
    }
    this.sentenceCnt++;
    return newSentence;
  }


  public void cleanInstances() {

    this.instances = new ArrayList<Window>();
  }


  public void saveLabelSet() {

    this.labelSet.write(GlobalConfig.getModelBuildFolder().resolve(this.labelMapFileName));
  }


  public void readLabelSet() {

    this.labelSet.readFromPath(GlobalConfig.getModelBuildFolder().resolve(this.labelMapFileName));
  }


  public void readLabelSet(Archivator archivator) {

    System.out.println("Load label set from archive: " + this.labelMapFileName);
    this.labelSet.readFromArchive(archivator, this.labelMapFileName);
  }


  public void saveWordSet() {

    List<String> sortedWordSet = new ArrayList<>(this.wordSet);
    sortedWordSet.sort(null);

    try {
      Path wordSetPath = GlobalConfig.getModelBuildFolder().resolve(this.wordMapFileName);
      Files.createDirectories(wordSetPath.getParent());
      try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
          wordSetPath, StandardCharsets.UTF_8))) {
        for (String oneWord : sortedWordSet) {
          out.println(oneWord);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  public void readWordSet() {

    Path wordSetPath = GlobalConfig.getModelBuildFolder().resolve(this.wordMapFileName);
    try (BufferedReader in = Files.newBufferedReader(wordSetPath, StandardCharsets.UTF_8)) {
      read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void readWordSet(Archivator archivator) {

    System.out.println("\n++++\nLoad known vocabulary from archive: " + this.wordMapFileName);
    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(archivator.getInputStream(this.wordMapFileName), "UTF-8"))) {
      read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void read(BufferedReader in)
      throws IOException {

    String line;
    while ((line = in.readLine()) != null) {
      this.wordSet.add(line);
    }
  }


  @Override
  public String toString() {

    String output = "";
    output += "Sentences: " + this.sentenceCnt
        + " words: " + this.wordSet.size()
        + " labels: " + this.labelSet.size() + "\n";
    return output;
  }
}
