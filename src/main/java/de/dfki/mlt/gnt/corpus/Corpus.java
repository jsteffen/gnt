package de.dfki.mlt.gnt.corpus;

import java.util.ArrayList;
import java.util.List;

import de.dfki.mlt.gnt.data.GlobalParams;

/**
 * The class that holds all corpus files for training, testing etc.
 *
 * @author Günter Neumann, DFKI
 */
public class Corpus {

  private GNTcorpusProperties gntProps = null;
  private GlobalParams globalParams = null;

  private List<String> trainingLabeledSourceFiles = new ArrayList<String>();
  private List<String> devLabeledSourceFiles = new ArrayList<String>();
  private List<String> testLabeledSourceFiles = new ArrayList<String>();

  private List<String> trainingLabeledData = new ArrayList<String>();
  private List<String> devLabeledData = new ArrayList<String>();
  private List<String> testLabeledData = new ArrayList<String>();

  private List<String> trainingUnLabeledData = new ArrayList<String>();
  private List<String> devUnLabeledData = new ArrayList<String>();
  private List<String> testUnLabeledData = new ArrayList<String>();


  public Corpus() {

  }


  public Corpus(GNTcorpusProperties properties) {
    this.gntProps = properties;

    this.setCorpusFiles();
  }


  public Corpus(GNTcorpusProperties corpusProps, GlobalParams globalParams) {
    this.gntProps = corpusProps;
    this.setCorpusFiles();
    this.setGlobalParams(globalParams);
  }


  public GlobalParams getGlobalParams() {

    return this.globalParams;
  }


  public void setGlobalParams(GlobalParams globalParams) {

    this.globalParams = globalParams;
  }


  public GNTcorpusProperties getGntProps() {

    return this.gntProps;
  }


  public void setGntProps(GNTcorpusProperties gntProps) {

    this.gntProps = gntProps;
  }


  public List<String> getTrainingLabeledData() {

    return this.trainingLabeledData;
  }


  public List<String> getDevLabeledData() {

    return this.devLabeledData;
  }


  public List<String> getTestLabeledData() {

    return this.testLabeledData;
  }


  public List<String> getTrainingUnLabeledData() {

    return this.trainingUnLabeledData;
  }


  public List<String> getDevUnLabeledData() {

    return this.devUnLabeledData;
  }


  public List<String> getTestUnLabeledData() {

    return this.testUnLabeledData;
  }


  public List<String> getTrainingLabeledSourceFiles() {

    return this.trainingLabeledSourceFiles;
  }


  public List<String> getDevLabeledSourceFiles() {

    return this.devLabeledSourceFiles;
  }


  public List<String> getTestLabeledSourceFiles() {

    return this.testLabeledSourceFiles;
  }


  private void setTrainingLabeledSourceFilesFromProps(String property) {

    if (property != null) {
      String[] fileList = property.split(",");
      for (String fileName : fileList) {
        this.trainingLabeledSourceFiles.add(fileName.trim());
      }
    }
  }


  private void setDevLabeledSourceFilesFromProps(String property) {

    if (property != null) {
      String[] fileList = property.split(",");
      for (String fileName : fileList) {
        this.devLabeledSourceFiles.add(fileName.trim());
      }
    }
  }


  private void setTestLabeledSourceFilesFromProps(String property) {

    if (property != null) {
      String[] fileList = property.split(",");
      for (String fileName : fileList) {
        this.testLabeledSourceFiles.add(fileName.trim());
      }
    }
  }


  private void setTrainingLabeledDataFromProps(String property) {

    String[] fileList = property.split(",");
    for (String fileName : fileList) {
      this.trainingLabeledData.add(fileName.trim());
    }
  }


  private void setDevLabeledDataFromProps(String property) {

    String[] fileList = property.split(",");
    for (String fileName : fileList) {
      this.devLabeledData.add(fileName.trim());
    }
  }


  private void setTestLabeledDataFromProps(String property) {

    String[] fileList = property.split(",");
    for (String fileName : fileList) {
      this.testLabeledData.add(fileName.trim());
    }
  }


  private void setTrainingUnLabeledDataFromProps(String property) {

    String[] fileList = property.split(",");
    for (String fileName : fileList) {
      this.trainingUnLabeledData.add(fileName.trim());
    }
  }


  private void setDevUnLabeledDataFromProps(String property) {

    String[] fileList = property.split(",");
    for (String fileName : fileList) {
      this.devUnLabeledData.add(fileName.trim());
    }
  }


  private void setTestUnLabeledDataFromProps(String property) {

    String[] fileList = property.split(",");
    for (String fileName : fileList) {
      this.testUnLabeledData.add(fileName.trim());
    }
  }


  private void setCorpusFiles() {

    this.setTrainingLabeledSourceFilesFromProps(this.gntProps.getProperty("trainingLabeledSourceFiles"));
    this.setDevLabeledSourceFilesFromProps(this.gntProps.getProperty("devLabeledSourceFiles"));
    this.setTestLabeledSourceFilesFromProps(this.gntProps.getProperty("testLabeledSourceFiles"));

    this.setTrainingLabeledDataFromProps(this.gntProps.getProperty("trainingLabeledData"));
    this.setDevLabeledDataFromProps(this.gntProps.getProperty("devLabeledData"));
    this.setTestLabeledDataFromProps(this.gntProps.getProperty("testLabeledData"));

    this.setTrainingUnLabeledDataFromProps(this.gntProps.getProperty("trainingUnLabeledData"));
    this.setDevUnLabeledDataFromProps(this.gntProps.getProperty("devUnLabeledData"));
    this.setTestUnLabeledDataFromProps(this.gntProps.getProperty("testUnLabeledData"));
  }
}
