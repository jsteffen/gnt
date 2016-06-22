package gnt;

import java.io.IOException;

import caller.TrainTagger;

public class TestTrainPosTagger {

	// TODO it is NOT thread save

	public static void main(String[] args) throws IOException{
		//		TrainTagger.trainer(
		//				"src/main/resources/dataProps/EnNerTagger.xml", 
		//				"src/main/resources/corpusProps/EnNerTagger.xml");
		//		TrainTagger.trainer(
		//				"src/main/resources/dataProps/EnPosTagger.xml", 
		//				"src/main/resources/corpusProps/EnPosTagger.xml");
		//		TrainTagger.trainer(
		//				"src/main/resources/dataProps/EnUniPosTagger.xml", 
		//				"src/main/resources/corpusProps/EnUniPosTagger.xml");
				TrainTagger.trainer(
						"src/main/resources/dataProps/FrUniPosTagger.xml", 
						"src/main/resources/corpusProps/FrUniPosTagger.xml");
//		TrainTagger.trainer(
//				"src/main/resources/dataProps/EnWsjPosTagger.xml", 
//				"src/main/resources/corpusProps/EnWsjPosTagger.xml");
	}
}
