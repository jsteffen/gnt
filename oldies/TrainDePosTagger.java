package test;

import java.io.IOException;

import trainer.GNTrainer;
import data.GNTProperties;
import data.ModelInfo;

public class TrainDePosTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo();

		GNTProperties props = new GNTProperties("resources/props/DePosTagger.xml");

		GNTrainer gnTrainer = new GNTrainer(modelInfo, props);

		gnTrainer.gntTrainingWithDimensionFromConllFile(
				props.getTrainingFile(), props.getClusterIdNameFile(), ModelInfo.dim, ModelInfo.numberOfSentences);

	}
}
