package test;

import java.io.IOException;

import trainer.GNTrainer;
import data.Alphabet;
import data.ModelInfo;
import features.WordSuffixFeatureFactory;

public class TrainDePosMorphTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("MDP");
		modelInfo.setTaggerName("DEMORPH");
		
		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 0;
		double subSamplingThreshold = 0.000000001;
		Alphabet.withWordFeats=false;
		Alphabet.withShapeFeats=true;
		Alphabet.withSuffixFeats=true;
		Alphabet.withClusterFeats=true;
		System.out.println(Alphabet.toActiveFeatureString());
		
		WordSuffixFeatureFactory.ngram = false;
		
		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTrainer gnTrainer = new GNTrainer(modelInfo, windowSize, subSamplingThreshold);
		String trainingFileName = "resources/data/german/tiger2_morph_train";
		String clusterIdSourceFileName = "/Users/gune00/data/Marmot/Word/de_marlin_cluster_1000";

		gnTrainer.gntTrainingWithDimensionFromConllFile(trainingFileName, clusterIdSourceFileName, dim, numberOfSentences);

	}
}
