package corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import data.Data;

public class GNTcorpusProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setGntProps (String propsFileName)
			throws InvalidPropertiesFormatException, IOException, XMLStreamException {
		FileInputStream fileIn = new FileInputStream(new File(propsFileName));
		this.loadFromXML(fileIn);
	}

	public String getTrainingFile(){
		return this.getProperty("trainingFile").split("\\.conll")[0].replaceAll("[\n\r\t]", "");
	}

	public String getClusterIdNameFile(){
		return this.getProperty("clusterIdSourceFileName".replaceAll("[\n\r\t]", ""));
	}

	private void setDataAccessors(){
		if ((this.getProperty("wordFormIndex") != null) &&
				(this.getProperty("posTagIndex") != null)){
			Data.wordFormIndex = Integer.parseInt(this.getProperty("wordFormIndex"));
			Data.posTagIndex = Integer.parseInt(this.getProperty("posTagIndex"));	
		}
	}

	// call setters via class instantiation
	public GNTcorpusProperties(String propsFileName){
		try {
			this.setGntProps(propsFileName);
			this.setDataAccessors();
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}
	}

}
