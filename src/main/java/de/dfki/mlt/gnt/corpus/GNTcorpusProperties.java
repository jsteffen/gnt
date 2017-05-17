package de.dfki.mlt.gnt.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import de.dfki.mlt.gnt.data.Data;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class GNTcorpusProperties extends Properties {

  private static final long serialVersionUID = 1L;


  public GNTcorpusProperties(String propsFileName) {
    try {
      this.setGntProps(propsFileName);
      this.setDataAccessors();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void setGntProps(String propsFileName)
      throws InvalidPropertiesFormatException, IOException {

    FileInputStream fileIn = new FileInputStream(new File(propsFileName));
    this.loadFromXML(fileIn);
  }


  public String getTrainingFile() {

    return this.getProperty("trainingFile").split("\\.conll")[0].replaceAll("[\n\r\t]", "");
  }


  public String getClusterIdNameFile() {

    return this.getProperty("clusterIdSourceFileName".replaceAll("[\n\r\t]", ""));
  }


  private void setDataAccessors() {

    if ((this.getProperty("wordFormIndex") != null)
        && (this.getProperty("posTagIndex") != null)) {
      Data.setWordFormIndex(Integer.parseInt(this.getProperty("wordFormIndex")));
      Data.setPosTagIndex(Integer.parseInt(this.getProperty("posTagIndex")));
    }
  }
}
