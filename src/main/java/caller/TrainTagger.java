package caller;

import java.io.IOException;

import trainer.GNTrainer;
import data.GNTProperties;
import data.GlobalParams;
import data.ModelInfo;

public class TrainTagger {

	public static void trainer(String configFileName) throws IOException{
		ModelInfo modelInfo = new ModelInfo();

		GNTProperties props = new GNTProperties(configFileName);
		GNTrainer gnTrainer = new GNTrainer(modelInfo, props);
		gnTrainer.gntTrainingWithDimensionFromConllFile(
				props.getTrainingFile(), props.getClusterIdNameFile(), GlobalParams.dim, GlobalParams.numberOfSentences);
	}
}
