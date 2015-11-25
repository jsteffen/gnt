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
 * A indicator word is used to define a dimension of distributed word vectors
 * a indicator word is selected on basis its rank.
 *<p><p> 
 * A class for creating the indicator words for a given text corpus
 * - each line of a text file corresponds to a sentence of lower-cased words
 * - depending on the corpus sources, some cleaning has to be done 
 * - then term frequency TF is computed and finally a ranked list is computed in decreasing order of TF
 * - the final list is then output - either completely or only N highest terms
 * 
 * NOTE: it is open for other corpora as well
 * @author gune00
 *
 */
public class IndicatorWordsCreator {
	static int lineCnt = 0;
	static int tokenCnt = 0;
	private Map<String, Integer> wordToNum = new HashMap<String, Integer>();

	public IndicatorWordsCreator(){
	}

	// Clean text line according to given type
	// AND lower case text
	// It is assumed that line is a tokenized sentence

	public Map<String, Integer> getWordToNum() {
		return wordToNum;
	}
	public void setWordToNum(Map<String, Integer> wordToNum) {
		this.wordToNum = wordToNum;
	}

	//
	private String[] cleanTextLine(String line, String type) {
		String[] words = {};
		switch (type) {
		case "ptb": 
			words = cleanPTBLine(line);
			break;
		case "eng_web_tbk":
			words = cleanWebTBKLine(line);
			break;
		default: 
			words = line.toLowerCase().split(" ");
			break;
		}

		tokenCnt = tokenCnt + words.length;
		return words;
	}

	private String[] cleanPTBLine(String line) {
		String[] words = line.toLowerCase().split(" ");
		return words;
	}

	private String[] cleanWebTBKLine(String line) {
		String[] words = line.toLowerCase().split(" ");
		String[] firstWord = words[0].split(">");
		//for (String x : firstWord) System.out.print(x+" --- ");System.out.println("\n");
		words[0] = firstWord[firstWord.length-1];
		return words;
	}

	// Count frequency of words
	public void countWords(String[] words){
		for (String word : words){
			if (this.getWordToNum().containsKey(word)) {
				this.getWordToNum().put(word, this.getWordToNum().get(word)+1);
			} else {
				this.getWordToNum().put(word, 1);
			}
		}
	}

	// Printing methods

	// Print size of hash
	public void printWordNumSize(){
		System.out.println("Token: " + tokenCnt + " Types: " + wordToNum.size());
	}

	// MAIN methods
	// iterate through input file linewise
	// clean line according to given type (depends on corpus encoding)
	// count words according to term frequency
	public void readAndProcessInputTextLineWise(String fileName, String type, int max){
		BufferedReader reader;
		int mod = 100000;
		int myLineCnt = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				if  ((max > 0) && (myLineCnt >= max)) break;
				lineCnt++;
				myLineCnt++;
				if (!line.isEmpty()){
					String[] words = cleanTextLine(line, type);
					countWords(words);
				}
				if ((lineCnt % mod) == 0) System.out.println(lineCnt);
			}
			System.out.println("+++"+myLineCnt); reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Sort wordToNum according to value in decreasing order
	// transform hashmap to treemap by using ValueComparator
	private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {
		Map<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}

