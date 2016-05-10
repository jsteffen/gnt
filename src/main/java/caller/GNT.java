package caller;

import java.io.IOException;

import corpus.EvalConllFile;
import tagger.GNTagger;
import trainer.GNTrainer;
import data.Alphabet;
import data.GlobalParams;
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
 * -mode train|test -config <configFile>|
 * -mode train -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> -m <model info type> 
 * \ -wordFeats F|T -shapeFeats F|T -suffixFeats F|T -clusterFeats F|T -f <filename> -c <fileName>|
 * -mode test -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> -m <model info type> 
 * \ -wordFeats F|T -shapeFeats F|T -suffixFeats F|T -f <filename test> -e <evalFileName>
 */
public class GNT {
	private String mode = "train";
	private String config = "";
	private String windowSize = "2";
	private String dimension = "0";
	private String sentences = "-1";
	private String modelInfoType = "MDP";
	private String inFile = "";
	private String clusterIDfile = "";
	private String outFile = "";

	public GNT(){
		this.setDefaultValues();
	}

	private void errorMessageAndExit(){
		System.err.println("-mode train|test -config src/main/resources/props/<configFile.xml>"
				+ "\nor ...");
		System.err.println("-mode train -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> "
				+ "-wordFeats F|T -shapeFeats F|T -suffixFeats F|T -clusterFeats F|T"
				+ "-f <training file name in conll format>"
				+ "-c <clusterID file name>"
				+ "-mif F|T"
				+ "\nor ...");
		System.err.println("-mode test -tagger taggerName -w <window size> -d <dimension> -s <number of sentences> "
				+ "-m <model info type> "
				+ "-wordFeats F|T -shapeFeats F|T -suffixFeats F|T -clusterFeats F|T"
				+ "-f <test file name in conll format> "
				+ "-e <merged output file for evaluation>"
				+ "-mif F|T");
		System.err.println("Or use defaults by just calling -mode train|test ; Default values are:");
		System.err.println(this.toString());
		// Exit with error !
		System.exit(1);
	}

	private void setDefaultValues(){
		mode = "train";
		windowSize = "2";
		dimension = "0";
		sentences = "-1";
		modelInfoType = "MDP";
		inFile = "";
		clusterIDfile = "resources/cluster/marmot/en_marlin_cluster_1000";
		outFile = "";
		GlobalParams.taggerName = "ENPOS";
		Alphabet.withWordFeats = false;
		Alphabet.withShapeFeats = true;
		Alphabet.withSuffixFeats = true;
		Alphabet.withClusterFeats = true;
		GlobalParams.saveModelInputFile = false;
	}

	private void initGNTArguments(String[] args){

		for (int i=0; i < args.length;i++){
			switch (args[i]){
			case "-mode" 	: this.mode = args[i+1]; break;
			case "-config"	: this.config = args[i+1]; break;
			case "-tagger" 	: GlobalParams.taggerName = args[i+1]; break;
			case "-w" 		: this.windowSize= args[i+1]; break;
			case "-d" 		: this.dimension= args[i+1]; break;
			case "-s" 		: this.sentences= args[i+1]; break;
			case "-m" 		: this.modelInfoType= args[i+1]; break;
			case "-f" 		: this.inFile= args[i+1]; break;
			case "-c" 		: this.clusterIDfile= args[i+1]; break;
			case "-e" 		: this.outFile = args[i+1]; break;
			case "-mif"		: 
				if (args[i+1].equalsIgnoreCase("F"))
					GlobalParams.saveModelInputFile=false;
				else
					GlobalParams.saveModelInputFile=true;
				; break;
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

		output += " -mode "+ this.mode ;
		if (!this.config.isEmpty()){
			output += " -config "+ this.config ;
		}
		else
		{
			output += " -tagger "+ GlobalParams.taggerName;
			output += " -w "+ this.windowSize ;
			output += " -d "+ this.dimension ;
			output += " -s "+ this.sentences ;
			output += " -m "+ this.modelInfoType ;
			output += " -wordFeats "+ ((Alphabet.withWordFeats)?"T":"F");
			output += " -shapeFeats "+ ((Alphabet.withShapeFeats)?"T":"F");
			output += " -suffixFeats "+ ((Alphabet.withSuffixFeats)?"T":"F");
			output += " -clusterFeats "+ ((Alphabet.withClusterFeats)?"T":"F");
			output += " -f "+ this.inFile ;
			output += " -mif "+ ((GlobalParams.saveModelInputFile)?"T":"F");
			if (this.mode.equalsIgnoreCase("train"))
				output += " -c "+ this.clusterIDfile;
			if (this.mode.equalsIgnoreCase("test"))
				output += " -e "+ this.outFile ;
		}

		return output;

	}
	private void runGNTrainerInner(String[] args) throws IOException	{
		ModelInfo modelInfo = new ModelInfo(this.modelInfoType);

		GlobalParams.windowSize = Integer.valueOf(this.windowSize);
		GlobalParams.dim = Integer.valueOf(this.dimension);
		GlobalParams.numberOfSentences = Integer.valueOf(this.sentences);

		modelInfo.createModelFileName(GlobalParams.windowSize, GlobalParams.dim, GlobalParams.numberOfSentences);

		GNTrainer gnTrainer = new GNTrainer(modelInfo, GlobalParams.windowSize);

		gnTrainer.gntTrainingWithDimensionFromConllFile(this.inFile, this.clusterIDfile, GlobalParams.dim, GlobalParams.numberOfSentences);

	}

	private void runGNTaggerInner(String[] args) throws IOException	{
		ModelInfo modelInfo = new ModelInfo(this.modelInfoType);

		GlobalParams.windowSize = Integer.valueOf(this.windowSize);
		GlobalParams.dim = Integer.valueOf(this.dimension);
		GlobalParams.numberOfSentences = Integer.valueOf(this.sentences);

		modelInfo.createModelFileName(GlobalParams.windowSize, GlobalParams.dim, GlobalParams.numberOfSentences);

		GNTagger posTagger = new GNTagger(modelInfo);

		posTagger.initGNTagger(GlobalParams.windowSize, GlobalParams.dim);
		EvalConllFile evalFile = new EvalConllFile();
		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: " + evalFile.getData().getWordMapFileName());
		evalFile.getData().readWordSet();
		System.out.println(evalFile.getData().toString());
		
		posTagger.tagAndWriteFromConllDevelFile(this.inFile, this.outFile, -1);
		System.out.println("Create eval file: " + this.outFile);
		evalFile.computeAccuracy(this.outFile, false);
	}

	private void runGNTrainer(String[] args) throws IOException {
		System.out.println("Run GNTrainer: ");
		System.out.println(this.toString());
		if (!this.config.isEmpty())
			TrainTagger.trainer(this.config);
		else
			this.runGNTrainerInner(args);
	}

	private void runGNTagger(String[] args) throws IOException {
		System.out.println("Run GNTagger: ");
		System.out.println(this.toString());
	
		if (!this.config.isEmpty())
			RunTagger.runner(this.config);
		else
			this.runGNTaggerInner(args);
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
