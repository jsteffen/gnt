package features;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import corpus.Corpus;

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

public class WordSuffixFeatureFactory {
	// stores indicator word -> rank -> is needed when computing the left/right bigrams of a word
	private Map<String, Integer> suffix2num = new HashMap<String, Integer>();
	// stores rank -> indicator word -> is needed for indexing the context vectors using index rank-1 
	
	// TODO later only used for ppPrint
	private Map<Integer, String> num2suffix = new TreeMap<Integer, String>();

	private int wordCnt = 0;
	private int suffixCnt = 0;

	public Map<String, Integer> getSuffix2num() {
		return suffix2num;
	}
	public void setSuffix2num(Map<String, Integer> suffix2num) {
		this.suffix2num = suffix2num;
	}
	public Map<Integer, String> getNum2suffix() {
		return num2suffix;
	}
	public void setNum2suffix(Map<Integer, String> num2suffix) {
		this.num2suffix = num2suffix;
	}

	public void clean(){
		num2suffix = new TreeMap<Integer, String>();
	}
	
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

	public void readFlorsCorpus(Corpus corpus){
		for (String fileName : corpus.trainingUnLabeledData){
			System.out.println(fileName);
			// read only first file
			createSuffixListFromFile(fileName, -1);
			break;
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
		if (!this.getSuffix2num().containsKey(suffix)){
			if (i==0) wordCnt++;

			this.suffixCnt++;
			this.getSuffix2num().put(suffix, suffixCnt);
			num2suffix.put(suffixCnt, suffix);
		}
	}

	//**
	// after the above has been done, write out vocabulary into files:
	// Firstly, sort num2word according to natural order, and write value of entry key.
	private void writeSuffixFile(String targetFileName){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileName),"UTF-8"));
			for(int key: num2suffix.keySet()){
				writer.write(num2suffix.get(key)+"\n");
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
				this.getSuffix2num().put(line, cnt);
				this.getNum2suffix().put(cnt,line);
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
	public Integer getLongestKnownSuffixForWord(String word){
		int suffixIndex = -1;
		for (int i = 0; i < word.length(); i++){
			String suffix = word.substring(i);
			if (this.getSuffix2num().containsKey(suffix)) {
				suffixIndex = this.getSuffix2num().get(suffix);
				break;
			}
		}
		return suffixIndex;
	}

	/**
	 * Given a word, find all matching suffixes from the known suffix list
	 * @param word
	 * @return
	 */
	public List<Integer> getAllKnownSuffixForWord(String word){
		List<Integer> indices = new ArrayList<Integer>();
		if (!isNumber(word))
			for (int i = 0; i < word.length(); i++){
				String suffix = word.substring(i);
				if (this.getSuffix2num().containsKey(suffix)) {
					indices.add(this.getSuffix2num().get(suffix));
				}
			}
		indices.sort(null);
		return indices;
	}

	//** tests methods
	public void testWriteSuffixList(){
		
		createSuffixListFromFile("resources/data/english/ptb3-std-training-sents.txt", -1);
		// createSuffixListFromFile("resources/data/ptb/unlab/english_ptb_unlab", -1);
		
		System.out.println("#word: " + this.wordCnt + 
				" #suffixes: " + this.suffixCnt);
		System.out.println("Writing suffix list to: " + "resources/features/suffixList.txt");
		this.writeSuffixFile("resources/features/suffixList.txt");
		System.out.println("... done");
	}

	public void testReadSuffixList(){
		System.out.println("Reading suffix list from: " + "resources/features/suffixList.txt");
		this.readSuffixFile("resources/features/suffixList.txt");
		System.out.println("... done");
	}

	public static void main(String[] args){
		WordSuffixFeatureFactory wordSuffixFactory = new WordSuffixFeatureFactory();
		wordSuffixFactory.testWriteSuffixList();
	}
}
