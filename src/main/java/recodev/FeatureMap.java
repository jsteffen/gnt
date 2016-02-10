package recodev;

import java.util.Map;
import java.util.TreeMap;

public class FeatureMap {

	Map<Integer,Double> featureMap = new TreeMap<Integer,Double>();

	
	private Data dataObj = new Data();

	public Data getDataObj() {
		return dataObj;
	}

	public void setDataObj(Data dataObj) {
		this.dataObj = dataObj;
	}

	public Map<Integer, Double> getFeatureMap() {
		return featureMap;
	}

	public void setFeatureMap(Map<Integer, Double> featureMap) {
		this.featureMap = featureMap;
	}

	

	
	


}
