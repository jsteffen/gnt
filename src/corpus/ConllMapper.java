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

	public static void transcode(String sourceFileName, String sourceEncoding,
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
				writer.write(ConllMapper.sentenceToString(tokens)+"\n");
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

	private static String sentenceToString(List<String> tokens){
		String sentenceString = "";
		for (int i=0; i < tokens.size()-1; i++){
			sentenceString = sentenceString + tokens.get(i)+" ";
		}
		return sentenceString+tokens.get(tokens.size()-1);
	}
	
	public static void transcodeFlorsFileList(){
		List<String> fileList = new ArrayList<String>();
		fileList.add("/Users/gune00/data/MLDP/english/english-train");
		fileList.add("/Users/gune00/data/BioNLPdata/CoNLL2007/pbiotb/dev/english_pbiotb_dev");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-answers-dev");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-dev");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-dev");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-dev");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-answers-test");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-test");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-test");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-test");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-test");
		fileList.add("/Users/gune00/data/sancl-2012/sancl.labeled/ontonotes-wsj-test");
		
		for (String fileName : fileList){
			try {
				System.out.println(fileName);
				ConllMapper.transcode(fileName+".conll","utf-8", fileName+"-sents.txt", "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] args) throws IOException {
		ConllMapper.transcodeFlorsFileList();
	}
}	
