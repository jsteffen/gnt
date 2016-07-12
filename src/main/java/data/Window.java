package data;

import java.util.ArrayList;
import java.util.List;

import features.WordFeatures;

/**
 * Create local context window for tagging t_i of size 2*windowSize+1 centered around t_i.
 * The idea of the approach is as follow:
 * - determine number of padding and context elements for center element i
 * - for each element of window, create 2*windowSize +1 elements of WordFeatures.
 * Use left and right sentence pads "<s>" and "</s>" for ensuring sufficient context for all words. 
 * They are needed to make sure that offSets are computed correctly
 * I will do in that way, that I treat pads as empty WordFeatures with some dummy string.
 * 
 * @author gune00
 *
 */
public class Window {
	public static int windowCnt = 0;
	private Data data ;
	private Alphabet alphabet ;
	private Sentence sentence;
	// Index of the window center element
	private int center;
	// radius of the window
	private int windowSize = 0;
	private List<WordFeatures> elements = new ArrayList<WordFeatures>();
	// Total length of the window by adding all features of each window element
	private int windowLength = 0;
	private int labelIndex = -1;

	// Setters and getters

	public List<WordFeatures> getElements() {
		return elements;
	}
	public void setElements(List<WordFeatures> elements) {
		this.elements = elements;
	}
	public int getWindowLength() {
		return windowLength;
	}
	public void setWindowLength(int windowLength) {
		this.windowLength = windowLength;
	}
	public int getLabelIndex() {
		return labelIndex;
	}
	public void setLabelIndex(int labelIndex) {
		this.labelIndex = labelIndex;
	}

	// Instance
	public Window(Sentence sentence, int i, int windowSize, Data data,
			Alphabet alphabet) {
		Window.windowCnt++;
		this.windowSize = windowSize;
		this.alphabet = alphabet;
		this.data = data;
		this.sentence = sentence;
		this.center = i;
	}

	public void clean() {
		elements = new ArrayList<WordFeatures>();
		windowLength = 0;
	}

	/*
	 * I have to fill pads on either sides of a sentence, depending on windowSize l and sentence length max.
	 * In order to do so, I distinguish for the window center i:
	 * - leftPads -> number of left pads : if i < l then lp=l-i  else lp=0; 
	 * - leftContext -> number of left context elements -> lc=l-lp
	 * - rightContext -> number of right context elements -> (max-i) < l then rc=max-i else rc=l
	 * - rightPads -> number of right pads -> rp = l-rc
	 * 
	 * This will give me a total of 2 * windowSize + 1 elements for the window
	 */

	/**
	 * The general structure of a window is:
	 * <leftPads, leftContext, Center, rightContext, rightPad>
	 * where center is a single element, and the other elements are sequences of elements
	 * depending on the actually value of this.windowSize; this means that the total number of elements
	 * is #|leftPads + leftContext| == this.windowSize
	 * 
	 * boolean train means: we are in the training mode
	 * boolean adjust means: add offsets to each index in order to get Liblinear-consitent feature names (numerical indices)
	 * @param train
	 * @param adjust
	 */
	public void fillWindow(boolean train, boolean adjust){

		// compute left/right borders of the size of the window elements, which depends on windowSize
		int max = this.sentence.getWordArray().length-1; // Because elements are indexed from 0 upwards
		int leftPads = (this.center < this.windowSize)?(this.windowSize-this.center):0;
		int leftContext = (this.windowSize-leftPads);
		int rightContext = ((max-this.center) < this.windowSize)?(max-this.center):this.windowSize;
		// TODO: means context cannot cross sentence boundary ! -> OK?
		int rightPads = (this.windowSize-rightContext);

		// the surface word string determined from the training examples
		// Use "<BOUNDARY>" as dummy for padding elements
		String wordString ="";
		// counts the dynamically created window element and use it as index in WordFeatures
		int elementCnt = 0;

		// printWindowIntervalInfo(max, leftPads, leftContext, rightContext, rightPads);

		// Based on the computed intervals above, this also indicates the number of window elements.
		// based on value this.size;
		// NOTE Does not add label

		// Add left padding elements to sentence (if any); needed later to correctly compute global offSets

		for (int i = 0 ; i < leftPads; i++){
			// Make sure that center element does not cross sentence boundary
			if (i <= sentence.getLabelArray().length) {
				wordString = "<BOUNDARY>";
				// wordLoc does not matter here, because empty WordFeatures class is created
				WordFeatures wordFeatures = createWordFeatures(sentence, wordString, i, elementCnt, train, adjust);
				windowLength+= wordFeatures.getLength();
				elements.add(wordFeatures);
				elementCnt++;
			}
		}
		// Add left context elements
		for (int i = (this.center-leftContext) ; i < this.center; i++){
			wordString = this.data.getWordSet().num2label.get(this.sentence.getWordArray()[i]);
			WordFeatures wordFeatures = createWordFeatures(sentence, wordString, i, elementCnt, train, adjust);
			windowLength+= wordFeatures.getLength();
			elements.add(wordFeatures);
			elementCnt++;
		}
		// Add token center element
		{	
			wordString = this.data.getWordSet().num2label.get(this.sentence.getWordArray()[this.center]);
			WordFeatures wordFeatures = createWordFeatures(sentence, wordString, this.center, elementCnt, train, adjust);
			windowLength+= wordFeatures.getLength();
			elements.add(wordFeatures);
			elementCnt++;
		}
		// right content elements; 
		// set wordLoc always to 1, because can never be 0
		for (int i = this.center+1 ; i < (this.center+1+rightContext); i++){
			wordString = this.data.getWordSet().num2label.get(this.sentence.getWordArray()[i]);
			WordFeatures wordFeatures = createWordFeatures(sentence, wordString, i, elementCnt, train, adjust);
			windowLength+= wordFeatures.getLength();
			elements.add(wordFeatures);
			elementCnt++;
		}
		// right sentence pads

		for (int i = (this.center + rightContext) ; i < (this.center + rightContext + rightPads); i++) {
			// Make sure that center element does not cross sentence boundary
			if (i <= sentence.getLabelArray().length) {
				wordString = "<BOUNDARY>";
				// wordLoc does not matter here, because empty WordFeatures class is created
				WordFeatures wordFeatures = createWordFeatures(sentence, wordString, i, elementCnt, train, adjust);
				windowLength+= wordFeatures.getLength();
				elements.add(wordFeatures);
				elementCnt++;
			}
		}
	}

