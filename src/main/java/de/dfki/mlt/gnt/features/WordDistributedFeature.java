package de.dfki.mlt.gnt.features;

import java.util.Map;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class WordDistributedFeature {

  // leftContext and rightContext both corresponds to a vector constructed from the indicator words
  // leftContext[0] == indicator word with rank 1
  // leftContext[n] == indicator word with rank n+1
  // n == rank n+1 - 1
  // leftContext[n+1] counts for non-indicator context elements

  // The cells keep the frequency information which is finally transformed to a weight by computing
  // 1+log(freq); this is why I am using double
  private double[] leftContext;
  private double[] rightContext;


  WordDistributedFeature(int n) {

    this.leftContext = new double[n + 1];
    this.rightContext = new double[n + 1];
  }


  public WordDistributedFeature(int n, int leftWordIndex, int rightWordIndex) {

    // when a new word vector is initialized, all elements should be initialized with 0
    // -> in java the default
    this.leftContext = new double[n + 1];
    this.rightContext = new double[n + 1];
    this.updateWordVector(leftWordIndex, rightWordIndex);
  }


  public double[] getLeftContext() {

    return this.leftContext;
  }


  public void setLeftContext(double[] leftContext) {

    this.leftContext = leftContext;
  }


  public double[] getRightContext() {

    return this.rightContext;
  }


  public void setRightContext(double[] rightContext) {

    this.rightContext = rightContext;
  }


  public void updateWordVector(int leftWordIndex, int rightWordIndex) {

    // increment frequencies
    this.leftContext[leftWordIndex] = this.leftContext[leftWordIndex] + 1;
    this.rightContext[rightWordIndex] = this.rightContext[rightWordIndex] + 1;
  }


  private void computeWeights(double[] context) {

    for (int i = 0; i < context.length; i++) {
      // for rounding Math.floor((1 + Math.log(context[i]))*1000)/1000;
      if (context[i] != 0.0) {
        context[i] = (1 + Math.log(context[i]));
      }
    }
  }


  public void computeContextWeights() {

    this.computeWeights(this.leftContext);
    this.computeWeights(this.rightContext);
  }


  public void initializeContext(String[] contextVector, String direction) {

    // Insert non-zero weights for given index
    for (int i = 0; i < contextVector.length; i++) {
      String[] indexWeightPair = contextVector[i].split(":");
      int index = Integer.parseInt(indexWeightPair[0]);
      double weight = Double.parseDouble(indexWeightPair[1]);
      if (direction.equals("left")) {
        this.leftContext[index] = weight;
      } else if (direction.equals("right")) {
        this.rightContext[index] = weight;
      }
    }
    // Finally set weights of not seen index to zero
    // BASICALLY I assume that 0 is the default !
  }


  /**
   * This is a self-made function that concatenates the left and right vector to a single one
   *
   * @return
   */
  // TODO Could also be done offline, finally, and then left and right could be deleted
  public double[] concatenateLeftAndRightVector() {

    double[] lrVector = new double[this.getLeftContext().length + this.getRightContext().length];
    for (int i = 0; i < this.getLeftContext().length; i++) {
      lrVector[i] = this.getLeftContext()[i];
    }
    for (int j = this.getLeftContext().length + 1; j < lrVector.length; j++) {
      lrVector[j] = this.getLeftContext()[lrVector.length - j];
    }
    return lrVector;
  }


  public String toLeftContext() {

    String outputString = "";
    for (int i = 0; i < this.leftContext.length; i++) {
      outputString = outputString + this.leftContext[i] + "\t";
    }
    return outputString;
  }


  public String toRightContext() {

    String outputString = "";
    for (int i = 0; i < this.rightContext.length; i++) {
      outputString = outputString + this.rightContext[i] + "\t";
    }
    return outputString;
  }


  public String toLeftContextIndex() {

    String outputString = "";
    for (int i = 0; i < this.leftContext.length; i++) {
      if (this.leftContext[i] != 0) {
        outputString = outputString + i + ":" + this.leftContext[i] + "\t";
      }
    }
    return outputString;
  }


  public String toRightContextIndex() {

    String outputString = "";
    for (int i = 0; i < this.rightContext.length; i++) {
      if (this.rightContext[i] != 0) {
        outputString = outputString + i + ":" + this.rightContext[i] + "\t";
      }
    }
    return outputString;
  }


  @Override
  public String toString() {

    String outputString = "";
    for (int i = 0; i < this.leftContext.length; i++) {
      outputString = outputString + (i + 1) + "\t";
    }
    outputString = outputString + "\n";
    for (int i = 0; i < this.leftContext.length; i++) {
      outputString = outputString + this.leftContext[i] + "\t";
    }
    outputString = outputString + "\n";
    for (int i = 0; i < this.rightContext.length; i++) {
      outputString = outputString + this.rightContext[i] + "\t";
    }
    outputString = outputString + "\n";
    return outputString;
  }


  public String toStringEncoded(Map<Integer, String> num2iw) {

    String outputString = "";
    for (int i = 0; i < this.leftContext.length; i++) {
      outputString = outputString + num2iw.get(i + 1) + "\t";
    }
    outputString = outputString + "\n";
    for (int i = 0; i < this.leftContext.length; i++) {
      outputString = outputString + this.leftContext[i] + "\t";
    }
    outputString = outputString + "\n";
    for (int i = 0; i < this.rightContext.length; i++) {
      outputString = outputString + this.rightContext[i] + "\t";
    }
    outputString = outputString + "\n";
    return outputString;
  }
}
