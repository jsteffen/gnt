package data;

import java.util.List;

public class Data {

		private SetIndexMap wordSet = new SetIndexMap();
		private SetIndexMap labelSet = new SetIndexMap();
		
		private Sentence sentence = new Sentence();
		
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
			return this.getLabelSet().updateSetIndexMap(word);
		}
		private int updateLabelMap(String label) {
			return this.getLabelSet().updateSetIndexMap(label);
		}
		
		public void generateSentenceObjectFromConllLabeledSentence(List<String[]> tokens) {
			Sentence sentence = new Sentence(tokens.size());
			for (int i=0; i < tokens.size(); i++){
				// tokens are of form
				// "1	The	The	DT	DT	_	2	NMOD"
				// NOTE: NO lower case here fo word
				// Extract word and pos from conll sentence, create index for both
				// and create sentence using word/pos index
				sentence.addNextToken(i,
						updateWordMap(tokens.get(i)[1]),
						updateLabelMap(tokens.get(i)[3]));
			}
			this.setSentence(sentence);
		}
		
				
}
