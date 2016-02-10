package data;

public class Sentence {
	private int[] wordArray = new int[0];
	private int[] labelArray = new int[0];
	
	// Setters and getters
	public int[] getWordArray() {
		return wordArray;
	}
	public void setWordArray(int[] wordArray) {
		this.wordArray = wordArray;
	}
	public int[] getLabelArray() {
		return labelArray;
	}
	public void setLabelArray(int[] labelArray) {
		this.labelArray = labelArray;
	}
	
	// Instance
	
	public Sentence (){
	}
	
	
	public Sentence (int size){
		wordArray = new int[size];
		labelArray = new int[size];
	}
	
	public void addNextToken(int i, int wordIndex, int posIndex) {
		this.getWordArray()[i] = wordIndex;
		this.getLabelArray()[i] = posIndex;
	}
	

}
