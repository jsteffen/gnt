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
 * A class for creating the indicator words for a given text corpus
 * - each line of a text file corresponds to a sentence of lower-cased words
 * - depending of the corpus sources, some cleaning has to be done 
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

	private void writeSortedMap (int n, BufferedWriter writer){
		int cnt = 1;
		Map<String, Integer> sortedMap = sortByValue(wordToNum);
		try {
			writer.write(tokenCnt + "\t" + this.getWordToNum().size());
			for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
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
	private void createIndicatorWordsFromFiles(){

		// Training data labeled sentences
		readAndProcessInputTextLineWise("resources/data/english/ptb3-std-training-sents.txt", "ptb", 100000);
		// Training data unlabeled
		readAndProcessInputTextLineWise("resources/data/ptb/unlab/english_ptb_unlab", "ptb", 100000);

		// Labeled development PTB-BIO
		readAndProcessInputTextLineWise("resources/data/pbiotb/dev/english_pbiotb_dev-sents.txt", "ptb", 1000);
		// Test data
		// unlabeled PTB-BIO
		readAndProcessInputTextLineWise("resources/data/pbiotb/unlab/all-unlab.txt", "ptb", 100000);

		// sentence form from resources/data/sancl-2012/ 

		// sancl.labeled/

		readAndProcessInputTextLineWise("resources/data/english/ptb3-std-devel-sents.txt", "ptb", 1000);
		readAndProcessInputTextLineWise("resources/data/english/ptb3-std-test-sents.txt", "ptb", 1000);
		
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

		// unlabeled sancl.all
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-answers.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-emails.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-newsgroups.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-reviews.unlabeled.txt", "ptb", 100000);
		readAndProcessInputTextLineWise("resources/data/sancl-2012/sancl.all/gweb-weblogs.unlabeled.txt", "ptb", 100000);
	}

	// Test  caller

	public static void main(String[] args) throws IOException {
		IndicatorWordsCreator iwp = new IndicatorWordsCreator();
		long time1 = System.currentTimeMillis();

		iwp.createIndicatorWordsFromFiles();

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