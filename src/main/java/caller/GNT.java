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
 * -mode train -dataConfig src/main/resources/dataProps/<configFile.xml> -corpusConfig src/main/resources/corpusProps/<configFile.xml> |
 * -mode test -archiveName resources/models/<archive.zip> -corpusConfig src/main/resources/corpusProps/<configFile.xml>
 */
public class GNT {
	private String mode = "train";
	private String dataConfig = "";
	private String archiveName = "";
	private String corpusConfig = "";

	public GNT(){
	}

	private void errorMessageAndExit(){
		System.err.println("-mode train -dataConfig src/main/resources/dataProps/<configFile.xml> -corpusConfig src/main/resources/corpusProps/<configFile.xml>"
				+ "\nor ...");
		System.err.println("-mode test -archiveName resources/models/<archive.zip> -corpusConfig src/main/resources/corpusProps/<configFile.xml>"
				+ "\nor ...");
		System.err.println(this.toString());
		// Exit with error !
		System.exit(1);
	}

	private void initGNTArguments(String[] args){

		for (int i=0; i < args.length;i++){
			switch (args[i]){
			case "-mode" 	: this.mode = args[i+1]; break;
			case "-dataConfig"	: this.dataConfig = args[i+1]; break;
			case "-archiveName"	: this.archiveName = args[i+1]; break;
			case "-corpusConfig"	: this.corpusConfig = args[i+1]; break;
			case "-tagger" 	: GlobalParams.taggerName = args[i+1]; break;
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
		if (!this.dataConfig.isEmpty()){
			output += " -dataConfig "+ this.dataConfig ;
			output += " -corpusConfig "+ this.corpusConfig ;
		}
		else
			if (!this.archiveName.isEmpty()){
				output += " -archiveName "+ this.archiveName ;
				output += " -corpusConfig "+ this.corpusConfig ;
			}
		return output;
	}

	private void runGNTrainer(String[] args) throws IOException {
		System.out.println("Run GNTrainer: ");
		System.out.println(this.toString());
		if (!this.dataConfig.isEmpty())
			TrainTagger.trainer(this.dataConfig, this.corpusConfig);
		else
			System.err.println("Only training GNT with config files is supported!");
	}

	private void runGNTagger(String[] args) throws IOException {
		System.out.println("Run GNTagger: ");
		System.out.println(this.toString());

		if (!this.dataConfig.isEmpty())
			RunTagger.runner(this.archiveName, this.corpusConfig);
		else
			System.err.println("Only running GNT with config files is supported!");
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
