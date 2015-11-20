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

/*
 * reads in a file of sentences in conll format and reads out each sentence lines-wise in a output file.
 * CONLL format:
 * - each word a line, sentence ends with newline
 * - word is at second position:
 * 1       The     _       DT      DT      _       2       NMOD 
 * Files to process for FLORS experiments:
 * - /Users/gune00/data/MLDP/english/english-train.conll
 * - /Users/gune00/data/sancl-2012/sancl.labeled/*-dev.conll
 * - /Users/gune00/data/BioNLPdata/CoNLL2007/pbiotb/english_pbiotb_dev.conll
 */

public class ConllMapper {

	private Corpus corpus = null;
	private String taggerName = "";
	public Corpus getCorpus() {
		return corpus;
	}
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public ConllMapper(String taggerName) {
		this.taggerName = taggerName;
		this.setCorpus(new Corpus(taggerName));
	}

	public void transcode(String sourceFileName, String sourceEncoding,
			String targetFileName, String targetEncoding)
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
		List<String> tokens = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				writer.write(sentenceToString(tokens)+"\n");
				tokens = new ArrayList<String>();
			}
			else
			{
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

	/**
	 * Used to map a conll file to a file of sentences/line !
	 */
	public void transcodeConllToSentenceFiles(){
		for (String fileName : this.getCorpus().trainingLabeledData){
			try {
				System.out.println(fileName);
				transcode(fileName+".conll","utf-8", fileName+"-sents.txt", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (String fileName : this.getCorpus().devLabeledData){
			try {
				System.out.println(fileName);
				transcode(fileName+".conll","utf-8", fileName+"-sents.txt", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (String fileName : this.getCorpus().testLabeledData){
			try {
				System.out.println(fileName);
				transcode(fileName+".conll","utf-8", fileName+"-sents.txt", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
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

	//HIERIX
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
		if (this.taggerName.equals("NER"))
			output = enNerTokenToString(tokenizedLine,index);
		else
			if (this.taggerName.equals("DENER"))
				output = deNerTokenToString(tokenizedLine,index);
		return output;

	}

	public void transCodeEnNerFiles() throws IOException{
		transcodeNERfile(
				"resources/data/ner/en/eng.train", "utf-8", 
				"resources/data/ner/en/eng-train.conll", "utf-8");
		transcodeNERfile(
				"resources/data/ner/en/eng.testa", "utf-8", 
				"resources/data/ner/en/eng-testa.conll", "utf-8");
		transcodeNERfile(
				"resources/data/ner/en/eng.testb", "utf-8", 
				"resources/data/ner/en/eng-testb.conll", "utf-8");
	}

	public void transCodeDeNerFiles() throws IOException{
		transcodeNERfile(
				"resources/data/ner/de/deu.train", "utf-8", 
				"resources/data/ner/de/deu-train.conll", "utf-8");
		transcodeNERfile(
				"resources/data/ner/de/deu.testa", "utf-8", 
				"resources/data/ner/de/deu-testa.conll", "utf-8");
		transcodeNERfile(
				"resources/data/ner/de/deu.testb", "utf-8", 
				"resources/data/ner/de/deu-testb.conll", "utf-8");
	}

	public static void main(String[] args) throws IOException {
		String taggerName = "DENER";
		ConllMapper mapper = new ConllMapper(taggerName);
		if (taggerName.equals("POS"))
			mapper.transcodeConllToSentenceFiles();
		else
			if (taggerName.equals("NER")){
				mapper.transCodeEnNerFiles();

				mapper.transcodeConllToSentenceFiles();
			}
			else
				if (taggerName.equals("DENER")){
					mapper.transCodeDeNerFiles();

					mapper.transcodeConllToSentenceFiles();
				}
	}
}	
