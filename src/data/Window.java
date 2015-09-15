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
	private int size = 0;
	private List<WordFeatures> elements = new ArrayList<WordFeatures>();
	private boolean trainingPhase = true;
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
		this.size = windowSize;
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
	 * - lp -> number of left pads : if i < l then lp=l-i  else lp=0; 
	 * - lc -> number of left context elements -> lc=l-lp
	 * - rc -> number of right context elements -> (max-i) < l then rc=max-i else rc=l
	 * - rp -> number of right pads -> rp = l-rc
	 * This will give me a total of 2 * windowSize + 1 elements for the window
	 */

	/**
	 * The general structure of a window is:
	 * <leftPads, leftContext, Center, rightContext, rightPad>
	 * where center is a single elements, and the other elements are sequences of elements
	 * depending on the actually value of this.size; this means that the total number of elements
	 * is #|leftPads and leftContext| == this.size
	 * 
	 * boolean train means: we are in the training mode
	 * boolean adjust means: add offsets to each index in order to get Liblinear-consitent feature names (numerical indices)
	 * @param train
	 * @param adjust
	 */
	public void fillWindow(boolean train, boolean adjust){
		this.trainingPhase = train;

		// compute left/right borders of the size of the window elements
		// depends on windowSize
		int max = this.sentence.getWordArray().length-1;
		int lp = (this.center < this.size)?(this.size-this.center):0;
		int lc = (this.size-lp);
		int rc = ((max-this.center) < this.size)?(max-this.center):this.size;
		int rp = (this.size-rc);

		// the surface word string determined from the training examples
		String wordString ="";
		// the location of the word in the sentence: 0 means "first word in sentence", 1 means "otherwise"
		int wordLoc = 1;
		// counts the dynamically created window elements and use it as index in WordFeatures
		int elementCnt = 0;

		// printWindowIntervalInfo(max, lp, lc, rc, rp);

		// Based on the computed intervals above, this also indicates the number of window elements.
		// based on value this.size;
		// NOTE Does not add label
		// Add left padding elements to sentence; needed to later correctly compute global offSets

		for (int i = 0 ; i < lp; i++){
			wordString = "<BOUNDARY>";
			// wordLoc does not matter here, because empty WordFeatures class is created
			WordFeatures wordFeatures = createWordFeatures(wordString, 1, elementCnt, adjust);
			windowLength+= wordFeatures.getLength();
			elements.add(wordFeatures);
			elementCnt++;
		}
		// Add left context elements
		for (int i = (this.center-lc) ; i < this.center; i++){
			// check position of word in sentence; if it is either at start or not; influences computation of shape feature
			wordLoc = (i==0)?0:1;
			wordString = this.data.getWordSet().num2label.get(this.sentence.getWordArray()[i]);
			WordFeatures wordFeatures = createWordFeatures(wordString, wordLoc, elementCnt, adjust);
			windowLength+= wordFeatures.getLength();
			elements.add(wordFeatures);
			elementCnt++;
		}
		// Add token center element
		{	wordLoc = (this.center==0)?0:1;
		wordString = this.data.getWordSet().num2label.get(this.sentence.getWordArray()[this.center]);
		WordFeatures wordFeatures = createWordFeatures(wordString, wordLoc, elementCnt, adjust);
		windowLength+= wordFeatures.getLength();
		elements.add(wordFeatures);
		elementCnt++;
		}
		// right content elements; 
		// set wordLoc always to 1, because can never be 0
		for (int i = this.center+1 ; i < (this.center+1+rc); i++){
			wordString = this.data.getWordSet().num2label.get(this.sentence.getWordArray()[i]);
			WordFeatures wordFeatures = createWordFeatures(wordString, 1, elementCnt, adjust);
			windowLength+= wordFeatures.getLength();
			elements.add(wordFeatures);
			elementCnt++;
		}
		// right sentence pads

		for (int i = (this.center + rc) ; i < (this.center + rc + rp); i++) {
			wordString = "<BOUNDARY>";
			// wordLoc does not matter here, because empty WordFeatures class is created
			WordFeatures wordFeatures = createWordFeatures(wordString, 1, elementCnt, adjust);
			windowLength+= wordFeatures.getLength();
			elements.add(wordFeatures);
			elementCnt++;
		}
	}

	// wordPosition is 0 if word is first word in sentence, else 1
	private WordFeatures createWordFeatures(String word, int wordPosition, int elementCnt, boolean adjust) {
		// create a new WordFeatures element
		WordFeatures wordFeatures = new WordFeatures(word);
		// set its index
		wordFeatures.setIndex(elementCnt);
		// set its offsets using the values from OffSets which are pre-initialised after data has been loaded and before
		// training starts
		wordFeatures.setOffSets();
		// indicate whether relative feature names (indices) should be adjusted to global ones according to the rules of Liblinear
		wordFeatures.setAdjust(adjust);
		// fill all the window's elements
		wordFeatures.fillWordFeatures(word, wordPosition, alphabet, this.trainingPhase);
		return wordFeatures;
	}

	// Print functions

	/**
	 * Only for printing the borders of the window elements
	 * @param max
	 * @param lp
	 * @param lc
	 * @param rc
	 * @param rp
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

	public void ppWindowElement(){
		for (WordFeatures element : elements){
			element.ppIthppWordFeaturesWindowElements(alphabet);
		}
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
