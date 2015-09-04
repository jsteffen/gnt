package data;

public class Sentence {
	private int[] wordArray = new int[0];
	private int[] posArray = new int[0];
	
	// Setters and getters
	public int[] getWordArray() {
		return wordArray;
	}
	public void setWordArray(int[] wordArray) {
		this.wordArray = wordArray;
	}
	public int[] getPosArray() {
		return posArray;
	}
	public void setPosArray(int[] posArray) {
		this.posArray = posArray;
	}
	
	// Instance
	
	public Sentence (){
	}
	
	
	public Sentence (int size){
		wordArray = new int[size];
		posArray = new int[size];
	}
	
	public void addNextToken(int i, int wordIndex, int posIndex) {
		this.getWordArray()[i] = wordIndex;
		this.getPosArray()[i] = posIndex;
	}
	

}
