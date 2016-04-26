package corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.GNTProperties;
import data.GlobalParams;

// The class that holds all corpus files for training, testing etc.
public class Corpus {
	private GNTProperties gntProps = null;

	private  List<String> trainingLabeledSourceFiles = new ArrayList<String>();
	private  List<String> devLabeledSourceFiles = new ArrayList<String>();
	private  List<String> testLabeledSourceFiles = new ArrayList<String>();

	private  List<String> trainingLabeledData = new ArrayList<String>();
	private  List<String> devLabeledData = new ArrayList<String>();
	private  List<String> testLabeledData = new ArrayList<String>();

	private  List<String> trainingUnLabeledData = new ArrayList<String>();
	private  List<String> devUnLabeledData = new ArrayList<String>();
	private  List<String> testUnLabeledData = new ArrayList<String>();

	public List<String> getTrainingLabeledData() {
		return trainingLabeledData;
	}
	public List<String> getDevLabeledData() {
		return devLabeledData;
	}
	public List<String> getTestLabeledData() {
		return testLabeledData;
	}
	public List<String> getTrainingUnLabeledData() {
		return trainingUnLabeledData;
	}
	public List<String> getDevUnLabeledData() {
		return devUnLabeledData;
	}
	public List<String> getTestUnLabeledData() {
		return testUnLabeledData;
	}
	public List<String> getTrainingLabeledSourceFiles() {
		return trainingLabeledSourceFiles;
	}
	public List<String> getDevLabeledSourceFiles() {
		return devLabeledSourceFiles;
	}
	public List<String> getTestLabeledSourceFiles() {
		return testLabeledSourceFiles;
	}

	// Constructor;

	private void setTrainingLabeledSourceFilesFromProps(String property) {
		if (property != null){
			String[] fileList = property.split(",");
			for (String fileName : fileList){
				String fileNameWithoutExtension = fileName.split(".src")[0].replaceAll("[\n\r\t]", "");	
				this.trainingLabeledSourceFiles.add(fileNameWithoutExtension);
			}
		}
	}
	private void setDevLabeledSourceFilesFromProps(String property) {
		if (property != null){
			String[] fileList = property.split(",");
			for (String fileName : fileList){
				String fileNameWithoutExtension = fileName.split(".src")[0].replaceAll("[\n\r\t]", "");
				this.devLabeledSourceFiles.add(fileNameWithoutExtension);
			}		
		}
	}
	private void setTestLabeledSourceFilesFromProps(String property) {
		if (property != null){
			String[] fileList = property.split(",");
			for (String fileName : fileList){
				String fileNameWithoutExtension = fileName.split(".src")[0].replaceAll("[\n\r\t]", "");
				this.testLabeledSourceFiles.add(fileNameWithoutExtension);
			}	
		}
	}

	private void setTrainingLabeledDataFromProps(String property) {
		String[] fileList = property.split(",");
		for (String fileName : fileList){
			String fileNameWithoutExtension = fileName.split(".conll")[0].replaceAll("[\n\r\t]", "");
			trainingLabeledData.add(fileNameWithoutExtension);
		}		
	}
	private void setDevLabeledDataFromProps(String property) {
		String[] fileList = property.split(",");
		for (String fileName : fileList){
			String fileNameWithoutExtension = fileName.split(".conll")[0].replaceAll("[\n\r\t]", "");
			devLabeledData.add(fileNameWithoutExtension);
		}		
	}
	private void setTestLabeledDataFromProps(String property) {
		String[] fileList = property.split(",");
		for (String fileName : fileList){
			String fileNameWithoutExtension = fileName.split(".conll")[0].replaceAll("[\n\r\t]", "");
			testLabeledData.add(fileNameWithoutExtension);
		}		
	}
	private void setTrainingUnLabeledDataFromProps(String property) {
		String[] fileList = property.split(",");
		for (String fileName : fileList){
			trainingUnLabeledData.add(fileName.replaceAll("[\n\r\t]", ""));
		}		
	}
	private void setDevUnLabeledDataFromProps(String property) {
		String[] fileList = property.split(",");
		for (String fileName : fileList){
			devUnLabeledData.add(fileName.replaceAll("[\n\r\t]", ""));
		}		
	}
	private void setTestUnLabeledDataFromProps(String property) {
		String[] fileList = property.split(",");
		for (String fileName : fileList){
			testUnLabeledData.add(fileName.replaceAll("[\n\r\t]", ""));
		}		
	}

	public Corpus(){

	}
	public Corpus(GNTProperties properties) {
		this.gntProps = properties;
		
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
	
	public String makeEvalFileName(String labeledFile){
		return GlobalParams.evalFilePathname+ new File(labeledFile).getName()+".txt";
		
	}

}