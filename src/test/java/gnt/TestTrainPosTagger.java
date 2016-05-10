package gnt;

import java.io.IOException;

import caller.TrainTagger;

public class TestTrainPosTagger {

	public static void main(String[] args) throws IOException{
		TrainTagger.trainer("src/main/resources/props/DePosTagger.xml");
	}
}
