package test;

import java.io.IOException;

import trainer.GNTrainer;
import data.Alphabet;
import data.ModelInfo;
import features.WordSuffixFeatureFactory;

public class TrainEnPosTagger2 {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("MDP");
		modelInfo.setTaggerName("POS");
		
		ModelInfo.saveModelInputFile = false;
		
		ModelInfo.windowSize = 2;
		ModelInfo.numberOfSentences = -1;
		ModelInfo.dim = 0;
		ModelInfo.subSamplingThreshold = 0.000000001;
		
		Alphabet.withWordFeats=false;
		Alphabet.withShapeFeats=true;
		Alphabet.withSuffixFeats=true;
		Alphabet.withClusterFeats=true;
		System.out.println(Alphabet.toActiveFeatureString());
		
		WordSuffixFeatureFactory.ngram = false;
		
		modelInfo.createModelFileName(ModelInfo.windowSize, ModelInfo.dim, ModelInfo.numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTrainer gnTrainer = new GNTrainer(modelInfo, ModelInfo.windowSize, ModelInfo.subSamplingThreshold);
		String trainingFileName = "resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-train";
		String clusterIdSourceFileName = "/Users/gune00/data/Marmot/Word/en_marlin_cluster_1000";

		gnTrainer.gntTrainingWithDimensionFromConllFile(
				trainingFileName, clusterIdSourceFileName, ModelInfo.dim, ModelInfo.numberOfSentences);

	}

}
