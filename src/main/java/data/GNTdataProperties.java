package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import de.bwaldvogel.liblinear.SolverType;
import features.WordSuffixFeatureFactory;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class GNTdataProperties extends Properties {

  public static final String configTmpFileName = "src/main/resources/dataConfig.xml";

  private static final long serialVersionUID = 1L;

  private GlobalParams globalParams = new GlobalParams();
  private Alphabet alphabet = new Alphabet();
  private ModelInfo modelInfo = new ModelInfo();


  public GNTdataProperties(String propsFileName) {
    // call setters via class instantiation
    try {
      this.setGntProps(propsFileName);
      this.setGlobalParamsFromProperties();
      this.setModelInfoParametersFromProperties();
      this.setActivatedFeatureExtractors();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public GNTdataProperties(InputStream fileIn) {
    try {
      this.loadFromXML(fileIn);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.setGlobalParamsFromProperties();
    this.setModelInfoParametersFromProperties();
    this.setActivatedFeatureExtractors();
  }


  public GlobalParams getGlobalParams() {

    return this.globalParams;
  }


  public void setGlobalParams(GlobalParams globalParams) {

    this.globalParams = globalParams;
  }


  public Alphabet getAlphabet() {

    return this.alphabet;
  }


  public void setAlphabet(Alphabet alphabet) {

    this.alphabet = alphabet;
  }


  public ModelInfo getModelInfo() {

    return this.modelInfo;
  }


  public void setModelInfo(ModelInfo modelInfo) {

    this.modelInfo = modelInfo;
  }


  public void setGntProps(String propsFileName)
      throws InvalidPropertiesFormatException, IOException {

    FileInputStream fileIn = new FileInputStream(new File(propsFileName));
    this.loadFromXML(fileIn);
  }


  private SolverType parseSolverType(String solverTypeString) {

    SolverType solverType = null;
    switch (solverTypeString) {
      case "MCSVM_CS":
        solverType = SolverType.MCSVM_CS;
        break;
      case "L2R_L2LOSS_SVC":
        solverType = SolverType.L2R_L2LOSS_SVC;
        break;
      case "L2R_LR":
        solverType = SolverType.L2R_LR;
        break;
      default:
        break;
    }
    return solverType;
  }


  private void setGlobalParamsFromProperties() {

    this.getGlobalParams().setTaggerName(this.getProperty("taggerName").toUpperCase());
    this.getGlobalParams().setSaveModelInputFile(Boolean.parseBoolean(this.getProperty("saveModelInputFile")));
    this.getGlobalParams().setWindowSize(Integer.parseInt(this.getProperty("windowSize")));
    this.getGlobalParams().setNumberOfSentences(Integer.parseInt(this.getProperty("numberOfSentences")));
    this.getGlobalParams().setDim(Integer.parseInt(this.getProperty("dim")));
    this.getGlobalParams().setSubSamplingThreshold(Double.parseDouble(this.getProperty("subSamplingThreshold")));
  }


  private void setModelInfoParametersFromProperties() {

    this.getModelInfo().setSolver(this.parseSolverType(this.getProperty("solverType")));
    this.getModelInfo().setCost(Double.parseDouble(this.getProperty("c")));
    this.getModelInfo().setEps(Double.parseDouble(this.getProperty("eps")));
  }


  private void setActivatedFeatureExtractors() {

    this.getAlphabet().setWithWordFeats(Boolean.parseBoolean(this.getProperty("withWordFeats")));
    this.getAlphabet().setWithShapeFeats(Boolean.parseBoolean(this.getProperty("withShapeFeats")));
    this.getAlphabet().setWithSuffixFeats(Boolean.parseBoolean(this.getProperty("withSuffixFeats")));
    this.getAlphabet().setWithClusterFeats(Boolean.parseBoolean(this.getProperty("withClusterFeats")));
    this.getAlphabet().setWithLabelFeats(Boolean.parseBoolean(this.getProperty("withLabelFeats")));

    WordSuffixFeatureFactory.setNgram(Boolean.parseBoolean(this.getProperty("WordSuffixFeatureFactory.ngram")));
    if (this.getProperty("WordSuffixFeatureFactory.ngramSize") != null) {
      WordSuffixFeatureFactory.setNgramSize(Integer.parseInt(this.getProperty("WordSuffixFeatureFactory.ngramSize")));
    }
  }


  public void copyConfigFile(String propsFileName) {

    Path sourceFile = new File(propsFileName).toPath();
    Path targetFile = new File(GNTdataProperties.configTmpFileName).toPath();
    try {
      Files.copy(sourceFile, targetFile);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
