package data;

import java.util.List;

public class Data {

	// TODO necessary to keep a hash-map for words ?
	private SetIndexMap wordSet = new SetIndexMap();
	private SetIndexMap labelSet = new SetIndexMap();

	private Sentence sentence = new Sentence();

	private int sentenceCnt = 0;

	// Setters and getters

	public int getSentenceCnt() {
		return sentenceCnt;
	}
	public void setSentenceCnt(int sentenceCnt) {
		this.sentenceCnt = sentenceCnt;
	}
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
	public Sentence getSentence() {
		return sentence;
	}
	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}
	//
	// Instances
	public Data() {
	}

	// Methods

	private int updateWordMap(String word) {
		return this.getWordSet().updateSetIndexMap(word);
	}
	private int updateLabelMap(String label) {
		return this.getLabelSet().updateSetIndexMap(label);
	}

	public void generateSentenceObjectFromConllLabeledSentence(List<String[]> tokens) {
		Sentence sentence = new Sentence(tokens.size());
		for (int i=0; i < tokens.size(); i++){
			// tokens are of form
			// "1	The	The	DT	DT	_	2	NMOD"
			// NOTE: No lower case here of word
			// Extract word and pos from conll sentence, create index for both
			// and create sentence using word/pos index
			sentence.addNextToken(i,
					updateWordMap(tokens.get(i)[1]),
					updateLabelMap(tokens.get(i)[3]));
		}
		this.setSentence(sentence);
		this.sentenceCnt++;
	}

	public String toString(){
		String output = "";
		output += "Sentences: " + this.sentenceCnt +
				" words: " + this.getWordSet().getLabelCnt() +
				" labels: " + this.getLabelSet().getLabelCnt() + "\n";
		return output;
	}


}
