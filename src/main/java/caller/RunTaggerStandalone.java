package caller;

import java.io.IOException;
import data.GNTProperties;
import data.ModelInfo;
import tagger.GNTagger;

public class RunTaggerStandalone {
	
	public static void runner(String inputString, String configFileName) throws IOException{
		ModelInfo modelInfo = new ModelInfo();
		GNTProperties props = new GNTProperties(configFileName);
		GNTagger posTagger = new GNTagger(modelInfo, props);
		posTagger.initGNTagger(ModelInfo.windowSize, ModelInfo.dim);
		
		String[] tokens = inputString.split(" ");

		posTagger.tagUnlabeledTokens(tokens);
		
		String taggedString = posTagger.taggedSentenceToString();
		
		System.out.println(taggedString);
	}

}
