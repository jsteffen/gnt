package data;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SetIndexMap {
	private Map<String, Integer> label2num = new HashMap<String, Integer>();
	Map<Integer, String> num2label = new TreeMap<Integer, String>();
	private int labelCnt = 0;

	// Setters and getters
	public Map<String, Integer> getLabel2num() {
		return label2num;
	}
	public void setLabel2num(Map<String, Integer> label2num) {
		this.label2num = label2num;
	}
	public Map<Integer, String> getNum2label() {
		return num2label;
	}
	public void setNum2label(Map<Integer, String> num2label) {
		this.num2label = num2label;
	}
	public int getLabelCnt() {
		return labelCnt;
	}
	public void setLabelCnt(int labelCnt) {
		this.labelCnt = labelCnt;
	}

	// Methods
	public int updateSetIndexMap(String label){
		int index = -1;
		if (this.getLabel2num().containsKey(label)){
			index = this.getLabel2num().get(label);
		} else {
			labelCnt++;
			this.getLabel2num().put(label, labelCnt);
			this.getNum2label().put(labelCnt, label);
			index = labelCnt;
		}	
		return index;
	}
	
	public String toString (){
		String output = "";
		for (int index : getNum2label().keySet()){
			output += index+":"+getNum2label().get(index);
		}
		return output;
	}
}
