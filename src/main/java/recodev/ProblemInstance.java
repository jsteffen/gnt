package recodev;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import data.Window;
import de.bwaldvogel.liblinear.FeatureNode;

/**
 * If I understand MDP correctly, then first all training instances are collected
 * in two parallel list yList and xList, where yList keeps the label of instance i, and
 * xList keeps the feature vector of i which is a FeatureNode[];
 * together with the max feature size
 * a problem is actually created;
 * <p>
 * From de.dfki.lt.mdparser.parser.Trainer.constructProblem(List<Integer>, List<FeatureNode[]>, int)
 * problem.y is an array of size problem.l and each element keeps the label index of that training instance i
 * problem.x is a parallel array where each element keeps the FeatureNode[]
 * the size of each  FeatureNode[] depends on non-zero values; each element is a feature node.
 * so, in order to use a similar approach, I would need to collect all labels and feature vectors of
 * the training examples in some variables, and then create the problem.
 * At least, it seems that I cannot do it online without knowing prob.l and prob.n in advance.
 *
 * @author Günter Neumann, DFKI
 */
public class ProblemInstance {

  private FeatureNode[] featureNodes;


  public ProblemInstance() {
  }


  public FeatureNode[] getFeatureNodes() {

    return this.featureNodes;
  }


  public void setfeatureNodes(FeatureNode[] featureNodes) {

    this.featureNodes = featureNodes;
  }


  //HIERIX
  public void createProblemInstanceFromWindow(FeatureMap featureMap) {

    this.setfeatureNodes(new FeatureNode[featureMap.getFeatureMap().size()]);

    int offSet = 0;
    for (Map.Entry<Integer, Double> entry : featureMap.getFeatureMap().entrySet()) {
      Integer key = entry.getKey();
      Double value = entry.getValue();
      this.featureNodes[offSet] = new FeatureNode(key, value);
      offSet++;
    }
  }


  /**
   * Checks whether a feature vector is well-formed
   * wrt. to the definition of liblinear which requires that the features in the vector are in natural order.
   * <p>
   * It is activated when TrainerInMem.debug = true;
   * @param tokenWindow
   */
  private void checkfeatureNodes(Window tokenWindow) {

    int lastValue = 0;
    int fLen = this.featureNodes.length - 1;
    for (int i = 0; i < fLen; i++) {
      FeatureNode x = this.featureNodes[i];
      if (x.getIndex() <= lastValue) {
        System.err.println(tokenWindow.toString());
        throw new IllegalArgumentException("GN: feature nodes must be sorted by index in ascending order: "
            + lastValue + "..." + x.getIndex() + " i= " + i + " value: " + x.getValue());
      }
      lastValue = x.getIndex();
    }
  }


  /**
   * Saves the feature vector of the current window plus its given label directly as liblinear vector
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
    int fLen = this.featureNodes.length - 1;
    for (int i = 0; i < fLen; i++) {
      FeatureNode x = this.featureNodes[i];
      output += x.getIndex() + ":" + x.getValue() + " ";
    }
    output += this.featureNodes[fLen].getIndex()
        + ":" + this.featureNodes[fLen].getValue();
    return output;
  }
}
