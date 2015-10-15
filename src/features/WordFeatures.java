package features;

import java.util.ArrayList;
import java.util.List;

import data.Alphabet;
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
	private String word = "";
	private int index = 0;
	private int elementOffset = 0;
	private int leftOffset = 0;
	private int rightOffset = 0;
	private int shapeOffset = 0;
	private int suffixOffset = 0;
	private boolean adjust = false;
	private int length = 0;


	private List<Pair<Integer,Double>> left = new ArrayList<Pair<Integer,Double>>();
	private List<Pair<Integer,Double>> right = new ArrayList<Pair<Integer,Double>>();
	private List<Pair<Integer,Boolean>> suffix = new ArrayList<Pair<Integer,Boolean>>();
	// I am using here a list although we only have always one shape
	private List<Pair<Integer,Boolean>> shape = new ArrayList<Pair<Integer,Boolean>>();
	
	public static boolean withWordFeats = true;
	public static boolean withShapeFeats = true;
	public static boolean withSuffixFeats = true;

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
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	// Instantiation
	public WordFeatures(String word) {
		this.word = word;
	}

	// Methods

//	public void setOffSets() {
//		elementOffset = (index * OffSets.tokenVectorSize) + 1;
//		leftOffset = elementOffset + 0; // Plus one here is needed to make sure that indices in Liblinear start from 1 and not 0
//		rightOffset = leftOffset + 0 + OffSets.wvLeftSize;
//		shapeOffset = rightOffset + 0 + OffSets.wvRightSize;
//		suffixOffset = shapeOffset + 0 + OffSets.shapeSize - 1;
//	}
	
	// GN on 14.10.2015
	// I have to shift offset by OffSets.tokenVectorSize + 1, and so later have to remove the +1
	public void setOffSets() {
		elementOffset = (index * OffSets.tokenVectorSize) + 1;
		leftOffset = elementOffset;
		rightOffset = leftOffset + OffSets.wvLeftSize;
		shapeOffset = rightOffset + OffSets.wvRightSize;
		suffixOffset = shapeOffset + OffSets.shapeSize - 1;
	}

	public void fillWordFeatures(String word, int index, Alphabet alphabet, boolean train){
		// if word is a sentence padding element, then just return an empty WordFeatures
		if (word.endsWith("<BOUNDARY>")) return;
		
		if (WordFeatures.withWordFeats) {
			fillLeftDistributedWordFeatures(word, alphabet, train, true);
			fillRightDistributedWordFeatures(word, alphabet, train, true);
		}
		if (WordFeatures.withShapeFeats)
			fillShapeFeatures(word, index, alphabet, true);
		if (WordFeatures.withSuffixFeats)
			fillSuffixFeatures(word, alphabet, true);
	}

	// boolean flag offline means: assume that features have been pre-loaded into to memory
	// boolean train means: training phase, which means do not handle unknown words
	// since word is from input stream, need to lower-case it first
	private void fillLeftDistributedWordFeatures(String word,
			Alphabet alphabet, boolean train, boolean offline) {
		String lowWord = word.toLowerCase();
		WordDistributedFeature distributedWordVector = alphabet.getWordVectorFactory().getWordVector(lowWord, train);
		for (int i = 0; i < distributedWordVector.getLeftContext().length; i++){
			int index = ((this.isAdjust())?(this.leftOffset+i):i);
			double value = distributedWordVector.getLeftContext()[i];
			if (value > 0) {
				Pair<Integer,Double> node = new Pair<Integer,Double>(index, value);
				left.add(node);
			}
		}
		length += left.size();
	}

	private void fillRightDistributedWordFeatures(String word,
			Alphabet alphabet, boolean train, boolean offline) {
		// since word is from input stream, need to lower-case it first
		String lowWord = word.toLowerCase();
		WordDistributedFeature distributedWordVector = alphabet.getWordVectorFactory().getWordVector(lowWord, train);
		for (int i = 0; i < distributedWordVector.getRightContext().length; i++){
			int index = ((this.isAdjust())?(this.rightOffset+i):i);
			double value = distributedWordVector.getRightContext()[i];
			if (value > 0) {
				Pair<Integer,Double> node = new Pair<Integer,Double>(index, value);
				right.add(node);
			}
		}
		length += right.size();
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
	// TODO: it is an overhead to keep a list of shapes, because we always have a single element
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
			// we have an unknown signature, so we cannot add it to the list
			// which basically means that shape-size() will remain 0
			System.err.println("Word: " + word + " at loc: " + index + ": unknown signature!");

		}
		// is always 1
		length += shape.size();
	}

	// boolean flag offline means: assume that suffixes have been preprocessed 
	// and pre-loaded into to memory
	private void fillSuffixFeatures(String word, Alphabet alphabet, boolean offline) {
		/*
		 * Lowercase word
		 * Create all suffixes for word
		 * Loop them up
		 * Return suffixes list of Pairs (index, true)
		 */
		// since word is from input stream, need to lower-case it first
		String lowWord = word.toLowerCase();
		List<Integer> suffixIndices = alphabet.getWordSuffixFactory().getAllKnownSuffixForWord(lowWord);
		//if (suffixIndices.isEmpty()) System.err.println("No known suffixes: " + word);
		for (int x : suffixIndices){
			int realIndex = (this.isAdjust())?(this.suffixOffset+x):x;
			Pair<Integer,Boolean> node = new Pair<Integer,Boolean>(realIndex, true);
			suffix.add(node);
		}
		// this means that if word has no known suffix, then suffix list is 0
		length += suffix.size();
	}

	// This is a way to show the words/labels that correspond to a window
	public void ppIthppWordFeaturesWindowElements(Alphabet alphabet){
		System.out.println(" Word: " + word + "; Element: " + index + "; Length: " + length);
		System.out.println("\nLeft:");
		for (Pair<Integer,Double> pair : this.getLeft()){
			int index = (this.isAdjust())?(pair.getL()-this.leftOffset):(pair.getL());
			double value = pair.getR();
			// index+1 plus is needed because left is a vector with index starting from 0, but I start iw words from 1
			String label = alphabet.getWordVectorFactory().getNum2iw().get(index+1);
			System.out.print(label+":"+index+":"+value+";");
		}
		System.out.println("\nRight:");
		for (Pair<Integer,Double> pair : this.getRight()){
			int index = (this.isAdjust())?(pair.getL()-this.rightOffset):(pair.getL());
			double value = pair.getR();
			// index+1 plus is needed because left is a vector with index starting from 0, but I start iw words from 1
			String label = alphabet.getWordVectorFactory().getNum2iw().get(index+1);
			System.out.print(label+":"+index+":"+value+";");
		}
		System.out.println("\nShape:");
		for (Pair<Integer,Boolean> pair : this.getShape()){
			int index = (this.isAdjust())?(pair.getL()-this.shapeOffset):(pair.getL());
			boolean value = pair.getR();
			String label = alphabet.getWordShapeFactory().getIndex2signature().get(index);
			System.out.print(label+":"+index+":"+value+";");
		}
		System.out.println("\nSuffix:");
		for (Pair<Integer,Boolean> pair : this.getSuffix()){
			int index = (this.isAdjust())?(pair.getL()-this.suffixOffset):(pair.getL());
			boolean value = pair.getR();
			String label = alphabet.getWordSuffixFactory().getNum2suffix().get(index);
			System.out.print(label+":"+index+":"+value+";");
		}
		System.out.println("\n************");
	}

	// String representations

	public static String toActiveFeatureString(){
		String output = "\nActive features\n";
		output += "withWordFeats= 	" + withWordFeats +"\n";
		output += "withShapeFeats=  " + withShapeFeats +"\n";
		output += "withSuffixFeats= " + withSuffixFeats +"\n";
		return output;	
	}
	
	public String toOffSetsString(){
		String output = "\nElement-"+this.getIndex()+"\n";
		output += "OffSets:\n";
		output += "Element: " + this.elementOffset +"\n";
		output += "Length: " + this.length +"\n";
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
