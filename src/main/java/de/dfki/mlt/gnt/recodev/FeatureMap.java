package de.dfki.mlt.gnt.recodev;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class FeatureMap {

  private Map<Integer, Double> featureMap = new TreeMap<Integer, Double>();


  private Data dataObj = new Data();


  public Data getDataObj() {

    return this.dataObj;
  }


  public void setDataObj(Data dataObj) {

    this.dataObj = dataObj;
  }


  public Map<Integer, Double> getFeatureMap() {

    return this.featureMap;
  }


  public void setFeatureMap(Map<Integer, Double> featureMap) {

    this.featureMap = featureMap;
  }
}
