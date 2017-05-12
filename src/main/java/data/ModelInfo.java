package data;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import de.bwaldvogel.liblinear.SolverType;

public class ModelInfo {

  private SolverType solver = SolverType.L2R_LR; // -s 0
  private double C = 1.0;    // cost of constraints violation
  private double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less

  private String modelFilePrefix = "resources/models/model_";
  private String modelFile = "";
  private String modelFileArchive = "";

  private String modelInputFilePrefix = "resources/modelInputFiles/modelInputFile_";
  private String modelInputFile = "";
  private BufferedWriter modelInputFileWriter = null;
  
  public void setSolver(SolverType solver) {
    this.solver = solver;
  }

  public void setC(double c) {
    C = c;
  }

  public void setEps(double eps) {
    this.eps = eps;
  }

  public SolverType getSolver() {
    return solver;
  }
  
  public double getC() {
    return C;
  }
  
  public double getEps() {
    return eps;
  }

  public String getModelFilePrefix() {
    return modelFilePrefix;
  }
  public void setModelFilePrefix(String modelFilePrefix) {
    this.modelFilePrefix = modelFilePrefix;
  }
  public String getModelFile() {
    return modelFile;
  }
  public void setModelFile(String modelFile) {
    this.modelFile = modelFile;
  }
  
  public String getModelInputFilePrefix() {
    return modelInputFilePrefix;
  }
  public void setModelInputFilePrefix(String modelInputFilePrefix) {
    this.modelInputFilePrefix = modelInputFilePrefix;
  }
  public String getModelInputFile() {
    return modelInputFile;
  }
  public void setModelInputFile(String modelInputFile) {
    this.modelInputFile = modelInputFile;
  }
  public BufferedWriter getModelInputFileWriter() {
    return modelInputFileWriter;
  }
  public void setModelInputFileWriter(BufferedWriter modelInputFileWriter) {
    this.modelInputFileWriter = modelInputFileWriter;
  }
  public String getModelFileArchive() {
    return modelFileArchive;
  }

  public void setModelFileArchive(String modelFileArchive) {
    this.modelFileArchive = modelFileArchive;
  }

  //
  public ModelInfo(){
  }

  public ModelInfo(String type){
    if (type.equalsIgnoreCase("FLORS")) this.initFlorsInfo();
    else
      if (type.equalsIgnoreCase("MDP")) this.initMDPInfo();
      else
        if (type.equalsIgnoreCase("GNT")) this.initGNTInfo();
        else
        {
          System.err.println("Unknown model info type: " + type);
          System.exit(0);
        }
    this.setModelFile(this.getModelFilePrefix()+this.getSolver()+".txt");
  }

  public void initFlorsInfo(){
    // L2-regularized L2-loss support vector classification (primal)
    this.setSolver(SolverType.L2R_L2LOSS_SVC);
    this.setC(1.0);
    this.setEps(0.01);
  }

  public void initMDPInfo(){
    // multi-class SVM by Crammer and Singer
    this.setSolver(SolverType.MCSVM_CS);
    this.setC(0.1);
    this.setEps(0.1);
  }

  public void initGNTInfo(){
    // L2-regularized logistic regression (primal)
    this.setSolver(SolverType.L2R_LR);
    this.setC(1.0);
    this.setEps(0.01);
  }

  public String toString(){
    String output ="ModelInfo:\n";
    output += "Solver: " + this.getSolver()+"\n";
    output += "C: " + this.getC()+"\n";
    output += "Eps: " + this.getEps()+"\n";
    output += "ModelFilePrefix: " + this.getModelFilePrefix()+"\n";
    output += "ModelFileNames: " + this.getModelFile()+"\n";
    return output;

  }

  /**
   * A model file name is build from
   * <p>modelFilePrefix + taggerName + windowSize + dimension + number of training sentences + wordFeat-flag + shapeFeat-flag + suffixFeat-flag
   * @param windowSize + ".txt"
   * @param dim
   * @param numberOfSentences
   */
  public void createModelFileName(int windowSize, int dim, int numberOfSentences, Alphabet alphabet, GlobalParams globalParams) {
    String wordFeatString = (alphabet.isWithWordFeats())?"T":"F";
    String shapeFeatString = (alphabet.isWithShapeFeats())?"T":"F";
    String suffixFeatString = (alphabet.isWithSuffixFeats())?"T":"F";
    String clusterFeatString = (alphabet.isWithClusterFeats())?"T":"F";
    String labelFeatString = (alphabet.isWithLabelFeats())?"T":"F";
    if (wordFeatString.equals("F")) dim=0;

    String fileNameDetails = globalParams.getTaggerName()+"_"+windowSize+"_"+dim+"iw"+numberOfSentences+"sent_"+
        wordFeatString+shapeFeatString+suffixFeatString+clusterFeatString+labelFeatString+"_"+
        this.getSolver();
    
    this.modelFile = this.modelFilePrefix + fileNameDetails + ".txt";
    this.modelFileArchive = this.modelFilePrefix + fileNameDetails + ".zip";
    
    if (globalParams.isSaveModelInputFile()){
      //Only if ModelInfo.saveModelInputFile=true then save the modelInputFile
      this.modelInputFile = this.modelInputFilePrefix + fileNameDetails + ".txt";
      // And create and open the writerBuffer
      try {
        this.setModelInputFileWriter(new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(this.modelInputFile),"UTF-8")));
      } catch (UnsupportedEncodingException | FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
}
