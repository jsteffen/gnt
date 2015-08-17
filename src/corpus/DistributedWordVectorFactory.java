package corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * Goal is to create a file that represents distributed word vectors for a given set of words.
 * The words are from a given corpus.
 * This are processed line wise, where each line corresponds to a tokenized lower-cased sentence.
 * The indicator words iw are used to define the dimension of left and right distributed vectors.
 * The index i corresponds to the rank iw_i+1, i.e., 0 == iw_1 ... n == iw_m.
 * The context vectors are extended by a n+1 element for counting omitted context (avoids zero vectors)
 * Steps:
 * - define dimension m
 * - read in indicator words make a mapping iw2num and num2iw
 * - define word2num and num2word map for the words to come.
 * - define distributedWordsTable: index corresponds to index of value of word2num; elements are instances of wordVector
 * 
 * - read in file linewise; each line corresponds to a tokenized sentence and also has <s> and </s> boundaries (implicit)
 * - iterate through token list of sentence: means words are counted from left to right which will define word2num and num2word mapping
 * - for each word w_i:
 * - check whether already in word2num -> if false initialize word2num, num2word and wordsvector -> if true retrieve wordsVector(word2num)
 * - update left and right context with w_i-1 and w_i+1 by using iw2num(w_i-1); if iw2num is NULL then update last vector cell n+1
 * - when eof is reached, compute weight for non-zero frequencies of each word vector context element
 * - finally create vocabulary file vocFilename.txt using num2word and corresponding left and right files vocVectorLeft.txt and vocVectorRight.txt 
 * - this way we obtain a static knowledge base of distributed vectors for a set of words
 * @author gune00
 *
 */
public class DistributedWordVectorFactory {
	// stores indicator word -> rank -> is needed when computing the left/right bigrams of a word
	private Map<String, Integer> iw2num = new HashMap<String, Integer>();

	// stores rank -> indicator word -> is needed for indexing the context vectors using index rank-1 
	private Map<Integer, String> num2iw = new HashMap<Integer, String>();

	// stores word -> num -> is needed for computing an index for each word type needed for accessing distributed word vector
	private Map<String, Integer> word2num = new HashMap<String, Integer>();

	// stores num -> word -> is needed for creating the vocabulary file so that position in file corresponds to index and position 
	// of left/right vector files
	private Map<Integer, String> num2word = new HashMap<Integer, String>();

	// keeps track of the word indices
	private int wordCnt = 0;

	// stores context vector of each word, whereby word is indexed using value of word2num
	// Once text is processed, table has to be sorted in increasing order
	private Map<Integer, DistributedWordVector> distributedWordsTable = new HashMap<Integer, DistributedWordVector>();

