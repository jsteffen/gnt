package main;

import java.io.IOException;

import corpus.EvalConllFile;
import tagger.GNTagger;
import trainer.GNTrainer;
import data.Alphabet;
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
 * -mode train -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> -m <model info type> 
 * \ -wordFeats F|T -shapeFeats F|T -suffixFeats F|T -clusterFeats F|T -f <filename> -c <fileName>
 * -mode test -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> -m <model info type> 
 * \ -wordFeats F|T -shapeFeats F|T -suffixFeats F|T -f <filename test> -e <evalFileName>
 */
public class GNT {
	private String mode = "train";
	private String taggerName = "POS";
	private String windowSize = "2";
	private String dimension = "50";
	private String sentences = "-1";
	private String modelInfoType = "GNT";
	private String inFile = "";
	private String clusterIDfile = "";
	private String outFile = "";

	public GNT(){
		this.setDefaultValues();
	}

	private void errorMessageAndExit(){
		System.err.println("-mode train -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> "
				+ "-wordFeats F|T -shapeFeats F|T -suffixFeats F|T -clusterFeats F|T"
				+ "-f <training file name in conll format>"
				+ "-c <clusterID file name>"
				+ "\nor ...");
		System.err.println("-mode test -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> "
				+ "-wordFeats F|T -shapeFeats F|T -suffixFeats F|T -clusterFeats F|T"
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
		sentences = "-1";
		modelInfoType = "FLORS";
		inFile = "";
		clusterIDfile = "/Users/gune00/data/Marmot/Word/en_marlin_cluster_1000";
		outFile = "";
		Alphabet.withWordFeats = true;
		Alphabet.withShapeFeats = true;
		Alphabet.withSuffixFeats = true;
		Alphabet.withClusterFeats = true;
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
			case "-c" 		: this.clusterIDfile= args[i+1]; break;
			case "-e" 		: this.outFile = args[i+1]; break;
			case "-wordFeats" : 
				if (args[i+1].equalsIgnoreCase("F"))
					Alphabet.withWordFeats=false;
				else
					Alphabet.withWordFeats=true;
				; break;
			case "-shapeFeats" : 
				if (args[i+1].equalsIgnoreCase("F"))
					Alphabet.withShapeFeats=false;
				else
					Alphabet.withShapeFeats=true;
				; break;
			case "-suffixFeats" : 
				if (args[i+1].equalsIgnoreCase("F"))
					Alphabet.withSuffixFeats=false;
				else
					Alphabet.withSuffixFeats=true;
				; break;
			case "-clusterFeats" : 
				if (args[i+1].equalsIgnoreCase("F"))
					Alphabet.withClusterFeats=false;
				else
					Alphabet.withClusterFeats=true;
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
		output += " -wordFeats "+ ((Alphabet.withWordFeats)?"T":"F");
		output += " -shapeFeats "+ ((Alphabet.withShapeFeats)?"T":"F");
		output += " -suffixFeats "+ ((Alphabet.withSuffixFeats)?"T":"F");
		output += " -clusterFeats "+ ((Alphabet.withClusterFeats)?"T":"F");
		output += " -f "+ this.inFile ;
		if (this.mode.equalsIgnoreCase("train"))
			output += " -c "+ this.clusterIDfile;
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

		gnTrainer.gntTrainingWithDimensionFromConllFile(this.inFile, this.clusterIDfile, dim, numberOfSentences);

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
		EvalConllFile evalFile = new EvalConllFile();
		evalFile.getData().readWordSet(modelInfo.getTaggerName());
		System.out.println(evalFile.getData().toString());
		posTagger.tagAndWriteFromConllDevelFile(this.inFile, this.outFile, -1);
		System.out.println("Create eval file: " + this.outFile);
		evalFile.computeAccuracy(this.outFile);
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
