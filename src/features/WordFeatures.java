package features;

import java.util.ArrayList;
import java.util.List;

import data.OffSets;
import data.Pair;

/**
 * A word features consists  of its components:
 * - left distributed word features
 * - right distributed word features 
 * - shape features
 * - suffix features
 * 
 * they are represented by a list of pairs using relative index and value.
 * Where value can be double or boolean
 *  @author gune00
 *
 */
public class WordFeatures {
	private int index = -1;
	private int elementOffset = 0;
	private int leftOffset = 0;
	private int rightOffset = 0;
	private int shapeOffset = 0;
	private int suffixOffset = 0;
	private boolean adjust = false;


	private List<Pair<Integer,Double>> left = new ArrayList<Pair<Integer,Double>>();
	private List<Pair<Integer,Double>> right = new ArrayList<Pair<Integer,Double>>();
	private List<Pair<Integer,Boolean>> suffix = new ArrayList<Pair<Integer,Boolean>>();
	private List<Pair<Integer,Boolean>> shape = new ArrayList<Pair<Integer,Boolean>>();

	//Setters and getters
	public List<Pair<Integer, Double>> getLeft() {
		return left;
	}
	public void setLeft(List<Pair<Integer, Double>> left) {
		this.left = left;
	}
	public List<Pair<Integer, Double>> getRight() {
		return right;
	}
	public void setRight(List<Pair<Integer, Double>> right) {
		this.right = right;
	}
	public List<Pair<Integer, Boolean>> getSuffix() {
		return suffix;
	}
	public void setSuffix(List<Pair<Integer, Boolean>> suffix) {
		this.suffix = suffix;
	}
	public List<Pair<Integer, Boolean>> getShape() {
		return shape;
	}
	public void setShape(List<Pair<Integer, Boolean>> shape) {
		this.shape = shape;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean isAdjust() {
		return adjust;
	}
	public void setAdjust(boolean adjust) {
		this.adjust = adjust;
	}
	// Instantiation
	public WordFeatures() {
	}

	// Methods

	public void setOffSets() {
		elementOffset = index * OffSets.tokenVectorSize;
		leftOffset = elementOffset;
		rightOffset = leftOffset + 1 + OffSets.wvLeftSize-1;
		shapeOffset = rightOffset + 1 + OffSets.wvRightSize-1;
		suffixOffset = shapeOffset + 1 + OffSets.shapeSize-1;
	}

	public void fillWordFeatures(String word, int index, Alphabet alphabet, boolean train){
		// if word is a sentence padding element, then just return an empty WordFeatures
		if (word.endsWith("<BOUNDARY>")) return;
		fillLeftDistributedWordFeatures(word, alphabet, train, true);
		fillRightDistributedWordFeatures(word, alphabet, train, true);
		fillShapeFeatures(word, index, alphabet, true);
		fillSuffixFeatures(word, alphabet, true);

	}

	// boolean flag offline means: assume that features have been pre-loaded into to memory
	// boolean train means: training phase, which means do not handle unknown words
	private void fillLeftDistributedWordFeatures(String word,
			Alphabet alphabet, boolean train, boolean offline) {
		String lowWord = word.toLowerCase();
		WordDistributedFeature distributedWordVector = alphabet.getWordVectorFactory().getWordVector(lowWord, train);
		for (int i = 0; i < distributedWordVector.getLeftContext().length; i++){
			int index = (this.isAdjust())?(this.leftOffset+i):i;
			double value = distributedWordVector.getLeftContext()[i];
			if (value > 0) {
				Pair<Integer,Double> node = new Pair<Integer,Double>(index, value);
				left.add(node);
			}
		}
	}

	private void fillRightDistributedWordFeatures(String word,
			Alphabet alphabet, boolean train, boolean offline) {
		String lowWord = word.toLowerCase();
		WordDistributedFeature distributedWordVector = alphabet.getWordVectorFactory().getWordVector(lowWord, train);
		for (int i = 0; i < distributedWordVector.getRightContext().length; i++){
			int index = (this.isAdjust())?(this.rightOffset+i):i;
			double value = distributedWordVector.getRightContext()[i];
			if (value > 0) {
				Pair<Integer,Double> node = new Pair<Integer,Double>(index, value);
				right.add(node);
			}
		}
	}

	/** 
	 * boolean flag offline means: assume that known signatures have been pre-loaded into to memory
	 * NOTE: even in training phase, signature are computed dynamically 
	 * NOTE: we assume that a word has a unique signature so the list shape actually only contains a single element.
	 * @param word
	 * @param index
	 * @param alphabet
	 * @param offline
	 */
	private void fillShapeFeatures(String word, int index, Alphabet alphabet, boolean offline) {
		int wordShapeIndex = alphabet.getWordShapeFactory().getShapeFeature(word, index);
		if (wordShapeIndex > -1) {
			//System.out.println("Word: " + word + " Shape: " + alphabet.getWordShapeFactory().getIndex2signature().get(wordShapeIndex));
			int realIndex = (this.isAdjust())?(this.shapeOffset+wordShapeIndex):wordShapeIndex;
			Pair<Integer,Boolean> node = new Pair<Integer,Boolean>(realIndex, true);
			shape.add(node);
		}
		else
		{
			System.err.println("Word: " + word + " at loc: " + index + " has unknown signature !");
			shape = null;
		}

	}

	// boolean flag offline means: assume that suffixes have been preprocessed and pre-loaded into to memory
	private void fillSuffixFeatures(String word, Alphabet alphabet, boolean offline) {
		/*
		 * Lowercase word
		 * Create all suffixes for word
		 * Loop them up
		 * Return suffixes list of Pairs (index, true)
		 */
		String lowWord = word.toLowerCase();
		List<Integer> suffixIndices = alphabet.getWordSuffixFactory().getAllKnownSuffixForWord(lowWord);
		for (int x : suffixIndices){
			int realIndex = (this.isAdjust())?(this.suffixOffset+x):x;
			Pair<Integer,Boolean> node = new Pair<Integer,Boolean>(realIndex, true);
			suffix.add(node);
		}
	}

	// This is a way to show the words/labels that correspond to a window
	// TODO add the words of the window as well
	// I need this to see whether my indexing is correct
	public void ppIthppWordFeaturesWindowElements(Alphabet alphabet){
		System.out.println("Element: " + index);
		System.out.println("\nLeft:\n");
		for (Pair<Integer,Double> pair : this.getLeft()){
			int index = (this.isAdjust())?(pair.getL()-this.leftOffset):(pair.getL());
			double value = pair.getR();
			// index+1 plus is needed because left is a vector with index tsarting from 0, but I start iw words from 1
			String label = alphabet.getWordVectorFactory().getNum2iw().get(index+1);
			System.out.print(label+":"+index+":"+value+";");
		}
		System.out.println("\nRight:\n");
		for (Pair<Integer,Double> pair : this.getRight()){
			int index = (this.isAdjust())?(pair.getL()-this.rightOffset):(pair.getL());
			double value = pair.getR();
			// index+1 plus is needed because left is a vector with index tsarting from 0, but I start iw words from 1
			String label = alphabet.getWordVectorFactory().getNum2iw().get(index+1);
			System.out.print(label+":"+index+":"+value+";");
		}
		System.out.println("\nShape:\n");
		for (Pair<Integer,Boolean> pair : this.getShape()){
			int index = (this.isAdjust())?(pair.getL()-this.shapeOffset):(pair.getL());
			boolean value = pair.getR();
			String label = alphabet.getWordShapeFactory().getIndex2signature().get(index);
			System.out.print(label+":"+index+":"+value+";");
		}
		System.out.println("\nSuffix:\n");
		for (Pair<Integer,Boolean> pair : this.getSuffix()){
			int index = (this.isAdjust())?(pair.getL()-this.suffixOffset):(pair.getL());
			boolean value = pair.getR();
			String label = alphabet.getWordSuffixFactory().getNum2suffix().get(index);
			System.out.print(label+":"+index+":"+value+";");
		}	
		System.out.println("\n");
	}

	// String representations
	
	public String toOffSetsString(){
		String output = "\nElement-"+this.getIndex()+"\n";
		output += "OffSets:\n";
		output += "Element: " + this.elementOffset +"\n";
		output += "Left: " + this.leftOffset +"\n";
		output += "Right: " + this.rightOffset +"\n";
		output += "Shape: " + this.shapeOffset +"\n";
		output += "Suffix: " + this.suffixOffset +"\n";
		return output;	
	}

	public String toString(){
		String output = "\nElement-"+this.getIndex();
		output += this.toOffSetsString();
		output +="\nLeft: ";
		for (Pair<Integer,Double> pair : this.left){
			output+=pair.toString();
		}
		output +="\nRight: ";
		for (Pair<Integer,Double> pair : this.right){
			output+=pair.toString();
		}
		output +="\nShape: ";
		for (Pair<Integer,Boolean> pair : this.shape){
			output+=pair.toString();
		}
		output +="\nSuffix: ";
		for (Pair<Integer,Boolean> pair : this.suffix){
			output+=pair.toString();
		}

		return output;
	}
}
