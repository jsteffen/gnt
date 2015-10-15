package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import data.Data;

public class EvalConllFile {
	public static Data data = new Data();

	
	public static void computeAccuracy(String  sourceFileName) throws IOException{
		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),"UTF-8"));
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
					//else System.out.println(line);
					
					// Counting our of vocabulary words
					//TODO: note I do not lower case words when counting OOV -> correct? 
					// I guess so, because words in getWordSet() are also not lower-cased -> not sure, better try lowercase it as well
					boolean knownWord = EvalConllFile.data.getWordSet().getLabel2num().containsKey(word);
					if (!knownWord) goldOOVCnt++;
					if (!knownWord && predPos.equals(goldPos)) correctOOVCnt++;
					
				}
			}
		}
		conllReader.close();

		// accuracy for all words of test file
		double acc = (double) correctPosCnt / (double) goldPosCnt;
		// accuracy for all out of vocabulary words of test file
		double accOOV  = (double) correctOOVCnt / (double) goldOOVCnt;
		// accuracy for known vocabulary words of test file
		int correctKnownWords = (goldPosCnt - goldOOVCnt);
		int correctFoundKnownWords = (correctPosCnt - correctOOVCnt);
		double accInV  = (double) correctFoundKnownWords / (double) correctKnownWords;
		

		DecimalFormat formatter = new DecimalFormat("#0.00");

		System.out.println("All pos: " + goldPosCnt + " Correct: " + correctPosCnt + " Accuracy: " + 
				formatter.format(acc*100) +"%");
		System.out.println("All OOV pos: " + goldOOVCnt + " Correct: " + correctOOVCnt + " Accuracy: " + 
				formatter.format(accOOV*100) +"%");
		System.out.println("All InV pos: " + correctKnownWords + " Correct: " + correctFoundKnownWords + " Accuracy: " + 
				formatter.format(accInV*100) +"%");
	}

	
	public static void main(String[] args) throws IOException{
		// This reads saved vocabulary from training corpus
		EvalConllFile.data.readWordSet();

		EvalConllFile.computeAccuracy("resources/eval/gweb-answers-dev-flors.txt");
	}
}
