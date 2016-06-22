package corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import data.Pair;

public class ConlluToConllMapper {

	public static String conlluPath = "/Users/gune00/data/UniversalDependencies/";
	public static String conllPath = "/Users/gune00/data/UniversalDependencies/conll/";

	/**
	 * Example
	 * 
	 * Input: "/Users/gune00/data/UniversalDependencies/" "Arabic" "ar" "dev"
	 * 
	 * Output: "/Users/gune00/data/UniversalDependencies/UD_Arabic-master/ar-ud-dev.conllu"
	 * 
	 * @param conlluPath 
	 * @param languageName
	 * @param languageID
	 * @param mode
	 * @return
	 */
	private  static String makeConlluFileName (String languageName, String languageID, String mode){
		String fileName =
				ConlluToConllMapper.conlluPath + "UD_" + languageName + "-master/" + languageID + "-ud-" + mode + ".conllu";
		return fileName;

	}

	/**
	 * Creates 
	 * "/Users/gune00/data/UniversalDependencies/conll/Arabic/ar-ud-dev.conll";
	 * @param languageName
	 * @param languageID
	 * @param mode
	 * @return
	 */
	private  static String makeConllFileName (String languageName, String languageID, String mode){
		String conllDirName = ConlluToConllMapper.conllPath + languageName +"/";
		File conllLangDir = new File(conllDirName);
		if (!conllLangDir.exists()) conllLangDir.mkdir();
		String fileName = conllDirName + languageID + "-ud-" + mode + ".conll";
		return fileName;

	}

	private static String makeSentenceFileName(String conllFileName){
		return conllFileName.split("\\.conll")[0]+"-sents.txt";

	}

	/**
	 * bascially maps a conllu to conll format - very simple process so far.
	 * @param sourceFileName
	 * @param targetFileName
	 * @throws IOException
	 */
	private static void transformConlluToConllFile(String sourceFileName, String targetFileName)
			throws IOException {

		String sourceEncoding = "utf-8";
		String targetEncoding = "utf-8";
		// init reader for CONLL style file
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(sourceFileName),
						sourceEncoding));

		// init writer for line-wise file
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(targetFileName),
						targetEncoding));

		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) 
				writer.newLine();
			else
			{
				// Normalize line which is assumed to correspond to a sentence.
				if (!line.startsWith("#")){
					writer.write(line);
					writer.newLine();
				}
			}
		}
		reader.close();
		writer.close();
	}

	private static void transcodeConllToSentenceFile(String sourceFileName, String targetFileName)
			throws IOException {
		String sourceEncoding = "utf-8";
		String targetEncoding = "utf-8";
		// init reader for CONLL style file

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(sourceFileName),
						sourceEncoding));

		// init writer for line-wise file
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(targetFileName),
						targetEncoding));

		String line = "";
		List<String> tokens = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				// If we read a newline it means we know we have just extracted the words
				// of a sentence, so write them to file
				if (!tokens.isEmpty()){
					writer.write(sentenceToString(tokens)+"\n");
					tokens = new ArrayList<String>();
				}
			}
			else
			{
				// Extract the word from each CONLL token line
				String[] tokenizedLine = line.split("\t");
				tokens.add(tokenizedLine[1]);
			}

		}
		reader.close();
		writer.close();
	}

	private static String sentenceToString(List<String> tokens){
		String sentenceString = "";
		for (int i=0; i < tokens.size()-1; i++){
			sentenceString = sentenceString + tokens.get(i)+" ";
		}
		return sentenceString+tokens.get(tokens.size()-1);
	}

	/**
	 * Transform files fro train/test/dev and call them in onw main caller
	 * @param languageName
	 * @param languageID
	 * @throws IOException
	 */
	private static void transformerTrain(String languageName, String languageID) throws IOException{
		String conlluFile = ConlluToConllMapper.makeConlluFileName(languageName, languageID, "train");
		String conllFile = ConlluToConllMapper.makeConllFileName(languageName, languageID, "train");
		String sentFile = ConlluToConllMapper.makeSentenceFileName(conllFile);
		ConlluToConllMapper.transformConlluToConllFile(conlluFile, conllFile);
		ConlluToConllMapper.transcodeConllToSentenceFile(conllFile, sentFile);
	}

	private static void transformerDev(String languageName, String languageID) throws IOException{
		String conlluFile = ConlluToConllMapper.makeConlluFileName(languageName, languageID, "dev");
		String conllFile = ConlluToConllMapper.makeConllFileName(languageName, languageID, "dev");
		String sentFile = ConlluToConllMapper.makeSentenceFileName(conllFile);
		ConlluToConllMapper.transformConlluToConllFile(conlluFile, conllFile);
		ConlluToConllMapper.transcodeConllToSentenceFile(conllFile, sentFile);
	}

	private static void transformerTest(String languageName, String languageID) throws IOException{
		String conlluFile = ConlluToConllMapper.makeConlluFileName(languageName, languageID, "test");
		String conllFile = ConlluToConllMapper.makeConllFileName(languageName, languageID, "test");
		String sentFile = ConlluToConllMapper.makeSentenceFileName(conllFile);
		ConlluToConllMapper.transformConlluToConllFile(conlluFile, conllFile);
		ConlluToConllMapper.transcodeConllToSentenceFile(conllFile, sentFile);
	}

	public static void transformer(String languageName, String languageID) throws IOException{
		ConlluToConllMapper.transformerTrain(languageName, languageID);
		ConlluToConllMapper.transformerDev(languageName, languageID);
		ConlluToConllMapper.transformerTest(languageName, languageID);
	}

	/**
	 * Loop across languages
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		List<Pair<String,String>> languages = new ArrayList<Pair<String,String>>();

		languages.add(new Pair<String,String>("Arabic", "ar"));
		languages.add(new Pair<String,String>("Basque", "eu"));
		languages.add(new Pair<String,String>("Bulgarian", "bg"));
		languages.add(new Pair<String,String>("Croatian", "hr"));
		languages.add(new Pair<String,String>("Czech", "cs"));
		languages.add(new Pair<String,String>("Danish", "da"));
		languages.add(new Pair<String,String>("Dutch", "nl"));
		languages.add(new Pair<String,String>("English", "en"));
		languages.add(new Pair<String,String>("Finnish", "fi"));
		languages.add(new Pair<String,String>("French", "fr"));
		languages.add(new Pair<String,String>("German", "de"));
		languages.add(new Pair<String,String>("Hebrew", "he"));
		languages.add(new Pair<String,String>("Hindi", "hi"));
		languages.add(new Pair<String,String>("Indonesian", "id"));
		languages.add(new Pair<String,String>("Italian", "it"));
		languages.add(new Pair<String,String>("Norwegian", "no"));
		languages.add(new Pair<String,String>("Persian", "fa"));
		languages.add(new Pair<String,String>("Polish", "pl"));
		languages.add(new Pair<String,String>("Portuguese", "pt"));
		languages.add(new Pair<String,String>("Slovenian", "sl"));
		languages.add(new Pair<String,String>("Spanish", "es"));
		languages.add(new Pair<String,String>("Swedish", "sv"));

		for (Pair<String, String> language : languages){
			ConlluToConllMapper.transformer(language.getL(), language.getR());
		}


	}

}
