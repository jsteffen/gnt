package data;

import java.util.ArrayList;
import java.util.List;

import archive.Archivator;

public class Data {

  private SetIndexMap wordSet = new SetIndexMap();
  private SetIndexMap labelSet = new SetIndexMap();
  private Sentence sentence = new Sentence();
  private int sentenceCnt = 0;
  private List<Window> instances = new ArrayList<Window>();
  private String labelMapFileName = null;
  private String wordMapFileName = null;
  
  // counted from 0, 2nd column in conll in case of POS, else 0 for NER
  /**
   * Index of wordform in conll format
   */
  public static int wordFormIndex = 1;
  // counted from 0, 5th column in conll
  public static int posTagIndex = 4;
  
  // Setters and getters

  public List<Window> getInstances() {
    return instances;
  }
  public void setInstances(List<Window> instances) {
    this.instances = instances;
  }
  public int getSentenceCnt() {
    return sentenceCnt;
  }
  public void setSentenceCnt(int sentenceCnt) {
    this.sentenceCnt = sentenceCnt;
  }
  public SetIndexMap getWordSet() {
    return wordSet;
  }
  public void setWordSet(SetIndexMap wordSet) {
    this.wordSet = wordSet;
  }
  public SetIndexMap getLabelSet() {
    return labelSet;
  }
  public void setLabelSet(SetIndexMap labelSet) {
    this.labelSet = labelSet;
  }
  public Sentence getSentence() {
    return sentence;
  }
  public void setSentence(Sentence sentence) {
    this.sentence = sentence;
  }
  public String getLabelMapFileName() {
    return labelMapFileName;
  }
  public void setLabelMapFileName(String labelMapFileName) {
    this.labelMapFileName = labelMapFileName;
  }
  
  public String getWordMapFileName() {
    return wordMapFileName;
  }
  public void setWordMapFileName(String wordMapFileName) {
    this.wordMapFileName = wordMapFileName;
  }
  
  // Instances
  public Data(){
    
  }
  public Data(String featureFilePathname, String taggerName) {
    labelMapFileName = featureFilePathname+taggerName+"/labelSet.txt";
    wordMapFileName = featureFilePathname+taggerName+"/wordSet.txt";
  }
  
  // Methods

  
  private int updateWordMap(String word) {
    return this.getWordSet().updateSetIndexMap(word);
  }
  private int updateLabelMap(String label) {
    return this.getLabelSet().updateSetIndexMap(label);
  }

  /**
   * If all conll lines of a sentence have been collected
   * extract the relevant information (here word and pos)
   * and make a sentence object of it (two parallel int[];)
   * as a side effect, word and pos SetIndexMaps are created
   * and stored in the data object
   * @param tokens
   */
  public void generateSentenceObjectFromConllLabeledSentence(List<String[]> tokens) {
    // tokens are of form
    // "1  The  The  DT  DT  _  2  NMOD"
    // NOTE: No lower case here of word
    Sentence sentence = new Sentence(tokens.size());
    for (int i=0; i < tokens.size(); i++){
      // Extract word and pos from conll sentence, create index for both
      // and create sentence using word/pos index
      sentence.addNextToken(i,
          updateWordMap(tokens.get(i)[Data.wordFormIndex]),
          updateLabelMap(tokens.get(i)[Data.posTagIndex])
          );
    }
    this.setSentence(sentence);
    this.sentenceCnt++;
  }
  
  /**
   * tokens are a list of words in form of conll strings.
   * - the words are unlabeled
   * - No lower case here of word
   * - Using a dummy POS "UNK" encoded as -1
   * @param tokens
   */
  public void generateSentenceObjectFromConllUnLabeledSentence(List<String[]> tokens) {
    Sentence sentence = new Sentence(tokens.size());
    for (int i=0; i < tokens.size(); i++){
      // Extract word and pos from conll sentence, create index for both
      // and create sentence using word/pos index
      sentence.addNextToken(i,
          updateWordMap(tokens.get(i)[Data.wordFormIndex]),
          -1);
    }
    this.setSentence(sentence);
    this.sentenceCnt++;
  }

  /**
   * tokens are a vector of words in form of strings.
   * - the words are unlabeled
   * - No lower case here of word
   * - Using a dummy POS "UNK" encoded as -1
   * @param tokens
   */
  public void generateSentenceObjectFromUnlabeledTokens(String[] tokens){
    Sentence sentence = new Sentence(tokens.length);
    for (int i=0; i < tokens.length; i++){
      // tokens are strings
      // NOTE: No lower case here of word
      // Using a dummy POS -1
      sentence.addNextToken(i,
          updateWordMap(tokens[i]), 
          -1);
    }
    this.setSentence(sentence);
    this.sentenceCnt++;

  }

  public void cleanWordSet(){
    wordSet = new SetIndexMap();
  }
  public void cleanLabelSet(){
    labelSet = new SetIndexMap();
  }
  
  public void cleanInstances(){
    instances = new ArrayList<Window>();
  }
  
  public void saveLabelSet(){
    this.getLabelSet().writeSetIndexMap(this.getLabelMapFileName());  
  }

  public void readLabelSet(){
    this.getLabelSet().readSetIndexMap(this.getLabelMapFileName());  
  }
  
  public void readLabelSet(Archivator archivator){
    this.getLabelSet().readSetIndexMap(archivator, this.getLabelMapFileName());  
  }
  
  public void saveWordSet(){
    this.getWordSet().writeSetIndexMap(this.getWordMapFileName());  
  }

  public void readWordSet(){
    this.getWordSet().readSetIndexMap(this.getWordMapFileName());  
  }
  
  public void readWordSet(Archivator archivator){
    this.getWordSet().readSetIndexMap(archivator, this.getWordMapFileName());  
  }

  public String toString(){
    String output = "";
    output += "Sentences: " + this.sentenceCnt +
        " words: " + this.getWordSet().getLabelCnt() +
        " labels: " + this.getLabelSet().getLabelCnt() + "\n";
    return output;
  }


}
