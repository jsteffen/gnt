package caller;

import java.io.IOException;
import data.GNTProperties;
import data.ModelInfo;
import tagger.GNTagger;

public class GNTaggerStandalone {
	private ModelInfo modelInfo = null;
	private GNTProperties props = null;
	private GNTagger posTagger = null;

	public void initRunner(String configFileName) throws IOException{
		modelInfo = new ModelInfo();
		props = new GNTProperties(configFileName);
		posTagger = new GNTagger(modelInfo, props);
		posTagger.initGNTagger(ModelInfo.windowSize, ModelInfo.dim);
	}

	public void tagItRunner(String inputString) throws IOException{
		String[] tokens = inputString.split(" ");

		this.posTagger.tagUnlabeledTokens(tokens);

		String taggedString = posTagger.taggedSentenceToString();

		for (String token : taggedString.split(" ")){

			System.out.println(token);
		}
	}

}