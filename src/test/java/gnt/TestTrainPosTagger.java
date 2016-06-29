package gnt;

import java.io.IOException;

import caller.TrainTagger;

public class TestTrainPosTagger {

	public static void main(String[] args) throws IOException{
		TrainTagger gntTrainer = new TrainTagger();
		//		TrainTagger.trainer(
		//				"src/main/resources/dataProps/EnNerTagger.xml", 
		//				"src/main/resources/corpusProps/EnNerTagger.xml");
		//		TrainTagger.trainer(
		//				"src/main/resources/dataProps/EnPosTagger.xml", 
		//				"src/main/resources/corpusProps/EnPosTagger.xml");
		//		TrainTagger.trainer(
		//				"src/main/resources/dataProps/EnUniPosTagger.xml", 
		//				"src/main/resources/corpusProps/EnUniPosTagger.xml");
		gntTrainer.trainer(
				"src/main/resources/dataProps/FrUniPosTagger.xml", 
				"src/main/resources/corpusProps/FrUniPosTagger.xml");
		gntTrainer.trainer(
				"src/main/resources/dataProps/EnWsjPosTagger.xml", 
				"src/main/resources/corpusProps/EnWsjPosTagger.xml");
	}
}
