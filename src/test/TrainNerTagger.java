package test;

import java.io.IOException;

import trainer.GNTrainer;
import data.ModelInfo;
import features.WordFeatures;

public class TrainNerTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("NER");
		
		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 50;
		WordFeatures.withWordFeats=false;

		modelInfo.createModelFileName(dim, numberOfSentences);
		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize);
		String trainingFileName = "resources/data/ner/eng-train";

		gnTrainer.gntTrainingWithDimensionFromConllFile(trainingFileName, dim, numberOfSentences);

	}
}
