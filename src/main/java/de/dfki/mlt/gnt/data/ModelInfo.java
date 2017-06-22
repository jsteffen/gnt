package de.dfki.mlt.gnt.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import de.bwaldvogel.liblinear.SolverType;
import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.GlobalConfig;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class ModelInfo {

  private SolverType solver = SolverType.L2R_LR; // -s 0
  private double cost = 1.0; // cost of constraints violation
  private double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less

  private String modelName;
  private String modelFileArchive = "";
  private BufferedWriter modelInputFileWriter = null;


  public ModelInfo() {
  }


  public ModelInfo(String type) {
    if (type.equalsIgnoreCase("FLORS")) {
      this.initFlorsInfo();
    } else if (type.equalsIgnoreCase("MDP")) {
      this.initMDPInfo();
    } else if (type.equalsIgnoreCase("GNT")) {
      this.initGNTInfo();
    } else {
      System.err.println("Unknown model info type: " + type);
      System.exit(0);
    }
  }


  public void setSolver(SolverType solver) {

    this.solver = solver;
  }


  public void setCost(double cost) {

    this.cost = cost;
  }


  public void setEps(double eps) {

    this.eps = eps;
  }


  public SolverType getSolver() {

    return this.solver;
  }


  public double getCost() {

    return this.cost;
  }


  public double getEps() {

    return this.eps;
  }


  public String getModelName() {

    return this.modelName;
  }


  public void setModelName(String modelName) {

    this.modelName = modelName;
  }


  public BufferedWriter getModelInputFileWriter() {

    return this.modelInputFileWriter;
  }


  public void setModelInputFileWriter(BufferedWriter modelInputFileWriter) {

    this.modelInputFileWriter = modelInputFileWriter;
  }


  public String getModelFileArchive() {

    return this.modelFileArchive;
  }


  public void setModelFileArchive(String modelFileArchive) {

    this.modelFileArchive = modelFileArchive;
  }


  public void initFlorsInfo() {

    // L2-regularized L2-loss support vector classification (primal)
    this.setSolver(SolverType.L2R_L2LOSS_SVC);
    this.setCost(1.0);
    this.setEps(0.01);
  }


  public void initMDPInfo() {

    // multi-class SVM by Crammer and Singer
    this.setSolver(SolverType.MCSVM_CS);
    this.setCost(0.1);
    this.setEps(0.1);
  }


  public void initGNTInfo() {

    // L2-regularized logistic regression (primal)
    this.setSolver(SolverType.L2R_LR);
    this.setCost(1.0);
    this.setEps(0.01);
  }


  @Override
  public String toString() {

    String output = "ModelInfo:\n";
    output += "Solver: " + this.getSolver() + "\n";
    output += "Cost: " + this.getCost() + "\n";
    output += "Eps: " + this.getEps() + "\n";
    output += "ModelName: " + this.modelName + "\n";
    return output;

  }


  /**
   * A model file name is build from
   * <p>modelFilePrefix + taggerName + windowSize + dimension + number of training sentences
   * + wordFeat-flag + shapeFeat-flag + suffixFeat-flag
   * @param windowSize + ".txt"
   * @param dim
   * @param numberOfSentences
   */
  public void createModelFileName(int windowSize, int dim, int numberOfSentences, Alphabet alphabet,
      GlobalParams globalParams) {

    String wordFeatString = (alphabet.isWithWordFeats()) ? "T" : "F";
    String shapeFeatString = (alphabet.isWithShapeFeats()) ? "T" : "F";
    String suffixFeatString = (alphabet.isWithSuffixFeats()) ? "T" : "F";
    String clusterFeatString = (alphabet.isWithClusterFeats()) ? "T" : "F";
    String labelFeatString = (alphabet.isWithLabelFeats()) ? "T" : "F";
    if (wordFeatString.equals("F")) {
      dim = 0;
    }

    // TODO retrieve model name from model config
    this.modelName =
        "model_" + globalParams.getTaggerName() + "_" + windowSize + "_" + dim + "iw" + numberOfSentences + "sent_"
            + wordFeatString + shapeFeatString + suffixFeatString + clusterFeatString + labelFeatString + "_"
            + this.getSolver();
    this.modelFileArchive =
        GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER).resolve(this.modelName + ".zip").toString();

    if (GlobalConfig.getBoolean(ConfigKeys.CREATE_LIBLINEAR_INPUT_FILE)) {
      try {
        Path libLinearInputPath =
            GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER)
            .resolve("liblinear_input_" + this.modelName + ".txt");
        if (libLinearInputPath.getParent() != null) {
          Files.createDirectories(libLinearInputPath.getParent());
        }
        // create and open the writerBuffer
        this.setModelInputFileWriter(Files.newBufferedWriter(libLinearInputPath, StandardCharsets.UTF_8));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
