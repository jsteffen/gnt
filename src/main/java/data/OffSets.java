package data;

public class OffSets {

  private int wvLeftSize = -1;
  private int wvRightSize = -1;
  private int suffixSize = -1;
  private int shapeSize = -1;
  private int clusterIdSize = -1;
  private int tokenVectorSize = -1;
  private int windowVectorSize = -1;
  private int labelVectorSize = -1;


  public int getLabelVectorSize() {

    return this.labelVectorSize;
  }


  public void setLabelVectorSize(int labelVectorSize) {

    this.labelVectorSize = labelVectorSize;
  }


  public int getWvLeftSize() {

    return this.wvLeftSize;
  }


  public void setWvLeftSize(int wvLeftSize) {

    this.wvLeftSize = wvLeftSize;
  }


  public int getWvRightSize() {

    return this.wvRightSize;
  }


  public void setWvRightSize(int wvRightSize) {

    this.wvRightSize = wvRightSize;
  }


  public int getSuffixSize() {

    return this.suffixSize;
  }


  public void setSuffixSize(int suffixSize) {

    this.suffixSize = suffixSize;
  }


  public int getShapeSize() {

    return this.shapeSize;
  }


  public void setShapeSize(int shapeSize) {

    this.shapeSize = shapeSize;
  }


  public int getClusterIdSize() {

    return this.clusterIdSize;
  }


  public void setClusterIdSize(int clusterIdSize) {

    this.clusterIdSize = clusterIdSize;
  }


  public int getTokenVectorSize() {

    return this.tokenVectorSize;
  }


  public void setTokenVectorSize(int tokenVectorSize) {

    this.tokenVectorSize = tokenVectorSize;
  }


  public int getWindowVectorSize() {

    return this.windowVectorSize;
  }


  public void setWindowVectorSize(int windowVectorSize) {

    this.windowVectorSize = windowVectorSize;
  }


  // This is the length of the feature vector of a window element, this is constant, as well is the window vector size

  public void initializeOffsets(Alphabet alphabet, Data data, int windowSize) {

    this.wvLeftSize = (alphabet.isWithWordFeats()) ? alphabet.getWordVectorFactory().getIw2num().size() + 1 : 0;
    // plus one for unknown word statistics, cf. features.WordDistributedFeature.WordDistributedFeature(int)
    this.wvRightSize = (alphabet.isWithWordFeats()) ? alphabet.getWordVectorFactory().getIw2num().size() + 1 : 0;
    this.suffixSize = (alphabet.isWithSuffixFeats()) ? alphabet.getWordSuffixFactory().getSuffix2num().size() : 0;
    this.shapeSize = (alphabet.isWithShapeFeats()) ? alphabet.getWordShapeFactory().getSignature2index().size() : 0;
    this.clusterIdSize = (alphabet.isWithClusterFeats()) ? alphabet.getWordClusterFactory().getClusterIdcnt() : 0;
    //TODO: check whether I need an extra 1 for the dummy label
    this.labelVectorSize = (alphabet.isWithLabelFeats()) ? data.getLabelSet().getLabel2num().size() + 1 : 0;
    this.tokenVectorSize =
        (this.wvLeftSize + this.wvRightSize + this.suffixSize + this.shapeSize
            + this.clusterIdSize + this.labelVectorSize);
    this.windowVectorSize = (this.tokenVectorSize * (windowSize * 2 + 1) + 1);
  }


  @Override
  public String toString() {

    String output = "";
    output += "wvLeftSize: " + this.wvLeftSize + "; wvRightSize: " + this.wvLeftSize
        + "; suffixSize: " + this.suffixSize + "; shapSize: " + this.shapeSize
        + "; clusterIDsize: " + this.clusterIdSize + "; labelVectorsize: " + this.labelVectorSize
        + "; total token vector size: " + this.tokenVectorSize
        + "; total window vector size: " + this.windowVectorSize;
    return output;
  }

}
