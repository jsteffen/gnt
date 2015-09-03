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
 * For each word given, check all shape features and set bit vector map accordingly.
 * Maintain a map of word - bit vector, so that a output file for the training set can be created - really needed ?
 * Anyway, processing PTB should give me 50 different bit vectors instances (or signatures).
 * -> Basically I get only 49 !
 * 
 * NOTE: in principle: file operation not really needed here, because will be later done in one training class. 
 * BUT then: take care that words are not lower cased, before calling this.
 * 
 * @author gune00
 *
 */
public class WordShapeFeatureFactory {

	// A mapping from word to Shape vector; used as a cache to avoid redundant computation
	private Map<String,WordShapeFeature> word2signature = new HashMap<String,WordShapeFeature>();
	// A mapping from the string of a ShapeVector (signature) to its ShapeVector; also used as cache
	private Map<String,Integer> signature2index = new HashMap<String,Integer>();
	private Map<Integer,String> index2signature = new TreeMap<Integer,String>();


	/* process file line wise
	 * for each line (sentence) do:
	 * - extract words
	 * - for each word, compute signature, if not done so
	 * - store it in hash
	 * - NOTE: before lower casing word (as usual) first check Upercase feature and the like
	 */

	private int wordCnt = 0;
	private int signatureCnt = 0;

	public Map<String, WordShapeFeature> getWord2signature() {
		return word2signature;
	}
	public void setWord2signature(Map<String, WordShapeFeature> word2signature) {
		this.word2signature = word2signature;
	}
	public Map<String, Integer> getSignature2index() {
		return signature2index;
	}
	public void setSignature2index(Map<String, Integer> signature2index) {
		this.signature2index = signature2index;
	}
	public Map<Integer, String> getIndex2signature() {
		return index2signature;
	}
	public void setIndex2signature(Map<Integer, String> index2signature) {
		this.index2signature = index2signature;
	}

	public void createShapeVectorsFromFile(String fileName, int max){
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
				// split off words -> it will be lowercased as part of the process that computes the signatures
				String[] words = line.split(" ");
				// then compute suffixes
				computeShapeVectorsFromWords(words);
				if ((lineCnt % mod) == 0) System.out.println(lineCnt);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void computeShapeVectorsFromWords(String[] words) {
		int wordIndex = 0;
		for (String word : words){
			computeShapeVectorAndStore(word, wordIndex);
			determineSignatureType(word2signature.get(word));
			wordIndex++;
		}
	}

	private void determineSignatureType(WordShapeFeature wordShapeVector) {
		if (!signature2index.containsKey(wordShapeVector.getBitVectorString())){
			signatureCnt++;
			signature2index.put(wordShapeVector.getBitVectorString(), signatureCnt);
			index2signature.put(signatureCnt, wordShapeVector.getBitVectorString());
		}
	}

	private void computeShapeVectorAndStore(String word, int wordIndex) {
		if (!word2signature.containsKey(word)){
			WordShapeFeature wordShapeVector = new WordShapeFeature(word, wordIndex);
			this.wordCnt++;
			word2signature.put(word, wordShapeVector);
		}
	}

	public void printSignaturesMap(){
		for (Integer key: index2signature.keySet()){
			System.out.println(key + " " + index2signature.get(key));
		} 
	}

	private void writeShapeFeatureFile(String targetFileName){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileName),"UTF-8"));
			for(int key: index2signature.keySet()){
				writer.write(index2signature.get(key)+"\n");
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readShapeFeatureFile(String string) {
			BufferedReader reader;
			int cnt = 1;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(string),"UTF-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					signature2index.put(line, cnt);
					index2signature.put(cnt,line);
					cnt++;
				}
				reader.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	public void testReadShapList(){
		System.out.println("Reading shape list from: " + "/Users/gune00/data/wordVectorTests/shapeList.txt");
		this.readShapeFeatureFile("/Users/gune00/data/wordVectorTests/shapeList.txt");
		System.out.println("... done");
	}

	public static void main(String[] args){
		WordShapeFeatureFactory wordShapeFactory = new WordShapeFeatureFactory();
		wordShapeFactory.createShapeVectorsFromFile("/Users/gune00/data/MLDP/english/english-train-sents.txt", -1);
		//wordShapeFactory.createShapeVectorsFromFile("/Users/gune00/data/BioNLPdata/CoNLL2007/ptb/unlab/english_ptb_unlab", 100000);

		System.out.println("Writing shape list to: " + "/Users/gune00/data/wordVectorTests/shapeList.txt");
		wordShapeFactory.writeShapeFeatureFile("/Users/gune00/data/wordVectorTests/shapeList.txt");
		System.out.println("... done");
	}

}
