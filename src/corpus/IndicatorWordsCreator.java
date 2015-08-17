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
			if (wordToNum.containsKey(word)) {
				wordToNum.put(word, wordToNum.get(word)+1);
			} else {
				wordToNum.put(word, 1);
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
	public void readAndProcessInputTextLineWise(String fileName, String type){
		BufferedReader reader;
		int mod = 100000;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				lineCnt++;
				String[] words = cleanTextLine(line, type);
				countWords(words);
				if ((lineCnt % mod) == 0) System.out.println(lineCnt);
			}
			reader.close();

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
			writer.write(tokenCnt + "\t" + wordToNum.size());
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

	// Still missing: labeled data sentences for training sets and test sets
	private void processFlorsDataSet(){
		// Training data
		readAndProcessInputTextLineWise("/Users/gune00/data/BioNLPdata/CoNLL2007/ptb/unlab/english_ptb_unlab", "ptb");

		// Test data
		readAndProcessInputTextLineWise("/Users/gune00/data/BioNLPdata/CoNLL2007/pbiotb/unlab/all-unlab.txt", "ptb");

		// unlabeled test data in sentence form from /Users/gune00/data/sancl-2012/sancl.all
		readAndProcessInputTextLineWise("/Users/gune00/data/sancl-2012/sancl.all/gweb-answers.unlabeled.txt", "ptb");
		readAndProcessInputTextLineWise("/Users/gune00/data/sancl-2012/sancl.all/gweb-emails.unlabeled.txt", "ptb");
		readAndProcessInputTextLineWise("/Users/gune00/data/sancl-2012/sancl.all/gweb-newsgroups.unlabeled.txt", "ptb");
		readAndProcessInputTextLineWise("/Users/gune00/data/sancl-2012/sancl.all/gweb-reviews.unlabeled.txt", "ptb");
		readAndProcessInputTextLineWise("/Users/gune00/data/sancl-2012/sancl.all/gweb-weblogs.unlabeled.txt", "ptb");
	}

	private void processTestData(){
		readAndProcessInputTextLineWise("/Volumes/data1/news.2012.en.shuffled", "ptb");
	}
	
	// Test  caller

	public static void main(String[] args) throws IOException {
		IndicatorWordsCreator iwp = new IndicatorWordsCreator();
		long time1 = System.currentTimeMillis();

		iwp.processFlorsDataSet();

		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		iwp.printWordNumSize();
		time1 = System.currentTimeMillis();

		// -1 means: write all words, otherwise write n highest ranked words
		iwp.writeSortedIndicatorWords("resources/iw.txt", -1);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
		
		iwp.writeSortedIndicatorWords("resources/iw-1000.txt", 1000);
		
	}
}