	// read ranked list of indicators words from file and construct bijective mapping of word - rank
	private void initIndicatorMap(String fileName, int dim) throws IOException {
		BufferedReader reader;
		int lineCnt = 0;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			// ignore first line
			if (lineCnt != 0){

				String[] entry = line.split("\t");
				iw2num.put(entry[0], lineCnt);
				num2iw.put(lineCnt,entry[0]);
				// stop if dim-many lines have been read
				// this means the iw2num.size() == dim
				if (lineCnt == dim) break;
			}
			lineCnt++;
		}
		reader.close();
	}

	// read file line-wise - basically the same as in indicator words creator
	public void readAndProcessInputTextLineWise(String fileName, String type){
		BufferedReader reader;
		int lineCnt = 0;
		int mod = 100000;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				lineCnt++;
				String[] words = cleanTextLine(line, type);
				sentence2Bigrams(words);
				if ((lineCnt % mod) == 0) System.out.println(lineCnt);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Assume: a line is a tokenized sentence that is a list of words separated by blank.
	// Following FLORS, lower case all words.
	private String[] cleanTextLine(String line, String type) {
		String[] words = {};
		words = line.toLowerCase().split(" ");
		return words;
	}

	// NOW, create and fill the distributed word vectors

	// Iterate from left to right through the words of a sentence and construct/extend distributed vector of each word
	// Check whether word is in first/last position of sentence

	// TODO check: 
	// by adding <s> and </s> virtually, 
	// I do NOT treat them as individual words that should be tagged but I will count them as context elements.
	// THIS also means <s> and </s> are also not part of the indicator words
	private void sentence2Bigrams(String[] sentence){
		// sentence only contains a single word (or \newline)
		if (sentence.length == 1) word2Bigram("<s>", sentence[0],"</s>");
		else
			for (int i= 0; i < sentence.length; i++){
				// two word sentence
				if (i==0) word2Bigram("<s>", sentence[0],sentence[1]);
				else
					// remaining two words
					if (i==(sentence.length-1)) word2Bigram(sentence[i-1],sentence[i],"</s>");
					else
						// all others
						word2Bigram(sentence[i-1],sentence[i],sentence[i+1]);	
			}
	}

	// Update the distributed word vector:
	// Map the word and its left/right adjacent word to integer
	// Since  context vectors start from index 0 -> increase rank by -1

	private void word2Bigram(String leftWord, String word, String rightWord) {
		// System.out.println(leftWord + "#" + word + "#" + rightWord);
		// update word2num tables and context vectors
		int wordIndex = determineWordIndex(word);
		int leftWordIndex = determineIwIndex(leftWord);
		int rightWordIndex = determineIwIndex(rightWord);
		// TAKE CARE that context vector and word list index starts from 0 -> this is why they are called with x-1
		updateDistributedWordsVector(wordIndex, leftWordIndex-1, rightWordIndex-1);
	}

	// Incrementally build a bijective mapping word-integer
	// Need this construct the liblinear integer encoding
	private int determineWordIndex(String word) {
		// lookup word -> if true -> get index, if false -> add word with wordCnt value
		int index = 0;
		if (word2num.containsKey(word)) index = word2num.get(word);
		else
		{word2num.put(word, ++wordCnt); index = wordCnt; num2word.put(wordCnt, word);
		}
		//System.out.println("Word: " + word + " WordIdx: " + index);
		return index;
	}

	// Needed for building the context vectors and for mapping contetx words to its rank
	private int determineIwIndex(String word) {
		// lookup word in iw2num -> if true -> value, if false iw2num.length+1
		int index = 0;
		if (iw2num.containsKey(word)) 
			index = iw2num.get(word);
		else
			index = iw2num.size()+1;
		//System.out.println("IW: " + word + " IwIdx: " + index);
		return index;
	}

	// Create or update the distributed word representation using the word and its current context elements
	private void updateDistributedWordsVector(int wordIndex, int leftWordIndex,
			int rightWordIndex) {
		// create or update distributedWordVector of word, which is stored in updateDistributedWordsVector.get(wordIndex)
		// NOTE: the list is generated incrementally from left to right, so that new elements are always added to the end of that array
		// updating means: create and then access word vector and then adjust left and right context vectors by
		// freq(bigram (leftWordIndex, wordIndex)) 

		//		System.out.println("Left: " + leftWordIndex+":"+num2iw.get(leftWordIndex+1)
		//				+ " Word: " + wordIndex+":"+num2word.get(wordIndex)
		//				+ " Right: " + rightWordIndex+":"+num2iw.get(rightWordIndex+1)
		//				);
		if (distributedWordsTable.containsKey(wordIndex)){
			distributedWordsTable.get(wordIndex).updateWordVector(leftWordIndex, rightWordIndex);
			//System.out.println("Old:\n" + distributedWordsTable.get(wordIndex).toStringEncoded(num2iw));
		}
		else{
			DistributedWordVector newWordVector = new DistributedWordVector(
					iw2num.size(),leftWordIndex, rightWordIndex);
			distributedWordsTable.put(wordIndex,newWordVector);
			//System.out.println("New:\n" + distributedWordsTable.get(wordIndex).toStringEncoded(num2iw));
		}
	}

	//after all distributed word vectors have been computed, compute weights for the nonzero frequencies according to tf(x) = 1 + log(x)
	private void computeDistributedWordWeights(){
		for(int key: distributedWordsTable.keySet()){
			distributedWordsTable.get(key).computeContextWeights();
		}
	}

	// after the above has been done, write out vocabulary into files:
	// Firstly, sort num2word according to natural order, and write value of entry key.
	private void writeVocabularyFile(String targetFileName){
		BufferedWriter writer;
		Map<Integer, String> sortedMap = new TreeMap<Integer, String>(num2word);
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileName),"UTF-8"));
			for(int key: sortedMap.keySet()){
				writer.write(sortedMap.get(key)+"\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// after the above has been done, write out left and right context vectors of each word in separate files.
	// The order follows the natural order in num2word, i.e., x-th  left context vector belongs x-th word. 
	// Same for right context.
	// Hence, sort distributedWordsTable according to natural order, and write value of entry key.
	// To save space, only write non-zero weights in form of iw_index:weight -> leads to much smaller files
	
	private void writeLeftRightContextFile(String leftFilename, String rightFilename){
		BufferedWriter leftWriter, rightWriter;
		Map<Integer, DistributedWordVector> sortedMap = new TreeMap<Integer, DistributedWordVector>(distributedWordsTable);
		try {
			leftWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(leftFilename),"UTF-8"));
			rightWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rightFilename),"UTF-8"));
			for(int key: sortedMap.keySet()){
				leftWriter.write(sortedMap.get(key).toLeftContextIndex()+"\n");
				rightWriter.write(sortedMap.get(key).toRightContextIndex()+"\n");
			}
			leftWriter.close();
			rightWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeFlorsCondensed(){
		System.out.println("Write FLORS condensed ...");
		System.out.println("Write out vocabulary file.");
		this.writeVocabularyFile("/Users/gune00/data/wordVectorTests/vocFile.txt");
		System.out.println("Write out left/right context vector files.");
		this.writeLeftRightContextFile("/Users/gune00/data/wordVectorTests/vocLeftContext.txt", 
				"/Users/gune00/data/wordVectorTests/vocRightContext.txt");
		System.out.println("Done!");
	}
	

	// TODO
	/* Define also word2vec and glove based output:
	 * word2vec:
	 * 	first line: number of words, dimension
	 * 	other lines: word + vector
	 * Glove:
	 * 	all lines: word + vector
	 */
	

	// Test main method
	public static void main(String[] args) throws IOException {
		DistributedWordVectorFactory dwvFactory = new DistributedWordVectorFactory();
		System.out.println("Read indicator words sorted acoording to rank.");
		dwvFactory.initIndicatorMap("resources/iw.txt", 500);
		
		System.out.println("Read unlabeled files of sentences and create word vectors.");
		dwvFactory.readAndProcessInputTextLineWise("/Users/gune00/data/BioNLPdata/CoNLL2007/ptb/unlab/english_ptb_unlab", "ptb");
		dwvFactory.computeDistributedWordWeights();

		dwvFactory.writeFlorsCondensed();
	}

}
