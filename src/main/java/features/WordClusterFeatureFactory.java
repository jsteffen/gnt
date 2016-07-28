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
import java.util.HashMap;
import java.util.Map;

import archive.Archivator;
import data.GlobalParams;

/**
 * Given a word2clusterId file from /Users/gune00/data/Marmot/Word
 * where each line is of form:
 * "Word ClusterID"
 * Create a word to clusterID dictionary hash - needed in learning and application phase
 * And then create a clusterId to index hash needed for liblinear encoding.
 * NOTE: words are case-sensitive !!
 * 
 * Then create a new dictionary word2liblinearClusterId which directly maps a word to the integer representation
 * of liblinear;
 * This file should be saved and loaded for use in training and application phase.
 * This file should be part of Alphabet class.
 * @author gune00
 *
 */
public class WordClusterFeatureFactory {
	// store mapping of clusterID to liblinear indexing
	// private Map<String, Integer> clusterId2num = new HashMap<String, Integer>();
	// Store resulting word2liblinear index map
	// store words to clusterId mapping as provided by Marmot tool!
	private Map<String,Integer> word2index = new HashMap<String,Integer>();
	private int clusterIdcnt = 0;
	private String featureFilePathname = "";

	public String getFeatureFilePathname() {
		return featureFilePathname;
	}
	public void setFeatureFilePathname(String featureFilePathname) {
		this.featureFilePathname = featureFilePathname;
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

	
	public WordClusterFeatureFactory(String featureFilePathname){
		this.setFeatureFilePathname(featureFilePathname);
	}
	
	public WordClusterFeatureFactory(){
	}

	// Methods

	public void clean(){
	}

	/**
	 * For CASE-SENSITIVE word, look it up in word2liblinear index;
	 * If it exists, return index else return index of unknown word <RARE>|<Rare>|<STOP>.
	 * During training phase, all words from training files are used and cluster id is used.
	 * During testing, for each word in cluster-dictionary, its cluster id is used
	 * @param word
	 * @return
	 */
	// Used to fill cluster feature vector in training and tagging
	public int getClusterIdFeature(String word){
		String normalizedDigitString = word.replaceAll("\\d", "0");
		if (this.getWord2index().containsKey(normalizedDigitString))
			return this.getWord2index().get(normalizedDigitString);
		else {
			//System.out.println("Unknown cluster word: " + normalizedDigitString);
			//
			// Map unknown words to dummy word <RARE>
			if (this.getWord2index().containsKey("<RARE>"))
				return this.getWord2index().get("<RARE>");
			else
				if (this.getWord2index().containsKey("<Rare>"))
					return this.getWord2index().get("<Rare>");
				else
					if (this.getWord2index().containsKey("<STOP>"))
						return this.getWord2index().get("<STOP>");
					else
					{
						System.err.println("Word does not match with word2liblinear index: " + word);
						return -1;
					}
		}
	}

	// Used to internalize word2cluster id by ajusting cluster id by +1
	public void createAndSaveClusterIdFeature(Archivator archivator, String taggerName, String clusterIDfileName){
		System.out.println("Create cluster ID list from: " + clusterIDfileName);
		this.createWord2ClusterIdMapFromFile(clusterIDfileName, -1);

		String fileName = this.getFeatureFilePathname()+taggerName+"/clusterId.txt";
		System.out.println("Writing cluster ID list to: " + fileName);
		this.writeClusterIdFeatureFile(fileName);

		System.out.println("... done");
		// Add file to archivator
		archivator.getFilesToPack().add(fileName);
	}

	private void createWord2ClusterIdMapFromFile(String fileName, int max){
		BufferedReader reader;
		int lineCnt = 0;
		int mod = 10000;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));

			// Each line consists of "word\tclusterId"
			String line;
			while ((line = reader.readLine()) != null) {
				if  ((max > 0) && (lineCnt >= max)) break;
				lineCnt++;
				String[] entry = line.split("\t");
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

	// This is to make sure that clusterId starts from 1, because in Marlin they start from 0
	// so they have to be adjusted
	private Integer getLiblinearIndex(String clusterId) {
		return Integer.valueOf(clusterId)+1;
	}

	private void addNewWord2liblinearId(String word, Integer liblinearIndex) {
		if (!this.getWord2index().containsKey(word)){
			getWord2index().put(word, liblinearIndex);
		}
		else
			System.err.println("Word " + word + " already seen!");
	}

	public void writeClusterIdFeatureFile(String targetFileName){
		File file = new File(targetFileName);
		file.getParentFile().mkdirs();
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
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
	
	private void readClusterIdFeatureFile(Archivator archivator, String string) {
		BufferedReader reader;
		try {
			InputStream inputStream = archivator.getArchiveMap().get(string);
			reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
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

	public void readClusterIdList(String taggerName, String featureFilePath){
		String fileName = featureFilePath+taggerName+"/clusterId.txt";
		System.out.println("Reading cluster ID list from: " + fileName);
		this.readClusterIdFeatureFile(fileName);
		System.out.println("... done");
	}
	
	public void readClusterIdList(Archivator archivator, String taggerName){
		String fileName = this.getFeatureFilePathname()+taggerName+"/clusterId.txt";
		System.out.println("Reading cluster ID list from archive: " + fileName);
		this.readClusterIdFeatureFile(archivator, fileName);
		System.out.println("... done");
	}
}
