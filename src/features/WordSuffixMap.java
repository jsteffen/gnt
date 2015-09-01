package features;

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
 * Goal is to compute all lower-case suffixes from a training set of words.
 * For suffix s, we set the dimension corresponding to s in f_suffix(w) to 1 if lowercased w ends in s 
 * and to 0 otherwise. Note that w is a suffix of itself.
 * In FLORS: 91,161 suffix features from PTB - I guess - and PTB as about 32.500 words.
 * As far as I understand, a word should match just one suffix, so we have a very sparse vector in principle
 * but we can represent it for each word good, if we only have non-zero feature values.
 * For training it means we can determine, which suffix is particular for some POS tag, and for testing, we
 * simple compute it.
 * 
 * Current result when using PTB version three: #word: 32491 #suffixes: 98358
 * I have different numbers. I do not know how how exactly Flors defines word, but so maybe they have some filters.
 * I will use it, e.g., no number tokens or other specialized tokens.
 * I filter string which starts and ends with a digit.
 * This gives: #word: 28481 #suffixes: 91144
 * 
 * NOTE: in principle: file operation not really needed here, because will be later done in one training class
 * 
 * @author gune00
 *
 */

public class WordSuffixMap {
	// stores indicator word -> rank -> is needed when computing the left/right bigrams of a word
	private Map<String, Integer> suffix2num = new HashMap<String, Integer>();
	// stores rank -> indicator word -> is needed for indexing the context vectors using index rank-1 
	private Map<Integer, String> num2suffix = new HashMap<Integer, String>();

	private int wordCnt = 0;
	private int suffixCnt = 0;

	public void createSuffixListFromFile(String fileName, int max){
		BufferedReader reader;
		int lineCnt = 0;
		int mod = 10000;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));

			// Each line consists of a sequence of words
			String line;
			while ((line = reader.readLine()) != null) {
				if  ((max > 0) && (lineCnt >= max)) break;
				lineCnt++;
				// lower case line and split off words
				String[] words = line.toLowerCase().split(" ");
				// then compute suffixes
				computeSuffixesFromWords(words);
				if ((lineCnt % mod) == 0) System.out.println(lineCnt);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void computeSuffixesFromWords(String[] words) {
		for (String word : words){
			if (!isNumber(word)) 
				computeSuffixesAndStore(word);
		}
	}

	// A number is a string which starts and ends with a digit
	// This is used to filter out strings for which we do not want to compute suffixes, e.g., numbers
	private boolean isNumber(String word) {
		char lastChar = word.charAt(word.length()-1);
		char firstChar = word.charAt(0);
		return (Character.isDigit(lastChar) &&
				Character.isDigit(firstChar));
	}

	// compute all suffixes of a word starting from 0, which means the word is a suffix of itself
	// 
	private void computeSuffixesAndStore(String word) {
		// Smallest suffix is just last character of a word
		for (int i = 0; i < word.length(); i++){
			String suffix = word.substring(i);
			updateSuffixTable(suffix, i);
		}
	}

	private void updateSuffixTable(String suffix, int i) {
		if (!suffix2num.containsKey(suffix)){
			if (i==0) wordCnt++;

			this.suffixCnt++;
			suffix2num.put(suffix, suffixCnt);
			num2suffix.put(suffixCnt, suffix);
		}
	}

	//**
	// after the above has been done, write out vocabulary into files:
	// Firstly, sort num2word according to natural order, and write value of entry key.
	private void writeSuffixFile(String targetFileName){
		BufferedWriter writer;
		Map<Integer, String> sortedMap = new TreeMap<Integer, String>(num2suffix);
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

	//***
	// read preprocessed suffix list from file
	private void readSuffixFile(String string) {
		BufferedReader reader;
		int cnt = 1;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(string),"UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				suffix2num.put(line, cnt);
				num2suffix.put(cnt,line);
				cnt++;
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//*** basic tests
	/**
	 * Given a word, find the index of the longest matching suffix from the known suffix list
	 * @param word
	 * @return
	 */
	public Integer getKnownSuffixForWord(String word){
		int suffixIndex = -1;
		for (int i = 0; i < word.length(); i++){
			String suffix = word.substring(i);
			if (suffix2num.containsKey(suffix)) {
				suffixIndex = suffix2num.get(suffix);
				break;
			}
		}
		return suffixIndex;
	}

	/**
	 * Given a word and a suffix, first check whether suffix is a known one and
	 * if word contains it
	 * @param word
	 * @param suffix
	 * @return
	 */
	public boolean hasKnownSuffix(String word, String suffix){
		return (
				suffix2num.containsKey(suffix) &&
				suffix.equals(word.substring((word.length()-suffix.length())))
				);
	}

	//** tests methods
	public void testWriteSuffixList(){
		this.createSuffixListFromFile("/Users/gune00/data/MLDP/english/english-train-sents.txt", -1);
		//this.createSuffixListFromFile("/Users/gune00/data/BioNLPdata/CoNLL2007/ptb/unlab/english_ptb_unlab", 100000);
		System.out.println("#word: " + this.wordCnt + 
				" #suffixes: " + this.suffixCnt);
		System.out.println("Writing suffix list to: " + "/Users/gune00/data/wordVectorTests/suffixList.txt");
		this.writeSuffixFile("/Users/gune00/data/wordVectorTests/suffixList.txt");
		System.out.println("... done");
	}

	public void testReadSuffixList(){
		System.out.println("Reading suffix list from: " + "/Users/gune00/data/wordVectorTests/suffixList.txt");
		this.readSuffixFile("/Users/gune00/data/wordVectorTests/suffixList.txt");
		System.out.println("... done");
	}

	public static void main(String[] args){
		WordSuffixMap wordVector = new WordSuffixMap();
		wordVector.testWriteSuffixList();
		int suffixIndex = wordVector.getKnownSuffixForWord("bush");
		if (suffixIndex > -1) System.out.println(wordVector.num2suffix.get(suffixIndex));
		System.out.println(wordVector.hasKnownSuffix("xbush", "ush"));
	}
}