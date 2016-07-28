package features;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import archive.Archivator;
import data.GlobalParams;

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

	// later only used for ppPrint
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
	public int getWordCnt() {
		return wordCnt;
	}
	public void setWordCnt(int wordCnt) {
		this.wordCnt = wordCnt;
	}
	public int getSuffixCnt() {
		return suffixCnt;
	}
	public void setSuffixCnt(int suffixCnt) {
		this.suffixCnt = suffixCnt;
	}
	public void clean(){
		num2suffix = new TreeMap<Integer, String>();
	}

	// A simple flag for switching between suffix and ngram computation
	public static boolean ngram = false;
	public static int ngramSize = 3;
	
	private String featureFilePathname = "";

	public String getFeatureFilePathname() {
		return featureFilePathname;
	}
	public void setFeatureFilePathname(String featureFilePathname) {
		this.featureFilePathname = featureFilePathname;
	}

	// Constructor
	public WordSuffixFeatureFactory() {
	}
	
	public WordSuffixFeatureFactory(String featureFilePathname2) {
		this.setFeatureFilePathname(featureFilePathname2);
	}
	
	
	/*
	 * Methods
	 */

	//*********************** Computation of ngrams *********************** 
	/**
	 * Compute ngrams from given word
	 */
	private Set<String> generateNgrams(String word, int gramSize) {
		String realWord = "$$" + word + "$$";
		int start = 0;
		int end = start + gramSize - 1;
		Set<String> ngrams = new HashSet<String>();

		while (end < realWord.length()) {
			ngrams.add(realWord.substring(start, end + 1));
			start++;
			end++;
		}
		return ngrams;
	}

	public List<Integer> getAllKnownNgramsForWord(String word){
		List<Integer> indices = new ArrayList<Integer>();
		Set<String> ngrams = this.generateNgrams(word, WordSuffixFeatureFactory.ngramSize);
		for (String ngram : ngrams){
			if (!isNonWord(ngram)){
				if (this.getSuffix2num().containsKey(ngram)) {
					indices.add(this.getSuffix2num().get(ngram));
				}
			}
		}
		indices.sort(null);
		return indices;
	}

	private void computeNgramsAndStore(String word) {
		int i = 0;
		Set<String> ngrams = this.generateNgrams(word, WordSuffixFeatureFactory.ngramSize);
		for (String ngram : ngrams){
			if (!isNonWord(ngram)) 
				updateSuffixTable(ngram, i);
			i++;
		}
	}


	//*********************** Computation of suffixes *********************** 

	/**
	 * Given a word, find all matching suffixes from the known suffix list.
	 * Used in {@link features.WordSuffixFeatureFactory#getAllKnownSuffixForWord(String)}
	 * @param word
	 * @return
	 */

	private List<Integer> getAllKnownSuffixForWordIntern(String word){
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < word.length(); i++){
			String suffix = word.substring(i);
			if (!isNonWord(suffix)){
				if (this.getSuffix2num().containsKey(suffix)) {
					indices.add(this.getSuffix2num().get(suffix));
				}
			}
		}
		indices.sort(null);
		return indices;
	}

	/** 
	 * compute all suffixes of a word starting from 0, which means the word is a suffix of itself.
	 * If suffix is not a word, then do not store it.
	 * @param word
	 */
	private void computeSuffixesAndStore(String word) {
		// Smallest suffix is just last character of a word
		for (int i = 0; i < word.length(); i++){
			String suffix = word.substring(i);
			if (!isNonWord(suffix)) 
				updateSuffixTable(suffix, i);
		}
	}

	// ************************** Inserting or Updating extracted suffix/ngram **************************

	/** A number is a string which starts and ends with a digit.
	 * This is used to filter out strings for which we do not want to compute suffixes, e.g., numbers
	 * 
	 * @param word
	 * @return
	 */
	private boolean isNumber(String word) {
		char lastChar = word.charAt(word.length()-1);
		char firstChar = word.charAt(0);
		return (Character.isDigit(lastChar) 
				&& Character.isDigit(firstChar)
				);
	}

	private boolean hasLastNonLetter(String word) {
		char lastChar = word.charAt(word.length()-1);
		return !Character.isLetter(lastChar);
	}

	private boolean hasOnlyNonLetters(String token){
		boolean isValid = true;
		for (int i=0 ; i < token.length(); i++){
			char curChar = token.charAt(i);
			if (Character.isLetter(curChar)) {
				isValid = false; break;
			}
		}
		return isValid;
	}
	/**
	 * Returns true if token is not a word.
	 * @param token
	 * @return
	 */
	private boolean isNonWord(String token){
		return (
				false
				//				(token.length() < 3) ||
				//				 hasLastNonLetter(token)
				//								|| hasOnlyNonLetters(token) 
				//								|| isNumber(token)
				);
	}

	private void updateSuffixTable(String suffix, int i) {
		if (!this.getSuffix2num().containsKey(suffix)){
			if (i==0) wordCnt++;

			this.suffixCnt++;
			this.getSuffix2num().put(suffix, suffixCnt);
			num2suffix.put(suffixCnt, suffix);
		}
	}

	//*********************** generic caller *********************** 

	/**
	 * Receives a list of token, and computes suffixes for each token.
	 * @param words
	 */
	private void computeSubstringsFromWords(String[] words) {
		for (String word : words){
			if (WordSuffixFeatureFactory.ngram)
				computeNgramsAndStore(word);
			else{
				computeSuffixesAndStore(word);
			}
		}
	}

	/**
	 * Main caller that switches between suffix computation and ngram computation
	 * @param word
	 * @return
	 */

	//TODO
	// after adding also ngrams, indices must be sorted again!!
	public List<Integer> getAllKnownSubstringsForWord(String word){
		List<Integer> indices = new ArrayList<Integer>();
		if (WordSuffixFeatureFactory.ngram)
			indices = getAllKnownNgramsForWord(word);
		else
		{
			indices = getAllKnownSuffixForWordIntern(word);
			//indices.addAll(getAllKnownNgramsForWord(word));
		}
		return indices;
	}

	//*********************** creating and storing *********************** 

	public void createAndSaveSuffixFeature(Archivator archivator, String taggerName, String trainingFileName){
		System.out.println("Create suffix list from: " + trainingFileName);
		this.createSuffixListFromFile(trainingFileName, -1);

		System.out.println("#word: " + this.getWordCnt()+" #suffixes: " + this.getSuffixCnt());

		String suffixFileName = this.getFeatureFilePathname()+taggerName+"/suffixList.txt";
		System.out.println("Writing suffix list to: " + suffixFileName);
		this.writeSuffixFile(suffixFileName);
		System.out.println("... done");
		// Add file to archivator
		archivator.getFilesToPack().add(suffixFileName);
	}


	private void createSuffixListFromFile(String file, int max){
		BufferedReader reader;
		int lineCnt = 0;
		int mod = 10000;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

			// Each line consists of a sequence of words
			String line;
			while ((line = reader.readLine()) != null) {
				if  ((max > 0) && (lineCnt >= max)) break;
				lineCnt++;
				// lower case line and split off words
				String[] words = line.toLowerCase().split(" ");
				// then compute suffixes
				computeSubstringsFromWords(words);
				if ((lineCnt % mod) == 0) System.out.println(lineCnt);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	//**
	// after the above has been done, write out vocabulary into files:
	// Firstly, sort num2word according to natural order, and write value of entry key.
	private void writeSuffixFile(String targetFileName){
		File file = new File(targetFileName);
		file.getParentFile().mkdirs();

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
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
	
	private void readSuffixFile(Archivator archivator, String string) {
		BufferedReader reader;
		int cnt = 1;
		try {
			InputStream inputStream = archivator.getArchiveMap().get(string);
			reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
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

	public void readSuffixList(String taggerName){
		String suffixFileName = this.getFeatureFilePathname()+taggerName+"/suffixList.txt";
		System.out.println("Reading suffix list from: " + suffixFileName);
		this.readSuffixFile(suffixFileName);
		System.out.println("... done");
	}
	
	public void readSuffixList(Archivator archivator, String taggerName){
		String suffixFileName = this.getFeatureFilePathname()+taggerName+"/suffixList.txt";
		System.out.println("Reading suffix list from archive: " + suffixFileName);
		this.readSuffixFile(archivator, suffixFileName);
		System.out.println("... done");
	}


	private void computeSuffixesTest(String word) {
		System.out.println("Word: " + word);
		// Smallest suffix is just last character of a word
		for (int i = 0; i < word.length(); i++){
			String suffix = word.substring(i);
			System.out.println("Suff: " + suffix);
			String prefix = word.substring(0,i+1);
			System.out.println("Pref : " + prefix);
		}
	}
	public static void main(String[] args) throws IOException{
		WordSuffixFeatureFactory wsf = new WordSuffixFeatureFactory();

		wsf.computeSuffixesTest("Hausmann");

	}
}
