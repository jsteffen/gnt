package gnt;

import java.io.IOException;

import caller.TrainPosTagger;

public class TestTrainPosTagger {

	public static void main(String[] args) throws IOException{
		TrainPosTagger.trainer("src/main/resources/props/EnPosTagger.xml");
	}

}
