package test;

import java.io.IOException;

import trainer.GNTrainer;
import data.GNTProperties;
import data.ModelInfo;

public class TrainPosTagger {

	public static void main(String[] args) throws IOException{
		String configFileName = "resources/props/DeMorphTagger.xml";
		
		
		ModelInfo modelInfo = new ModelInfo();
		GNTProperties props = new GNTProperties(configFileName);
		GNTrainer gnTrainer = new GNTrainer(modelInfo, props);
		gnTrainer.gntTrainingWithDimensionFromConllFile(
				props.getTrainingFile(), props.getClusterIdNameFile(), ModelInfo.dim, ModelInfo.numberOfSentences);

	}

}
