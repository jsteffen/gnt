package caller;

import java.io.IOException;

import corpus.GNTcorpusProperties;
import trainer.GNTrainer;
import data.GNTdataProperties;
import data.GlobalParams;
import data.ModelInfo;

public class TrainTagger {

	public static void trainer(String dataConfigFileName, String corpusConfigFileName) throws IOException{
		ModelInfo modelInfo = new ModelInfo();

		GNTdataProperties dataProps = new GNTdataProperties(dataConfigFileName);
		dataProps.copyConfigFile(dataConfigFileName);
		GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
		GNTrainer gnTrainer = new GNTrainer(modelInfo, dataProps, corpusProps);
		gnTrainer.gntTrainingWithDimensionFromConllFile(
				corpusProps.getTrainingFile(), corpusProps.getClusterIdNameFile(), GlobalParams.dim, GlobalParams.numberOfSentences);
	}
}
