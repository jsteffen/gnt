package main;

import java.io.IOException;

import corpus.EvalConllFile;
import tagger.GNTagger;
import trainer.GNTrainer;
import data.ModelInfo;
import features.WordFeatures;

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
 * -mode train -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> -m <model info type> -wordFeats F|T -shapeFeats F|T -suffixFeats F|T -f <filename>
 * -mode test -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> -m <model info type> -wordFeats F|T -shapeFeats F|T -suffixFeats F|T -f <filename test> -e <evalFileName>
 */
public class GNT {
	private String mode = "train";
	private String taggerName = "POS";
	private String windowSize = "2";
	private String dimension = "50";
	private String sentences = "100000";
	private String modelInfoType = "GNT";
	private String inFile = "";
	private String outFile = "";

	public GNT(){
		this.setDefaultValues();
	}

	private void errorMessageAndExit(){
		System.err.println("-mode train -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> "
				+ "-wordFeats F|T -shapeFeats F|T -suffixFeats F|T"
				+ "-f <training file name in conll format>"
				+ "\nor ...");
		System.err.println("-mode test -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> "
				+ "-wordFeats F|T -shapeFeats F|T -suffixFeats F|T"
				+ "-f <test file name in conll format> -e <merged output file for evaluation>");
		System.err.println("Or use defaults by just calling -mode train|test ; Default values are:");
		System.err.println(this.toString());
		// Exit with error !
		System.exit(1);
	}

	private void setDefaultValues(){
		mode = "train";
		taggerName = "POS";
		windowSize = "2";
		dimension = "500";
		sentences = "39274";
		modelInfoType = "GNT";
		inFile = "";
		outFile = "";
		WordFeatures.withWordFeats = true;
		WordFeatures.withShapeFeats = true;
		WordFeatures.withSuffixFeats = true;
	}
	
	private void initGNTArguments(String[] args){

		for (int i=0; i < args.length;i++){
			switch (args[i]){
			case "-mode" 	: this.mode = args[i+1]; break;
			case "-tagger" 	: this.taggerName = args[i+1]; break;
			case "-w" 		: this.windowSize= args[i+1]; break;
			case "-d" 		: this.dimension= args[i+1]; break;
			case "-s" 		: this.sentences= args[i+1]; break;
			case "-m" 		: this.modelInfoType= args[i+1]; break;
			case "-f" 		: this.inFile= args[i+1]; break;
			case "-e" 		: this.outFile = args[i+1]; break;
			case "-wordFeats" : 
				if (args[i+1].equalsIgnoreCase("F"))
					WordFeatures.withWordFeats=false;
				else
					WordFeatures.withWordFeats=true;
				; break;
			case "-shapeFeats" : 
				if (args[i+1].equalsIgnoreCase("F"))
					WordFeatures.withShapeFeats=false;
				else
					WordFeatures.withShapeFeats=true;
				; break;
			case "-suffixFeats" : 
				if (args[i+1].equalsIgnoreCase("F"))
					WordFeatures.withSuffixFeats=false;
				else
					WordFeatures.withSuffixFeats=true;
				; break;
			}
		}
	}

	private void setArgValues(String[] args) {
		if ((args.length == 0)) 
		{
			System.err.println("No arguments specified. Run either");
			errorMessageAndExit();
		}
		if ((args.length % 2) != 0){
			System.err.println("Not all arguments have values! Check!");
			errorMessageAndExit();
		}
		this.initGNTArguments(args);
	}

	public String toString (){
		String output = "";

		output += "-mode "+ this.mode ;
		output += "-tagger "+ this.taggerName;
		output += " -w "+ this.windowSize ;
		output += " -d "+ this.dimension ;
		output += " -s "+ this.sentences ;
		output += " -m "+ this.modelInfoType ;
		output += " -wordFeats "+ ((WordFeatures.withWordFeats)?"T":"F");
		output += " -shapeFeats "+ ((WordFeatures.withShapeFeats)?"T":"F");
		output += " -suffixFeats "+ ((WordFeatures.withSuffixFeats)?"T":"F");
		output += " -f "+ this.inFile ;
		if (this.mode.equalsIgnoreCase("test"))
			output += " -e "+ this.outFile ;

		return output;

	}
	private void runGNTrainer(String[] args) throws IOException {
		System.out.println("Run GNTrainer: ");
		System.out.println(this.toString());

		ModelInfo modelInfo = new ModelInfo(this.modelInfoType);
		modelInfo.setTaggerName(taggerName);

		int windowSize = Integer.valueOf(this.windowSize);
		int dim = Integer.valueOf(this.dimension);
		int numberOfSentences = Integer.valueOf(this.sentences);

		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);

		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize);

		gnTrainer.gntTrainingWithDimensionFromConllFile(this.inFile, dim, numberOfSentences);

	}

	private void runGNTagger(String[] args) throws IOException {
		System.out.println("Run GNTagger: ");
		System.out.println(this.toString());

		ModelInfo modelInfo = new ModelInfo(this.modelInfoType);
		modelInfo.setTaggerName(taggerName);

		int windowSize = Integer.valueOf(this.windowSize);
		int dim = Integer.valueOf(this.dimension);
		int numberOfSentences = Integer.valueOf(this.sentences);

		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);

		GNTagger posTagger = new GNTagger(modelInfo);

		posTagger.initGNTagger(windowSize, dim);
		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile.data.readWordSet();
		posTagger.tagAndWriteFromConllDevelFile(this.inFile, this.outFile, -1);
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
