package data;

import java.util.ArrayList;
import java.util.List;

import features.Alphabet;
import features.WordFeatures;

/**
 * Create local context window for tagging t_i of size 2*windowSize+1 centered around t_i.
 * The idea of the approach is as follow:
 * - determine number of padding and context elements for center element i
 * - for each element of window, create a list of elements which keeps
 * - the word features consisting of its components:
 * - left distributed word features
 * - right distributed word features 
 * - shape features
 * - suffix features
 * 
 * Thus each window consists of 2*windowSize +1 elements of WordFeatures
 * Each component will be represented by a list of tuples consisting of feature index and value.
 * Use sentence pads "<BOUNDARY>" for ensuring sufficient context for all words. 
 * I will do in that way, that I treat pads as empty WordFeatures with word index -1, but which are needed to 
 * make sure that offSets are computed correctly
 * 
 * @author gune00
 *
 */
public class Window {
	private Data data ;
	private Alphabet alphabet ;
	private Sentence sentence;
	private int center;
	private int size = 0;
	private List<WordFeatures> elements = new ArrayList<WordFeatures>();
	private int length = 0;

	public Window(Sentence sentence, int i, int windowSize, Data data,
			Alphabet alphabet) {
		this.size = windowSize;
		this.length = 2 * windowSize + 1;
		this.alphabet = alphabet;
		this.data = data;
		this.sentence = sentence;
		this.center = i;
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
	public void fillWindow(){
		int max = this.sentence.getWordArray().length-1;
		int lp = (this.center < this.size)?(this.size-this.center):0;
		int lc = (this.size-lp);
		int rc = ((max-this.center) < this.size)?(max-this.center):this.size;
		int rp = (this.size-rc);

		// printWindowIntervalInfo(max, lp, lc, rc, rp);
		
		//TODO iterate through window elements and call fillWindowElement()
		
		
	}
	
	

	public void printWindowIntervalInfo(int max, int lp, int lc, int rc, int rp){
		// print intervals
		System.out.println("max: " + max + " center: " + this.center);
		System.out.println("lp: " + lp + " lc: " + lc);
		System.out.println("rc: " + rc + " rp: " + rp);

		// print content of intervals

		String testString = "";
		// left sentence pads
		for (int i = 0 ; i < lp; i++) 
			testString += "<s> ";
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
			testString += "</s> ";
		testString +="\n";

		System.out.println(testString);

	}

}
