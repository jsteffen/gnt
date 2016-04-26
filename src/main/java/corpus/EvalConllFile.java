package corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import data.Data;
import data.GlobalParams;
import data.ModelInfo;

public class EvalConllFile {
	private Data data = new Data();
	private double acc;
	private double accOOV;
	private double accInV;

	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public double getAcc() {
		return acc;
	}
	public void setAcc(double acc) {
		this.acc = acc;
	}
	public double getAccOOV() {
		return accOOV;
	}
	public void setAccOOV(double accOOV) {
		this.accOOV = accOOV;
	}
	public double getAccInV() {
		return accInV;
	}


	public void setAccInV(double accInV) {
		this.accInV = accInV;
	}
	
	public EvalConllFile(){	
	}


	public void computeAccuracy(String  sourceFileName, boolean debug) throws IOException{
		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),"UTF-8"));
		File debugFileName = new File(sourceFileName+".debug");
		BufferedWriter debugWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(debugFileName),"UTF-8"));
		int goldPosCnt = 0;
		int correctPosCnt = 0;
		int goldOOVCnt = 0;
		int correctOOVCnt = 0;
		String line = "";
		while ((line = conllReader.readLine()) != null) {
			if (!line.isEmpty()) {
				if (!line.equals("-X- -X- -X- -X-")){
					String[] tokenizedLine = line.split(" ");
					String word = tokenizedLine[1];

					String goldPos = tokenizedLine[2];
					String predPos = tokenizedLine[3];
					
					goldPosCnt++;
					if (predPos.equals(goldPos)) correctPosCnt++;
					else
						if (debug) debugWriter.write(line+"\n");

					// Counting our of vocabulary words
					//TODO: note I do not lower case words when counting OOV -> correct? 
					// I guess so, because words in getWordSet() are also not lower-cased -> not sure, better try lowercase it as well
					boolean knownWord = data.getWordSet().getLabel2num().containsKey(word);
					if (!knownWord) goldOOVCnt++;
					if (!knownWord && predPos.equals(goldPos)) correctOOVCnt++;

				}
			}
		}
		conllReader.close();
		debugWriter.close();
		if (!debug) debugFileName.delete();

		// accuracy for all words of test file
		acc = (double) correctPosCnt / (double) goldPosCnt;
		// accuracy for all out of vocabulary words of test file
		accOOV  = (double) correctOOVCnt / (double) goldOOVCnt;
		// accuracy for known vocabulary words of test file
		int correctKnownWords = (goldPosCnt - goldOOVCnt);
		int correctFoundKnownWords = (correctPosCnt - correctOOVCnt);
		accInV  = (double) correctFoundKnownWords / (double) correctKnownWords;


		DecimalFormat formatter = new DecimalFormat("#0.00");

		System.out.println("All pos: " + goldPosCnt + " Correct: " + correctPosCnt + " Accuracy: " + 
				formatter.format(acc*100) +"%");
		System.out.println("All OOV pos: " + goldOOVCnt + " Correct: " + correctOOVCnt + " Accuracy: " + 
				formatter.format(accOOV*100) +"%");
		System.out.println("All InV pos: " + correctKnownWords + " Correct: " + correctFoundKnownWords + " Accuracy: " + 
				formatter.format(accInV*100) +"%");
	}


	public static void main(String[] args) throws IOException{
		// This is for testing
		// This reads saved vocabulary from training corpus
		GlobalParams.taggerName = "DEPOSMORPH";
		EvalConllFile evalFile = new EvalConllFile();
		evalFile.data.readWordSet();

		evalFile.computeAccuracy("resources/eval/tiger2_posmorph_devel.txt", true);
	}
}
