package trainer;

import java.io.BufferedWriter;
import java.io.IOException;

import data.Pair;
import data.Window;
import de.bwaldvogel.liblinear.FeatureNode;
import features.WordFeatures;

/*
 * If I understand MDP correctly, then first all training instances are collected
 * in two parallel list yList and xList, where yList keeps the label of instance i, and
 * xList keeps the feature vector of i which is a FeatureNode[];
 * together with the max feature size
 * a problem is actually created;
 * From de.dfki.lt.mdparser.parser.Trainer.constructProblem(List<Integer>, List<FeatureNode[]>, int)
 * problem.y is an array of size problem.l and each element keeps the label index of that training instance i
 * problem.x is a parallel array where each element keeps the FeatureNode[]
 * the size of each  FeatureNode[] depends on non-zero values; each element is a feature node.
 * so, in order to use a similar approach, I would need to collect all labels and feature vectors of
 * the training examples in some variables, and then create the problem.
 * At least, it seems that I cannot do it online without knowing prob.l and prob.n in advance.
 */

public class ProblemInstance {

  private static int cumLength = 0;
  private FeatureNode[] featureVector;


  // Instance

  public ProblemInstance() {
  }


  // Setters and getters


  public static int getCumLength() {

    return cumLength;
  }


  public FeatureNode[] getFeatureVector() {

    return this.featureVector;
  }


  public void setFeatureVector(FeatureNode[] featureVector) {

    this.featureVector = featureVector;
  }


  // Methods
  /**
   * Given a tokenWindow (which is a list of Wordfeatures (which each is a list of feature-value pairs)),
   * compute a feature vector which is a naturally ordered enumeration of all feature values nodes of a problem instance
   * @param tokenWindow
   */
  public void createProblemInstanceFromWindow(Window tokenWindow) {

    // This means that the feature vector has size window length
    // and window length is the number of non-zero features with relative feature index and value
    this.setFeatureVector(new FeatureNode[tokenWindow.getWindowLength()]);
    // Add to cumulative length: only needed for computing average length of window
    ProblemInstance.cumLength += this.featureVector.length;

    int offSet = 0;

    for (WordFeatures wordFeats : tokenWindow.getElements()) {
      // Add left word embedding length
      for (int i = 0; i < wordFeats.getLeft().size(); i++) {
        Pair<Integer, Double> pair = wordFeats.getLeft().get(i);
        this.featureVector[offSet + i] = new FeatureNode(pair.getLeft(), pair.getRight());
      }
      offSet += wordFeats.getLeft().size();
      // Add right word embedding length
      for (int i = 0; i < wordFeats.getRight().size(); i++) {
        Pair<Integer, Double> pair = wordFeats.getRight().get(i);
        this.featureVector[offSet + i] = new FeatureNode(pair.getLeft(), pair.getRight());
      }
      offSet += wordFeats.getRight().size();
      // Add shape length
      for (int i = 0; i < wordFeats.getShape().size(); i++) {
        Pair<Integer, Boolean> pair = wordFeats.getShape().get(i);
        this.featureVector[offSet + i] = new FeatureNode(pair.getLeft(), 1);
      }
      offSet += wordFeats.getShape().size();
      // Add suffix length
      for (int i = 0; i < wordFeats.getSuffix().size(); i++) {
        Pair<Integer, Boolean> pair = wordFeats.getSuffix().get(i);
        this.featureVector[offSet + i] = new FeatureNode(pair.getLeft(), 1);
      }
      offSet += wordFeats.getSuffix().size();
      // Add cluster length
      for (int i = 0; i < wordFeats.getCluster().size(); i++) {
        Pair<Integer, Boolean> pair = wordFeats.getCluster().get(i);
        this.featureVector[offSet + i] = new FeatureNode(pair.getLeft(), 1);
      }

      offSet += wordFeats.getCluster().size();
      // Add label length
      for (int i = 0; i < wordFeats.getLabel().size(); i++) {
        Pair<Integer, Boolean> pair = wordFeats.getLabel().get(i);
        this.featureVector[offSet + i] = new FeatureNode(pair.getLeft(), 1);
      }

      offSet += wordFeats.getLabel().size();
    }

    //this.normalizeFeatureVectorToUnitLenght();

    if (TrainerInMem.getDebug()) {
      this.checkFeatureVector(tokenWindow);
    }

  }


  /**
   * TODO
   * This method is used to normaliuze a feature vector. It is yet not used, because
   * not yet clear whether it is correctly defined.
   */
  private void normalizeFeatureVectorToUnitLenght() {

    double vecLength = computeUnitLength();
    for (FeatureNode node : this.featureVector) {
      node.setValue(node.getValue() / vecLength);
    }
  }


  private double computeUnitLength() {

    double vecLength = 0.0;
    for (FeatureNode node : this.featureVector) {
      vecLength += node.getValue() * node.getValue();
    }
    return Math.sqrt(vecLength);
  }


  /**
   * This is a method that checks whether a feature vector is well-formed
   * wrt. to the definition of liblinear which requires that the features in the vector are in natural order.
   * <p>
   * It is activated when TrainerInMem.debug = true;
   * @param tokenWindow
   */
  private void checkFeatureVector(Window tokenWindow) {

    int lastValue = 0;
    int fLen = this.featureVector.length - 1;
    for (int i = 0; i < fLen; i++) {
      FeatureNode x = this.featureVector[i];
      if (x.getIndex() <= lastValue) {
        System.err.println(tokenWindow.toString());
        throw new IllegalArgumentException("GN: feature nodes must be sorted by index in ascending order: "
            + lastValue + "..." + x.getIndex() + " i= " + i + " value: " + x.getValue());
      }
      lastValue = x.getIndex();
    }
  }


  /**
   * This method save the feature vector of the current window plus its given label directly
   * as liblinear vector
   * @param instanceWriter
   * @param labelIndex
   */
  public void saveProblemInstance(BufferedWriter instanceWriter, int labelIndex) {

    try {
      instanceWriter.write(labelIndex + " " + this.toString());
      instanceWriter.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public String toString() {

    String output = "";
    int fLen = this.featureVector.length - 1;
    for (int i = 0; i < fLen; i++) {
      FeatureNode x = this.featureVector[i];
      output += x.getIndex() + ":" + x.getValue() + " ";
    }
    output += this.featureVector[fLen].getIndex()
        + ":" + this.featureVector[fLen].getValue();
    return output;
  }


}
