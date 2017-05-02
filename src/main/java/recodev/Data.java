package recodev;

import data.SetIndexMap;

public class Data {

	private SetIndexMap wordSet = new SetIndexMap();
	private SetIndexMap labelSet = new SetIndexMap();
	private String labelMapFileName = "resources/recodev/labelSet.txt";
	private String wordMapFileName = "resources/recodev/wordSet.txt";
	
	// Setters and getters

	public SetIndexMap getWordSet() {
		return wordSet;
	}
	public void setWordSet(SetIndexMap wordSet) {
		this.wordSet = wordSet;
	}
	public SetIndexMap getLabelSet() {
		return labelSet;
	}
	public void setLabelSet(SetIndexMap labelSet) {
		this.labelSet = labelSet;
	}
	public String getLabelMapFileName() {
		return labelMapFileName;
	}
	public void setLabelMapFileName(String labelMapFileName) {
		this.labelMapFileName = labelMapFileName;
	}
	
	public String getWordMapFileName() {
		return wordMapFileName;
	}
	public void setWordMapFileName(String wordMapFileName) {
		this.wordMapFileName = wordMapFileName;
	}
	
	// Instances
	public Data() {
	}
	
	// Methods

	
	public int updateWordMap(String word) {
		return this.getWordSet().updateSetIndexMap(word);
	}
	public int updateLabelMap(String label) {
		return this.getLabelSet().updateSetIndexMap(label);
	}

	public void cleanWordSet(){
		wordSet = new SetIndexMap();
	}
	public void cleanLabelSet(){
		labelSet = new SetIndexMap();
	}
		
	public void saveLabelSet(){
		this.getLabelSet().writeSetIndexMap(this.getLabelMapFileName());	
	}

	public void readLabelSet(){
		this.getLabelSet().readSetIndexMap(this.getLabelMapFileName());	
	}
	
	public void saveWordSet(){
		this.getWordSet().writeSetIndexMap(this.getWordMapFileName());	
	}

	public void readWordSet(){
		this.getWordSet().readSetIndexMap(this.getWordMapFileName());	
	}

	public String toString(){
		String output = "";
		output += " words: " + this.getWordSet().getLabelCnt() +
				" labels: " + this.getLabelSet().getLabelCnt() + "\n";
		return output;
	}


}
