package gnt;

import java.io.IOException;

import caller.TrainTagger;

public class TestTrainPosTagger {

	public static void main(String[] args) throws IOException{
		TrainTagger gntTrainer = new TrainTagger();
		gntTrainer.trainer(
				"src/main/resources/dataProps/EnNerTagger.xml", 
				"src/main/resources/corpusProps/EnNerTagger.xml");
		//				gntTrainer.trainer(
		//						"src/main/resources/dataProps/DeNerKonvTagger.xml", 
		//						"src/main/resources/corpusProps/DeNerKonvTagger.xml");
		//		gntTrainer.trainer(
		//				"src/main/resources/dataProps/DePosTagger.xml", 
		//				"src/main/resources/corpusProps/DePosTagger.xml");
		//		gntTrainer.trainer(
		//				"src/main/resources/dataProps/EnPosTagger.xml", 
		//				"src/main/resources/corpusProps/EnPosTagger.xml");
		//		gntTrainer.trainer(
		//				"src/main/resources/dataProps/EnUniPosTagger.xml", 
		//				"src/main/resources/corpusProps/EnUniPosTagger.xml");
		//		gntTrainer.trainer(
		//				"src/main/resources/dataProps/EnUniPosTagger.xml", 
		//				"src/main/resources/corpusProps/EnUniPosTagger.xml");
	}
}
