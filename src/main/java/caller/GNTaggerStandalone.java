package caller;

import java.io.IOException;
import data.GlobalParams;
import data.ModelInfo;
import tagger.GNTagger;

public class GNTaggerStandalone {
	private ModelInfo modelInfo = null;
	private GNTagger posTagger = null;

	public void initRunner(String archiveName) throws IOException{
		modelInfo = new ModelInfo();
		posTagger = new GNTagger(archiveName, modelInfo);
		posTagger.initGNTagger(GlobalParams.windowSize, GlobalParams.dim);
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