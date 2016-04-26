package corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import data.GlobalParams;
import data.ModelInfo;

public class CorpusProcessor {

	private Corpus corpus = null;

	public Corpus getCorpus() {
		return corpus;
	}
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public CorpusProcessor(Corpus corpus) {
		this.setCorpus(corpus);
	}

	/*
	 * reads in a file of sentences in conll format and reads out each sentence lines-wise in a output file.
	 * CONLL format:
	 * - each word a line, sentence ends with newline
	 * - word is at second position:
	 * 1       The     _       DT      DT      _       2       NMOD 
	 */
	/**
	 * Receives a file in CONLL format and maps each CONLL sentence to a sentence, where each sentence
	 * is a line of words extracted from the CONLL sentence.
	 * @param sourceFileName
	 * @param sourceEncoding
	 * @param targetFileName
	 * @param targetEncoding
	 * @throws IOException
	 */
	private void transcodeConllToSentenceFile(String sourceFileName, String sourceEncoding,
			String targetFileName, String targetEncoding, int maxSent)
					throws IOException {

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
		int sentCnt = 0;
		List<String> tokens = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				// If ew read a newline it means we know we have just extracted the words
				// of a sentence, so write them to file
				if (!tokens.isEmpty()){
					writer.write(sentenceToString(tokens)+"\n");
					tokens = new ArrayList<String>();
					// Increase sentence counter
					sentCnt++;
					// Stop if maxSent has been processed
					// if maxSent is < 0 this means: read until end of file.
					if  ((maxSent > 0) && (sentCnt >= maxSent)) break;
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

	private String sentenceToString(List<String> tokens){
		String sentenceString = "";
		for (int i=0; i < tokens.size()-1; i++){
			sentenceString = sentenceString + tokens.get(i)+" ";
		}
		return sentenceString+tokens.get(tokens.size()-1);
	}

	// Processing NER files
	private void transcodeNERfile(String sourceFileName, String sourceEncoding,
			String targetFileName, String targetEncoding)
					throws IOException{
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
		int tokenCnt = 0;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				tokenCnt = 0;
				writer.newLine();
			}
			else
			{ //if (!line.equals("-DOCSTART- -X- O O"))
				{
					tokenCnt++;
					String[] tokenizedLine = line.split(" ");
					writer.write(this.nerTokenToString(tokenizedLine, tokenCnt));
					writer.newLine();
				}
			}
		}

		reader.close();
		writer.close();
	}

	private String enNerTokenToString(String[] tokenizedLine, int index) {
		// EN
		// West NNP I-NP I-MISC
		// index West NNP I-NP I-MISC
		String output = index+"\t";
		output +=tokenizedLine[0]+"\t";
		output +=tokenizedLine[1]+"\t";
		output +=tokenizedLine[2]+"\t";
		output +=tokenizedLine[3];
		return output;

	}

	private String deNerTokenToString(String[] tokenizedLine, int index) {
		// DE
		// Nordendler <unknown> NN I-NC I-ORG
		// index Nordendler NN I-NC I-ORG
		String output = index+"\t";
		output +=tokenizedLine[0]+"\t";
		output +=tokenizedLine[2]+"\t";
		output +=tokenizedLine[3]+"\t";
		output +=tokenizedLine[4];
		return output;

	}

	private String nerTokenToString(String[] tokenizedLine, int index) {
		String output = "";
		if (GlobalParams.taggerName.equals("NER")||
				GlobalParams.taggerName.equals("ENNER"))
			output = enNerTokenToString(tokenizedLine,index);
		else
			if (GlobalParams.taggerName.equals("DENER"))
				output = deNerTokenToString(tokenizedLine,index);
		
		return output;

	}

	// Main wrappers for processing all files defined in corpus for current used taggerName

	/**
	 * This is a wrapper to process a set of NER files. Currently, assuming conll 2003 format
	 */
	private void transcodeSourceFileToProperConllFormatFiles(){
		
		for (String fileName : this.getCorpus().getTrainingLabeledSourceFiles()){
			try {
				System.out.println(fileName+".src");
				transcodeNERfile(fileName+".src", "ISO-8859-1", fileName+".conll", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (String fileName : this.getCorpus().getDevLabeledSourceFiles()){
			try {
				System.out.println(fileName+".src");
				transcodeNERfile(fileName+".src", "ISO-8859-1", fileName+".conll", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (String fileName : this.getCorpus().getTestLabeledSourceFiles()){
			try {
				System.out.println(fileName+".src");
				transcodeNERfile(fileName+".src", "ISO-8859-1", fileName+".conll", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	/**
	 * This is a wrapper to process a set of files! -1 means: all files
	 */
	private void transcodeConllToSentenceFiles(){
		for (String fileName : this.getCorpus().getTrainingLabeledData()){
			try {
				System.out.println(fileName+".conll");
				transcodeConllToSentenceFile(fileName+".conll","utf-8", fileName+"-sents.txt", "utf-8", -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (String fileName : this.getCorpus().getDevLabeledData()){
			try {
				System.out.println(fileName+".conll");
				transcodeConllToSentenceFile(fileName+".conll","utf-8", fileName+"-sents.txt", "utf-8", -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (String fileName : this.getCorpus().getTestLabeledData()){
			try {
				System.out.println(fileName+".conll");
				transcodeConllToSentenceFile(fileName+".conll","utf-8", fileName+"-sents.txt", "utf-8", -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	public void processConllFiles() throws IOException{
		this.transcodeSourceFileToProperConllFormatFiles();
		this.transcodeConllToSentenceFiles();
	}
}	
