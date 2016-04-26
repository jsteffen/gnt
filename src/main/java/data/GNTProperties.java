package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import de.bwaldvogel.liblinear.SolverType;
import features.WordSuffixFeatureFactory;

public class GNTProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	private void setDataAccessors(){
		Data.wordFormIndex = Integer.parseInt(this.getProperty("wordFormIndex"));
		Data.posTagIndex = Integer.parseInt(this.getProperty("posTagIndex"));
	}

	// call setters via class instantiation
	public GNTProperties(String propsFileName){
		try {
			this.setGntProps(propsFileName);
			this.setGlobalParamsFromProperties();
			this.setModelInfoParametersFromProperties();
			this.setActivatedFeatureExtractors();
			this.setDataAccessors();
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}

	}

	public boolean getDebugProperty(){
		return Boolean.parseBoolean(this.getProperty("debug"));
	}

	public String getTrainingFile(){
		return this.getProperty("trainingFile").split(".conll")[0].replaceAll("[\n\r\t]", "");
	}

	public String getClusterIdNameFile(){
		return this.getProperty("clusterIdSourceFileName".replaceAll("[\n\r\t]", ""));
	}

}
