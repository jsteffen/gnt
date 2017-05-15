package data;

public class Sentence {

  private int[] wordArray = new int[0];
  private int[] labelArray = new int[0];


  // Instance

  public Sentence() {
  }


  public Sentence(int size) {
    this.wordArray = new int[size];
    this.labelArray = new int[size];
  }


  // Setters and getters
  public int[] getWordArray() {

    return this.wordArray;
  }


  public void setWordArray(int[] wordArray) {

    this.wordArray = wordArray;
  }


  public int[] getLabelArray() {

    return this.labelArray;
  }


  public void setLabelArray(int[] labelArray) {

    this.labelArray = labelArray;
  }


  public void addNextToken(int i, int wordIndex, int posIndex) {

    this.getWordArray()[i] = wordIndex;
    this.getLabelArray()[i] = posIndex;
  }


}
