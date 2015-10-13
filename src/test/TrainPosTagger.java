package test;

import java.io.IOException;

import trainer.GNTrainer;
import data.ModelInfo;
import features.WordFeatures;

public class TrainPosTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("POS");
		
		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 0;
		WordFeatures.withWordFeats=false;
		
		
		modelInfo.createModelFileName(dim, numberOfSentences);
		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize);
		String trainingFileName = "resources/data/english/ptb3-training";

		gnTrainer.gntTrainingWithDimensionFromConllFile(trainingFileName, dim, numberOfSentences);

	}

}
