package test;

import java.io.IOException;

import trainer.GNTrainer;
import trainer.TrainerInMem;
import data.ModelInfo;
import features.WordFeatures;

public class TrainNerTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("NER");

		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 50;
		double subSamplingThreshold = 0.000000001;
		WordFeatures.withWordFeats=true;
		WordFeatures.withShapeFeats=true;
		WordFeatures.withSuffixFeats=true;
		System.out.println(WordFeatures.toActiveFeatureString());
		
		TrainerInMem.debug=false;

		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize, subSamplingThreshold);
		String trainingFileName = "resources/data/ner/eng-train";

		gnTrainer.gntTrainingWithDimensionFromConllFile(trainingFileName, dim, numberOfSentences);
	}
}
