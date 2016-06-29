package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import de.bwaldvogel.liblinear.SolverType;
import features.WordSuffixFeatureFactory;

public class GNTdataProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String configTmpFileName = "src/main/resources/dataConfig.xml";

	public void setGntProps (String propsFileName)
			throws InvalidPropertiesFormatException, IOException, XMLStreamException {
		FileInputStream fileIn = new FileInputStream(new File(propsFileName));
		this.loadFromXML(fileIn);
	}

	private SolverType parseSolverType(String solverTypeString){
		SolverType solverType = null;
		switch (solverTypeString) {
		case "MCSVM_CS": solverType = SolverType.MCSVM_CS; break;
		case "L2R_L2LOSS_SVC": solverType = SolverType.L2R_L2LOSS_SVC; break;
		case "L2R_LR": solverType = SolverType.L2R_LR; break;
		default:
			break;
		}
		return solverType;
	}

	private void setGlobalParamsFromProperties(){
		GlobalParams.taggerName = this.getProperty("taggerName").toUpperCase();
		GlobalParams.saveModelInputFile = Boolean.parseBoolean(this.getProperty("saveModelInputFile"));
		GlobalParams.windowSize = Integer.parseInt(this.getProperty("windowSize"));
		GlobalParams.numberOfSentences = Integer.parseInt(this.getProperty("numberOfSentences"));
		GlobalParams.dim = Integer.parseInt(this.getProperty("dim"));
		GlobalParams.subSamplingThreshold = Double.parseDouble(this.getProperty("subSamplingThreshold"));		
	}

	private void setModelInfoParametersFromProperties(){
		ModelInfo.solver = this.parseSolverType(this.getProperty("solverType"));
		ModelInfo.C = Double.parseDouble(this.getProperty("c"));
		ModelInfo.eps = Double.parseDouble(this.getProperty("eps"));
	}

	private void setActivatedFeatureExtractors(){
		Alphabet.withWordFeats = Boolean.parseBoolean(this.getProperty("withWordFeats"));
		Alphabet.withShapeFeats = Boolean.parseBoolean(this.getProperty("withShapeFeats"));
		Alphabet.withSuffixFeats = Boolean.parseBoolean(this.getProperty("withSuffixFeats"));
		Alphabet.withClusterFeats = Boolean.parseBoolean(this.getProperty("withClusterFeats"));

		WordSuffixFeatureFactory.ngram = Boolean.parseBoolean(this.getProperty("WordSuffixFeatureFactory.ngram"));
		if (this.getProperty("WordSuffixFeatureFactory.ngramSize") != null)
			WordSuffixFeatureFactory.ngramSize = Integer.parseInt(this.getProperty("WordSuffixFeatureFactory.ngramSize"));
	}

	public void copyConfigFile(String propsFileName){
		Path sourceFile = new File(propsFileName).toPath();
		Path targetFile = new File(GNTdataProperties.configTmpFileName).toPath();
		try {
			Files.copy(sourceFile, targetFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	// call setters via class instantiation
	public GNTdataProperties(String propsFileName){
		try {
			this.setGntProps(propsFileName);
			this.setGlobalParamsFromProperties();
			this.setModelInfoParametersFromProperties();
			this.setActivatedFeatureExtractors();
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}

	}

	public GNTdataProperties(InputStream fileIn){
		try {
			this.loadFromXML(fileIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setGlobalParamsFromProperties();
		this.setModelInfoParametersFromProperties();
		this.setActivatedFeatureExtractors();
	}

}
