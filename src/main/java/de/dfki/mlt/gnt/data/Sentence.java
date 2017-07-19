package de.dfki.mlt.gnt.data;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class Sentence {

  private String[] words;
  private String[] tags;


  public Sentence(int size) {

    this.words = new String[size];
    this.tags = new String[size];
  }


  public String[] getWords() {

    return this.words;
  }


  public String[] getTags() {

    return this.tags;
  }


  public void addNextToken(int i, String word, String tag) {

    this.words[i] = word;
    this.tags[i] = tag;
  }
}