	// wordPosition is 0 if word is first word in sentence, else 1
	/**
	 * Create a feature vector for a word.
	 * @param word
	 * @param wordPosition
	 * @param elementCnt
	 * @param adjust
	 * @return
	 */
	private WordFeatures createWordFeatures(Sentence sentence, String word, int wordPosition, int elementCnt, boolean train, boolean adjust) {
		// Get left and right word of word -> later used for handing unknown words
		Pair<String, String> contextWords = getContextWords(sentence, wordPosition);
		// create a new WordFeatures element
		WordFeatures wordFeatures = new WordFeatures(word, contextWords.getL(), contextWords.getR());
		// set its index
		wordFeatures.setIndex(elementCnt);
		// set its offsets using the values from OffSets which are pre-initialised after data has been loaded and before
		// training starts
		wordFeatures.setOffSets();
		// indicate whether relative feature names (indices) should be adjusted to global ones according to the rules of Liblinear
		wordFeatures.setAdjust(adjust);
		// fill all the window's elements
		wordFeatures.fillWordFeatures(word, wordPosition, alphabet, train);
		return wordFeatures;
	}

	private Pair<String, String> getContextWords(Sentence sentence, int wordIndex){
		//System.out.println("Sentence: " + sentence.getWordArray().length + " Wordindex: " + wordIndex);
		String leftWord = (wordIndex==0)?"<BOUNDARY>":
			this.data.getWordSet().num2label.get(this.sentence.getWordArray()[wordIndex-1]);
		String rightWord = (wordIndex>=sentence.getWordArray().length-1)?"<BOUNDARY>":
			this.data.getWordSet().num2label.get(this.sentence.getWordArray()[wordIndex+1]);

		return new Pair<String, String>(leftWord,rightWord);
	}

	// Print functions

	/**
	 * Only for printing the borders of the window elements
	 * @param max	- indx of last token in sentence
	 * @param lp	- number of left padding elements	
	 * @param lc	- number of left context elements
	 * @param rc	- number of right context elements
	 * @param rp	- number of right padding elements
	 */
	public void printWindowIntervalInfo(int max, int lp, int lc, int rc, int rp){
		// print intervals
		System.out.println("max: " + max + " center: " + this.center);
		System.out.println("lp: " + lp + " lc: " + lc);
		System.out.println("rc: " + rc + " rp: " + rp);

		// print content of intervals

		String testString = "";
		// left sentence pads
		for (int i = 0 ; i < lp; i++) 
			testString += "<BOUNDARY> ";
		// lef context elements
		for (int i = (this.center-lc) ; i < this.center; i++)
			testString += this.data.getWordSet().num2label.get(this.sentence.getWordArray()[i])+" ";
		// center elelemt
		testString += this.data.getWordSet().num2label.get(this.sentence.getWordArray()[this.center])+" ";
		// right content elements
		for (int i = this.center+1 ; i < (this.center+1+rc); i++)
			testString += this.data.getWordSet().num2label.get(this.sentence.getWordArray()[i])+" ";
		// right sentence pads
		for (int i = (this.center + rc) ; i < (this.center + rc + rp); i++) 
			testString += "<BOUNDARY> ";
		testString +="\n";

		System.out.println(testString);
	}

	public String toString(){
		String output = "Window index:" + Window.windowCnt + " Window label index: " + this.getLabelIndex() + "\n";
		output += "Window total length:" + this.getWindowLength() + "\n";
		for (WordFeatures wordFeatures : this.elements){
			output+=wordFeatures.toString();
		}
		return output;
	}




}
