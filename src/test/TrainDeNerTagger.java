package test;

import java.io.IOException;

import trainer.GNTrainer;
import trainer.TrainerInMem;
import data.Alphabet;
import data.Data;
import data.ModelInfo;
import features.WordSuffixFeatureFactory;

public class TrainDeNerTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("DENER");
		Data.wordFormIndex = 1;
		// For conll 2003 NER data NE label is at 4 column (counted from 0)
		Data.posTagIndex = 4;

		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 50;
		double subSamplingThreshold = 0.000000001;
		Alphabet.withWordFeats=true;
		Alphabet.withShapeFeats=true;
		Alphabet.withSuffixFeats=true;
		Alphabet.withClusterFeats=true;
		System.out.println(Alphabet.toActiveFeatureString());
		
		TrainerInMem.debug=false;
		
		WordSuffixFeatureFactory.ngram = false;
		WordSuffixFeatureFactory.ngramSize = 1;

		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize, subSamplingThreshold);
		String trainingFileName = "resources/data/ner/de/deu-train";
		String clusterIdSourceFileName = "resources/data/ner/de/de_marlin_cluster_1000";

		gnTrainer.gntTrainingWithDimensionFromConllFile(trainingFileName, clusterIdSourceFileName, dim, numberOfSentences);
	}
}