	private Map<String, Integer> subSamplingEntries(double threshold) {
		Map<String, Integer> newMap = new TreeMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : this.getWordToNum().entrySet()){
			String key = entry.getKey();
			Integer value = entry.getValue();
			double p = 1 - Math.sqrt(threshold/value);
			if ((1-p) > threshold) {
				//System.out.println("Key: " + key + " Value: " + value + " p: " + p);
				newMap.put(key, value);	
			}
		}	
		return newMap;
	}

	public void postProcessWords(double threshold){	
		this.setWordToNum(this.subSamplingEntries(threshold));
		this.setWordToNum(sortByValue(this.getWordToNum()));
	}

	private void writeSortedMap (int n, BufferedWriter writer){
		int cnt = 1;
		//Map<String, Integer> sortedMap = sortByValue(wordToNum);
		try {
			writer.write(tokenCnt + "\t" + this.getWordToNum().size());
			for (Map.Entry<String, Integer> entry : this.getWordToNum().entrySet()) {
				writer.write("\n" + entry.getKey() + "\t"+ entry.getValue());
				if (cnt == n) break; cnt++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeSortedIndicatorWords(String targetFileName, int cnt){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileName),"UTF-8"));
			this.writeSortedMap(cnt, writer);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO - check, but I think I have all source files that are also used in FLORS
	private void createIndicatorEnPosWordsFromFiles(){

		// Training data labeled sentences
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-train-sents.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-test-sents.txt", "ptb", 1000);

		// Training data unlabeled
		readAndProcessInputTextLineWise("resources/data/ptb/unlab/english_ptb_unlab", "ptb", 100000);

		// PTB-BIO
		readAndProcessInputTextLineWise("resources/data/pbiotb/dev/english_pbiotb_dev-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/pbiotb/unlab/all-unlab.txt", "ptb", 100000);

		// sentence form from resources/data/sancl-2012/ 

		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-answers-dev-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-emails-dev-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-reviews-dev-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-dev-sents.txt", "ptb", 1000);

		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-answers-test-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-emails-test-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-test-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-reviews-test-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-test-sents.txt", "ptb", 1000);

		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-answers.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-emails.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-newsgroups.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-reviews.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-weblogs.unlabeled.txt", "ptb", 100000);
	}

	private void createIndicatorDePosWordsFromFiles(){


		// Training data unlabeled
		readAndProcessInputTextLineWise("resources/data/german/unlab/de-wikidump-sents500000.txt", "ptb", 100000);
		// Training data labeled sentences
		readAndProcessInputTextLineWise("resources/data/german/tiger2_train-sents.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/german/tiger2_devel-sents.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/german/tiger2_test-sents.txt", "ptb", 100000);
	}

	// NER
	// TODO - check what else
	private void createIndicatorEnNerWordsFromFiles(){

		// Training data unlabeled
		readAndProcessInputTextLineWise("resources/data/ptb/unlab/english_ptb_unlab", "ptb", 100000);
		// Training data labeled sentences
		readAndProcessInputTextLineWise("resources/data/ner/en/eng-train-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/ner/en/eng-testa-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/ner/en/eng-testb-sents.txt", "ptb", 1000);

	}

	// TODO - check what else
	private void createIndicatorDeNerWordsFromFiles(){

		// Training data unlabeled
		readAndProcessInputTextLineWise("resources/data/german/unlab/de-wikidump-sents500000.txt", "ptb", 100000);
		// Training data labeled sentences
		readAndProcessInputTextLineWise("resources/data/ner/de/deu-train-sents.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/ner/de/deu-testa-sents.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/ner/de/deu-testb-sents.txt", "ptb", 100000);

	}

	/**
	 * A wrapper for calling the different taggerName specific methods.
	 * @param taggerName
	 */
	public void createIndicatorTaggerNameWordsFromFile(String taggerName){
		if (taggerName.equals("POS"))
			createIndicatorEnPosWordsFromFiles();
		else
			if (taggerName.equals("NER"))
				createIndicatorEnNerWordsFromFiles();
			else
				if (taggerName.equals("DENER"))
					createIndicatorDeNerWordsFromFiles();
				else
					if (taggerName.equals("DEPOS"))
						createIndicatorDePosWordsFromFiles();

	}
	// Test  caller

	public static void main(String[] args) throws IOException {
		IndicatorWordsCreator iwp = new IndicatorWordsCreator();
		long time1 = System.currentTimeMillis();

		iwp.createIndicatorTaggerNameWordsFromFile("POS");

		iwp.postProcessWords(0.00001);

		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		iwp.printWordNumSize();
		time1 = System.currentTimeMillis();

		// -1 means: write all words, otherwise write n highest ranked words
		iwp.writeSortedIndicatorWords("resources/features/iw_all.txt", 10000);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

	}
}