package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class EvalConllFile {

	public static void computeAccuracy(String  sourceFileName) throws IOException{
		BufferedReader conllReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(sourceFileName),"UTF-8"));
		int goldPosCnt = 0;
		int correctPosCnt = 0;
		String line = "";
		while ((line = conllReader.readLine()) != null) {
			if (!line.isEmpty()) {
				if (!line.equals("-X- -X- -X- -X-")){
					String[] tokenizedLine = line.split(" ");
					String goldPos = tokenizedLine[2];
					String predPos = tokenizedLine[3];
					goldPosCnt++;
					if (predPos.equals(goldPos)) correctPosCnt++;
					else
						System.out.println(line);
				}
			}
		}
		conllReader.close();

		double acc = (double) correctPosCnt / (double) goldPosCnt;

		DecimalFormat formatter = new DecimalFormat("#0.00");

		System.out.println("All pos: " + goldPosCnt + " Correct: " + correctPosCnt + " Accuracy: " + 
				formatter.format(acc*100) +"%");
	}

	
	// TODO
	// Define a method for counting accuracy of OOV - that is only words that do not occur in the labeled WSJ data
	// Fro this I need to hash the words in the training phase and load them when I compute the OOV accuarcy
	public static void main(String[] args) throws IOException{

		EvalConllFile.computeAccuracy("resources/eval/gweb-answers-dev-flors.txt");
	}
}
