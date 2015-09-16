package main;

import java.io.IOException;

import tagger.GNTagger;
import test.GNTrainer;
import data.ModelInfo;

/**
 * The main calls for training and tagging and testing with GNTagger
 * with arguments;
 * Will be the main class for the self-contained image.
 * @author gune00
 *
 */

/*
 * arguments:
 * 
 * -mode train -w <window size> -d <dimension> -s <number of sentences> -m <model info type> -f <filename>
 * -mode test -w <window size> -d <dimension> -s <number of sentences> -m <model info type> -f <filename test> -e <evalFileName>
 */
public class GNT {
	private String mode = "train";
	private String windowSize = "2";
	private String dimension = "50";
	private String sentences = "100000";
	private String modelInfoType = "GNT";
	private String inFile = "";
	private String outFile = "";



	private void errorMessageAndExit(){
		System.err.println("No arguments specified. Run either");
		System.err.println("-mode train -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> -f <training file name in conll format>"
				+ "\nor ...");
		System.err.println("-mode test -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> -f <test file name in conll format> -e <merged output file for evaluation>");
		// Exit with error !
		System.exit(1);
	}

	private void setArgValues(String[] args) {
		if (args.length == 0) 
			errorMessageAndExit();
		else
			if ((args.length == 12)){
				this.initWith12arguments(args);
			}
			else
				if ((args.length == 14)){
					this.initWith14arguments(args);
				}
				else
					errorMessageAndExit();

	}

	// -mode train -w <window size> -d <dimension> -s <number of sentences> -m <model info type> -f <filename>
	private void initWith12arguments(String[] args) {
		if (args[0].equalsIgnoreCase("-mode"))this.mode = args[1];
		if (args[2].equalsIgnoreCase("-w"))this.windowSize= args[3];
		if (args[4].equalsIgnoreCase("-d"))this.dimension = args[5];
		if (args[6].equalsIgnoreCase("-s"))this.sentences = args[7];
		if (args[8].equalsIgnoreCase("-m"))this.modelInfoType = args[9];
		if (args[10].equalsIgnoreCase("-f"))this.inFile = args[11];

	}

	// -mode test -w <window size> -d <dimension> -s <number of sentences> -m <model info type> -f <filename test> -e <evalFileName>
	private void initWith14arguments(String[] args) {
		if (args[0].equalsIgnoreCase("-mode"))this.mode = args[1];
		if (args[2].equalsIgnoreCase("-w"))this.windowSize= args[3];
		if (args[4].equalsIgnoreCase("-d"))this.dimension = args[5];
		if (args[6].equalsIgnoreCase("-s"))this.sentences = args[7];
		if (args[8].equalsIgnoreCase("-m"))this.modelInfoType = args[9];
		if (args[10].equalsIgnoreCase("-f"))this.inFile = args[11];
		if (args[12].equalsIgnoreCase("-e"))this.outFile = args[13];
	}

	public String toString (){
		String output = "";
		
		output += "-mode "+ this.mode ;
		output += " -w "+ this.windowSize ;
		output += " -d "+ this.dimension ;
		output += " -s "+ this.sentences ;
		output += " -m "+ this.modelInfoType ;
		output += " -f "+ this.inFile ;
		if (this.mode.equalsIgnoreCase("test"))
			output += " -e "+ this.outFile ;

		return output;

	}
	private void runGNTrainer(String[] args) throws IOException {
		System.out.println("Run GNTrainer: ");
		System.out.println(this.toString());

		ModelInfo modelInfo = new ModelInfo(this.modelInfoType);

		int windowSize = Integer.valueOf(this.windowSize);
		int dim = Integer.valueOf(this.dimension);
		int numberOfSentences = Integer.valueOf(this.sentences);

		modelInfo.createModelFileName(dim, numberOfSentences);

		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize);

		gnTrainer.gntTrainingWithDimensionFromConllFile(this.inFile, dim, numberOfSentences);

	}

	private void runGNTagger(String[] args) throws IOException {
		System.out.println("Run GNTagger: ");
		System.out.println(this.toString());

		ModelInfo modelInfo = new ModelInfo(this.modelInfoType);

		int windowSize = Integer.valueOf(this.windowSize);
		int dim = Integer.valueOf(this.dimension);
		int numberOfSentences = Integer.valueOf(this.sentences);

		modelInfo.createModelFileName(dim, numberOfSentences);

		GNTagger posTagger = new GNTagger();

		posTagger.initGNTagger(modelInfo.getModelFile(), windowSize, dim);

		posTagger.tagAndWriteFromConllDevelFile(this.inFile, this.outFile);

	}


	public static void main(String[] args) throws IOException{
		GNT newGNT = new GNT();
		newGNT.setArgValues(args);
		if (newGNT.mode.equalsIgnoreCase("train"))
			newGNT.runGNTrainer(args);
		else
			if (newGNT.mode.equalsIgnoreCase("test"))
				newGNT.runGNTagger(args);
			else
				System.exit(1);

	}

}
