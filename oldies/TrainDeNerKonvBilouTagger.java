package test;

import java.io.IOException;

import trainer.GNTrainer;
import trainer.TrainerInMem;
import data.Alphabet;
import data.Data;
import data.ModelInfo;
import features.WordSuffixFeatureFactory;

public class TrainDeNerKonvBilouTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("MDP");
		modelInfo.setTaggerName("DENERKONVBILOU");
		Data.wordFormIndex = 1;
		// For konvens 2014 data labels are at column 2 (outer NE labels) or 3 (inner NE label)
		Data.posTagIndex = 2;

		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 50;
		double subSamplingThreshold = 0.000000001;
		Alphabet.withWordFeats=false;
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
		String trainingFileName = "resources/data/ner/bilou/deu.konvens.train";
		// I am using same cluster ids as for DE conll
		String clusterIdSourceFileName = "/Users/gune00/data/Marmot/Word/de_marlin_cluster_1000";

		gnTrainer.gntTrainingWithDimensionFromConllFile(trainingFileName, clusterIdSourceFileName, dim, numberOfSentences);
	}
}
