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

/**
 * Given a word2clusterId file from /Users/gune00/data/Marmot/Word
 * where each line is of form:
 * "Word ClusterID"
 * Create a word to clusterID dictionary hash - needed in learning and application phase
 * And then create a clusterId to index hash needed for liblinear encoding.
 * NOTE: words are case-sensitive !!
 * 
 * Then create a new dictionary word2liblinearClusterId which directly maops a word to the integer representation
 * of liblinear;
 * This file should be saved and loaded for use in training and application phase.
 * This file should be part of Alphabet class.
 * @author gune00
 *
 */
public class WordClusterFeatureFactory {
	// store mapping of clusterID to liblinear indexing
	private Map<String, Integer> clusterId2num = new HashMap<String, Integer>();
	// Store resulting word2liblinear index map
	// store words to clusterId mapping as provided by Marmot tool!
	private Map<String,Integer> word2index = new HashMap<String,Integer>();
private int clusterIdcnt = 0;

	// Getters and setters

	public Map<String, Integer> getClusterId2num() {
		return clusterId2num;
	}
	public void setClusterId2num(Map<String, Integer> clusterId2num) {
		this.clusterId2num = clusterId2num;
	}
	public Map<String, Integer> getWord2index() {
		return word2index;
	}
	public void setWord2index(Map<String, Integer> word2index) {
		this.word2index = word2index;
	}
	public int getClusterIdcnt() {
		return clusterIdcnt;
	}
	public void setClusterIdcnt(int clusterIdcnt) {
		this.clusterIdcnt = clusterIdcnt;
	}

	// Instances

	public WordClusterFeatureFactory(){
	}

	// Methods

	public void clean(){
	}

	/**
	 * For CASE-SENSITIVE word, look it up in word2liblinear index;
	 * If it exists, return index else return index of unknown word <RARE>|<Rare>
	 * @param word
	 * @return
	 */
	public int getClusterIdFeature(String word){
		if (this.getWord2index().containsKey(word))
			return this.getWord2index().get(word);
		else
			if (this.getWord2index().containsKey("<RARE>"))
				return this.getWord2index().get("<RARE>");
			else
				if (this.getWord2index().containsKey("<Rare>"))
					return this.getWord2index().get("<Rare>");
				else
				{
					System.err.println("Word does not match with word2liblinear index: " + word);
					return -1;
				}
	}

	public void createAndSaveClusterIdFeature(String taggerName, String clusterIDfileName){
		System.out.println("Create cluster ID list from: " + clusterIDfileName);
		this.createWord2ClusterIdMapFromFile(clusterIDfileName, -1);

		String fileName = "resources/features/clusterId"+"_"+taggerName+".txt";
		System.out.println("Writing cluster ID list to: " + fileName);
		this.writeClusterIdFeatureFile(fileName);

		System.out.println("... done");
	}

	private void createWord2ClusterIdMapFromFile(String fileName, int max){
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
				// split off words -> it will be lower-cased as part of the process that computes the signatures
				String[] entry = line.split("\t");
				// then compute suffixes
				computeWord2ClusterIdFromWords(entry[0], entry[1]);
				if ((lineCnt % mod) == 0) System.out.println(lineCnt);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void computeWord2ClusterIdFromWords(String word, String clusterId) {
		Integer liblinearIndex = getLiblinearIndex(clusterId);
		addNewWord2liblinearId(word, liblinearIndex);
	}

	private Integer getLiblinearIndex(String clusterId) {
		if (!this.getClusterId2num().containsKey(clusterId)){
			clusterIdcnt++;
			getClusterId2num().put(clusterId, clusterIdcnt);
			return clusterIdcnt;
		}
		else
			return getClusterId2num().get(clusterId);
	}

	private void addNewWord2liblinearId(String word, Integer liblinearIndex) {
		if (!this.getWord2index().containsKey(word)){
			getWord2index().put(word, liblinearIndex);
		}
		else
			System.err.println("Word " + word + " already in seen!");
	}

	public void writeClusterIdFeatureFile(String targetFileName){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileName),"UTF-8"));
			for(String word: this.getWord2index().keySet()){
				writer.write(word+"\t"+this.getWord2index().get(word)+"\n");
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readClusterIdFeatureFile(String string) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(string),"UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] entry = line.split("\t");
				int liblinearClusterId = Integer.parseInt(entry[1]);
				clusterIdcnt = Math.max(liblinearClusterId, clusterIdcnt);
				this.getWord2index().put(entry[0], liblinearClusterId);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readClusterIdList(String taggerName){
		String fileName = "resources/features/clusterId"+"_"+taggerName+".txt";
		System.out.println("Reading cluster ID list from: " + fileName);
		this.readClusterIdFeatureFile(fileName);
		System.out.println("... done");
	}
	
	public void readClusterIdList(){
		String fileName = "resources/features/clusterId"+"_"+".txt";
		System.out.println("Reading cluster ID list from: " + fileName);
		this.readClusterIdFeatureFile(fileName);
		System.out.println("... done");
	}


	public static void main(String[] args){
		WordClusterFeatureFactory wordClusterIdFactory = new WordClusterFeatureFactory();
		wordClusterIdFactory.createWord2ClusterIdMapFromFile("/Users/gune00/data/Marmot/Word/en_marlin_cluster_1000", -1);


		System.out.println("Writing clusterId list to: " + "resources/features/clusterId.txt");
		wordClusterIdFactory.writeClusterIdFeatureFile("resources/features/clusterId.txt");
		System.out.println(wordClusterIdFactory.clusterIdcnt);
		System.out.println("... done");
		System.out.println("Read cluster Id list from: " + "resources/features/clusterId.txt");
		wordClusterIdFactory.readClusterIdFeatureFile("resources/features/clusterId.txt");
		System.out.println("... done");
		String word = "The";
;		System.out.println(word + " : " + wordClusterIdFactory.getClusterIdFeature(word));
	}
}