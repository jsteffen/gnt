package caller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import data.GlobalParams;
import data.ModelInfo;
import tagger.GNTagger;
import tokenize.GntTokenizer;

public class GNTaggerStandalone {
	private ModelInfo modelInfo = null;
	private GNTagger posTagger = null;

	public void initRunner(String archiveName) throws IOException{
		modelInfo = new ModelInfo();
		posTagger = new GNTagger(archiveName, modelInfo);
		posTagger.initGNTagger(GlobalParams.windowSize, GlobalParams.dim);
	}

	/**
	 * Receives a string and calls GNT tagger. Then splits resulting tagged strings into a line-oriented format
	 * @param inputString
	 * @throws IOException
	 */
	public void tagStringRunner(String inputString) throws IOException{
		String[] tokens = GntTokenizer.splitTokenizer(inputString);

		this.posTagger.tagUnlabeledTokens(tokens);

		String taggedString = posTagger.taggedSentenceToString();

		for (String token : taggedString.split(" ")){
			System.out.println(token);
		}
	}

	/**
	 * Receives the name of a file, reads it line-wise, calls GNT tagger on each line, and
	 * saves resulting tagged string in output file. Output file is build from sourceFilename by 
	 * adding suffix .GNT
	 * @param sourceFileName
	 * @param inEncode
	 * @param outEncode
	 * @throws IOException
	 */
	public void tagFileRunner(String sourceFileName, String inEncode, String outEncode) throws IOException {
		BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName), inEncode));
		BufferedWriter fileWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(sourceFileName+".GNT"),outEncode));
		String line = "";
		while ((line = fileReader.readLine()) != null) {
			if (!line.isEmpty()){
				String[] tokens = GntTokenizer.splitTokenizer(line);
				this.posTagger.tagUnlabeledTokens(tokens);
				String taggedString = posTagger.taggedSentenceToString();
				for (String token : taggedString.split(" ")){
					fileWriter.write(token+"\n");
				}			
			}
		}
		fileReader.close();
		fileWriter.close();
	}
}